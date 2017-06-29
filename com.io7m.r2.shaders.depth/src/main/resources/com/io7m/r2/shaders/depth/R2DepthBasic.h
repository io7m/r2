#ifndef R2_DEPTH_BASIC_H
#define R2_DEPTH_BASIC_H

/// \file R2DepthBasic.h
/// \brief Basic depth-only implementation that discards fragments based on the opacity of an albedo texture and a threshold value

#include <com.io7m.r2.shaders.depth.api/R2DepthShaderMain.h>

uniform sampler2D R2_texture_albedo;
uniform float     R2_alpha_discard_threshold;

bool
R2_depthShaderMain (
  const R2_vertex_data_t data,
  const R2_view_t view,
  const R2_matrices_instance_t matrices_instance)
{
  vec4 surface = texture (R2_texture_albedo, data.uv);
  return surface.w < R2_alpha_discard_threshold;
}

#endif // R2_DEPTH_BASIC_H
