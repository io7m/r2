#ifndef R2_DEPTH_BASIC_STIPPLED_H
#define R2_DEPTH_BASIC_STIPPLED_H

/// \file R2DepthBasicStippled.h
/// \brief Basic depth-only implementation that discards fragments based on the opacity of an albedo texture, a threshold value, and stippling

#include <com.io7m.r2.shaders.depth.api/R2DepthShaderMain.h>
#include <com.io7m.r2.shaders.core/R2Viewport.h>
#include <com.io7m.r2.shaders.core/R2Stipple.h>

uniform sampler2D R2_texture_albedo;
uniform float     R2_alpha_discard_threshold;

uniform R2_stipple_t  R2_stipple;
uniform R2_viewport_t R2_viewport;

bool
R2_depthShaderMain (
  const R2_vertex_data_t data,
  const R2_view_t view,
  const R2_matrices_instance_t matrices_instance)
{
  vec4 surface = texture (R2_texture_albedo, data.uv);

  return R2_stippleRun(R2_stipple, R2_viewport, gl_FragCoord.xy)
    || surface.w < R2_alpha_discard_threshold;
}

#endif // R2_DEPTH_BASIC_STIPPLED_H
