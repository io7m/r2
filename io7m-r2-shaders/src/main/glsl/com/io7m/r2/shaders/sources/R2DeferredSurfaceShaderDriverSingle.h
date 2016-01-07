#ifndef R2_DEFERRED_SURFACE_SHADER_DRIVER_SINGLE_H
#define R2_DEFERRED_SURFACE_SHADER_DRIVER_SINGLE_H

// A fragment shader driver for single instances.

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2DeferredSurfaceTypes.h"
#include "R2GBuffer.h"

in      R2_deferred_surface_data_t              R2_deferred_surface_data;
uniform R2_deferred_surface_textures_t          R2_deferred_surface_textures;
uniform R2_deferred_surface_parameters_t        R2_deferred_surface_parameters;
uniform R2_deferred_surface_matrices_instance_t R2_deferred_surface_matrices_instance;
uniform R2_deferred_surface_matrices_view_t     R2_deferred_surface_matrices_view;

layout(location = 0) out vec4 R2_out_albedo;
layout(location = 1) out vec2 R2_out_normal;
layout(location = 2) out vec4 R2_out_specular;

R2_gbuffer_output_t
R2_deferred_surface_shader_main_gbuffer()
{
  vec3 normal = R2_normalsBump (
    R2_deferred_surface_textures.normal,
    R2_deferred_surface_matrices_instance.transform_normal,
    R2_deferred_surface_data.normal,
    R2_deferred_surface_data.tangent,
    R2_deferred_surface_data.bitangent,
    R2_deferred_surface_data.uv
  );

  vec2 normal_comp = R2_normalsCompress (normal);

  float depth_log = R2_logDepthEncodePartial (
    R2_deferred_surface_data.positive_eye_z,
    R2_deferred_surface_parameters.depth_coefficient);

  R2_deferred_surface_derived_t derived =
    R2_deferred_surface_derived_t (
      normal,
      normal_comp
    );

  R2_deferred_surface_output_t o = R2_deferred_main (
    R2_deferred_surface_data,
    derived,
    R2_deferred_surface_textures,
    R2_deferred_surface_parameters,
    R2_deferred_surface_matrices_view,
    R2_deferred_surface_matrices_instance
  );

  return R2_gbuffer_output_t(
    o.albedo,
    o.emission,
    normal_comp,
    o.specular,
    o.specular_exp / 256.0,
    depth_log
  );
}

void
main (void)
{
  R2_gbuffer_output_t o = R2_deferred_surface_shader_main_gbuffer();

  //
  // Assign all outputs.
  //

  R2_out_albedo   = vec4 (o.albedo, o.emission);
  R2_out_normal   = o.normal;
  R2_out_specular = vec4 (o.specular, o.specular_exp);
  gl_FragDepth    = o.depth;
}

#endif // R2_DEFERRED_SURFACE_SHADER_DRIVER_SINGLE_H
