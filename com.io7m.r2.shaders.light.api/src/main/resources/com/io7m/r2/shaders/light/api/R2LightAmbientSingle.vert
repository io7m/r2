/// \file R2LightAmbientSingle.vert
/// \brief A vertex shader for full-screen single-instance ambient lights

#include <com.io7m.r2.shaders.core/R2LogDepth.h>

#include <com.io7m.r2.shaders.light.api/R2LightVertex.h>
#include <com.io7m.r2.shaders.light.api/R2LightMatrices.h>

uniform float               R2_light_depth_coefficient;
uniform R2_light_matrices_t R2_light_matrices;

out float R2_light_volume_positive_eye_z;

void
main (void)
{
  // The input vertices are assumed to be in clip-space already.
  vec4 position_clip =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    R2_light_matrices.transform_projection_inverse * position_clip;
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_light_depth_coefficient),
      position_clip.w);

  R2_light_volume_positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);
  gl_Position                    = position_clip_log;
}
