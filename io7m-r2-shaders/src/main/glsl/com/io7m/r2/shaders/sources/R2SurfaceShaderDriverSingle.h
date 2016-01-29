#ifndef R2_SURFACE_SHADER_DRIVER_SINGLE_H
#define R2_SURFACE_SHADER_DRIVER_SINGLE_H

/// \file R2SurfaceShaderDriverSingle.h
/// \brief A fragment shader driver for single instance surfaces.

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2SurfaceTypes.h"
#include "R2GBufferOutput.h"
#include "R2View.h"

in      R2_vertex_data_t               R2_vertex_data;
uniform R2_surface_matrices_instance_t R2_surface_matrices_instance;
uniform R2_surface_textures_t          R2_surface_textures;
uniform R2_view_t                      R2_view;

layout(location = 0) out vec4 R2_out_albedo;
layout(location = 1) out vec2 R2_out_normal;
layout(location = 2) out vec4 R2_out_specular;

R2_gbuffer_output_t
R2_surface_shader_main_gbuffer()
{
  vec3 normal_bumped = R2_normalsBump (
    R2_surface_textures.normal,
    R2_surface_matrices_instance.transform_normal,
    R2_vertex_data.normal_vertex,
    R2_vertex_data.tangent,
    R2_vertex_data.bitangent,
    R2_vertex_data.uv
  );

  float depth_log = R2_logDepthEncodePartial (
    R2_vertex_data.positive_eye_z,
    R2_view.depth_coefficient);

  R2_surface_derived_t derived =
    R2_surface_derived_t (normal_bumped);

  R2_surface_output_t o = R2_deferredSurfaceMain (
    R2_vertex_data,
    derived,
    R2_surface_textures,
    R2_view,
    R2_surface_matrices_instance
  );

  return R2_gbuffer_output_t(
    o.albedo,
    o.emission,
    R2_normalsCompress (o.normal),
    o.specular,
    o.specular_exp / 256.0,
    depth_log
  );
}

void
main (void)
{
  R2_gbuffer_output_t o = R2_surface_shader_main_gbuffer();

  //
  // Assign all outputs.
  //

  R2_out_albedo   = vec4 (o.albedo, o.emission);
  R2_out_normal   = o.normal;
  R2_out_specular = vec4 (o.specular, o.specular_exp);
  gl_FragDepth    = o.depth;
}

#endif // R2_SURFACE_SHADER_DRIVER_SINGLE_H
