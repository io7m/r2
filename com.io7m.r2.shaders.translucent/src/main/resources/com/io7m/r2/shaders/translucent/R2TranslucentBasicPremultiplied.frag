/// \file R2TranslucentBasicPremultiplied.frag
/// \brief A fragment shader that provides basic textured instances with premultiplied alpha

#include <com.io7m.r2.shaders.fog/R2Fog.h>

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2Vertex.h>
#include <com.io7m.r2.shaders.core/R2View.h>
#include <com.io7m.r2.shaders.core/R2Viewport.h>

in R2_vertex_data_t R2_vertex_data;

uniform sampler2D R2_texture_albedo;
uniform vec4      R2_color;
uniform float     R2_fade_positive_eye_z_near;
uniform float     R2_fade_positive_eye_z_far;
uniform R2_view_t R2_view;

layout(location = 0) out vec4 R2_out;

void
main (void)
{
  // Reuse the existing fog functions to fade the surface in/out based on distance
  R2_fog_t fog = R2_fog_t(
    R2_fade_positive_eye_z_near,
    R2_fade_positive_eye_z_far,
    vec3 (1.0, 1.0, 1.0));

  float opacity =
    R2_fogAmountLinear(fog, R2_vertex_data.positive_eye_z);

  vec4 texture_sample =
    texture (R2_texture_albedo, R2_vertex_data.uv);
  vec4 texture_mult =
    texture_sample * R2_color;
  vec4 surface =
    vec4 (texture_mult.xyz * opacity, texture_mult.w * opacity);

  R2_out = surface;

  gl_FragDepth = R2_logDepthEncodePartial(
    R2_vertex_data.positive_eye_z,
    R2_view.depth_coefficient);
}
