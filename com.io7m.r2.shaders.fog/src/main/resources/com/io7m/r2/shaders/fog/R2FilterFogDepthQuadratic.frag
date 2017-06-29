/// \file R2FilterFogDepthQuadratic.frag
/// \brief Quadratic fog filter based on depth

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2Viewport.h>
#include <com.io7m.r2.shaders.core/R2ViewRays.h>

#include "R2Fog.h"

in vec2 R2_uv;

uniform R2_fog_t  R2_fog;
uniform sampler2D R2_image_depth;
uniform sampler2D R2_image;
uniform float     R2_depth_coefficient;

layout(location = 0) out vec4 out_rgba;

void
main (void)
{
  // Reconstruct the eye-space Z from the depth texture
  float log_depth =
    texture (R2_image_depth, R2_uv).x;
  float eye_z_positive =
    R2_logDepthDecode (log_depth, R2_depth_coefficient);

  vec4 surface =
    texture (R2_image, R2_uv);

  out_rgba = vec4(R2_fogQuadratic(R2_fog, surface.xyz, eye_z_positive), surface.w);
}
