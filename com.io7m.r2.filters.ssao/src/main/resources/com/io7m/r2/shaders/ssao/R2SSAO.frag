/// \file R2SSAO.frag
/// \brief Fragment shader for calculating an ambient occlusion term

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2Normals.h>
#include <com.io7m.r2.shaders.core/R2PositionReconstruction.h>
#include <com.io7m.r2.shaders.core/R2Viewport.h>

#include <com.io7m.r2.shaders.geometry.api/R2GBufferInput.h>

uniform R2_view_rays_t     R2_view_rays;
uniform R2_gbuffer_input_t R2_gbuffer;
uniform float              R2_depth_coefficient;

uniform mat4x4    R2_ssao_transform_projection;
uniform sampler2D R2_ssao_noise;
uniform vec2      R2_ssao_noise_uv_scale;
uniform vec3      R2_ssao_kernel[128];
uniform int       R2_ssao_kernel_size;
uniform float     R2_ssao_sample_radius;
uniform float     R2_ssao_power;

layout(location = 0) out float R2_out_occlusion;

in vec2 R2_uv;

void
main (void)
{
  vec2 screen_uv = R2_uv;

  // Reconstruct the eye-space Z from the depth texture
  float log_depth =
    texture (R2_gbuffer.depth, screen_uv).x;
  float eye_z_positive =
    R2_logDepthDecode (log_depth, R2_depth_coefficient);
  float eye_z =
    -eye_z_positive;

  // Reconstruct the full eye-space position
  vec4 eye_position =
    R2_positionReconstructFromEyeZ (eye_z, screen_uv, R2_view_rays);

  // Sample normals
  vec2 normal_compressed =
    texture (R2_gbuffer.normal, screen_uv).xy;
  vec3 normal =
    R2_normalsDecompress (normal_compressed);

  // Tile the noise texture across the screen
  vec2 noise_uv =
    screen_uv * R2_ssao_noise_uv_scale;
  // Sample a vector used to peturb the sampling hemisphere
  vec3 noise_sample =
    (texture(R2_ssao_noise, noise_uv).rgb * 2.0) - 1.0;

  // Construct a matrix used to transform the sampling hemisphere
  vec3 tangent   = normalize (noise_sample - normal * dot (noise_sample, normal));
  vec3 bitangent = cross (normal, tangent);
  mat3x3 m_hemi  = mat3x3 (tangent, bitangent, normal);

  // Calculate occlusion for the kernel
  float occlusion = 0.0;
  for (int index = 0; index < R2_ssao_kernel_size; ++index) {
    // Determine the eye-space position of the sample
    vec3 sample_eye = ((m_hemi * R2_ssao_kernel[index]) * R2_ssao_sample_radius) + eye_position.xyz;

    // Project the eye-space sample position to clip-space
    vec4 sample_hom  = vec4 (sample_eye, 1.0);
    vec4 sample_clip = R2_ssao_transform_projection * sample_hom;

    // Transform the clip-space sample position to UV coordinates
    vec2 sample_ndc = sample_clip.xy / sample_clip.w;
    vec2 sample_uv  = (sample_ndc * 0.5) + 0.5;

    // Sample the depth buffer at the sample position
    float sample_surface_depth_log =
      texture (R2_gbuffer.depth, sample_uv).r;

    // Transform logarithmic depth to linear eye-space Z
    float sample_surface_eye_z_positive =
      R2_logDepthDecode (sample_surface_depth_log, R2_depth_coefficient);
    float sample_surface_eye_z =
      -sample_surface_eye_z_positive;

    // Exclude anything from outside of the sample radius
    float sample_range_check =
      abs (eye_position.z - sample_surface_eye_z) < R2_ssao_sample_radius ? 1.0 : 0.0;

    // If the surface being sampled is in front of the sample
    // position, then the sample position is inside the geometry
    // and therefore contributes to the occlusion.
    //
    // Note that this check is dependent on coordinate system handedness:
    // Values closer to the viewer have a LARGER eye-space Z.
    occlusion += (sample_surface_eye_z >= sample_eye.z ? 1.0 : 0.0) * sample_range_check;
  }

  // Invert and normalize occlusion
  occlusion = 1.0 - (occlusion / R2_ssao_kernel_size);

  // Raise the occlusion to the given exponent, to optionally
  // add contrast.
  occlusion = pow (occlusion, R2_ssao_power);

  R2_out_occlusion = occlusion;
}
