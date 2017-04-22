#ifndef R2_DEPTH_SHADER_DRIVER_BATCHED_H
#define R2_DEPTH_SHADER_DRIVER_BATCHED_H

/// \file R2DepthShaderDriverBatched.h
/// \brief A fragment depth shader driver for batched instances.

#include "R2DepthTypes.h"
#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2SurfaceTypes.h"
#include "R2GBufferOutput.h"
#include "R2View.h"

in      R2_vertex_data_t               R2_vertex_data;
in      R2_surface_matrices_instance_t R2_surface_matrices_instance;
uniform R2_view_t                      R2_view;

layout(location = 0) out vec2 R2_out_depth_variance;

R2_depth_shader_result_t
R2_depth_shader_main_exec()
{
  float depth_log = R2_logDepthEncodePartial (
    R2_vertex_data.positive_eye_z,
    R2_view.depth_coefficient);

  bool discarded = R2_depthShaderMain(
    R2_vertex_data,
    R2_view,
    R2_surface_matrices_instance);

  return R2_depth_shader_result_t(discarded, depth_log);
}

void
main (void)
{
  R2_depth_shader_result_t o = R2_depth_shader_main_exec();

  R2_out_depth_variance = vec2 (o.depth, o.depth * o.depth);
  gl_FragDepth          = o.depth;

  if (o.discarded) {
    discard;
  }
}

#endif // R2_DEPTH_SHADER_DRIVER_BATCHED_H
