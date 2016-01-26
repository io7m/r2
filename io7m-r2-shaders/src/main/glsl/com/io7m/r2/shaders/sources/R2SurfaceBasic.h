#ifndef R2_SURFACE_BASIC_H
#define R2_SURFACE_BASIC_H

/// \file R2SurfaceBasic.h
/// \brief Basic surface implementation

#include "R2SurfaceShaderMain.h"

R2_surface_output_t
R2_deferredSurfaceMain (
  const R2_vertex_data_t data,
  const R2_surface_derived_t derived,
  const R2_surface_textures_t textures,
  const R2_surface_parameters_t params,
  const R2_surface_matrices_view_t matrices_view,
  const R2_surface_matrices_instance_t matrices_instance)
{
  vec4 albedo_sample =
    texture (textures.albedo, data.uv);
  vec4 surface =
    mix (params.albedo_color, albedo_sample, params.albedo_mix * albedo_sample.w);

  float emission =
    params.emission_amount * texture (textures.emission, data.uv).x;

  vec3 specular_sample =
    texture (textures.specular, data.uv).xyz;
  vec3 specular =
    specular_sample * params.specular_color;

  return R2_surface_output_t (
    surface.xyz,
    emission,
    derived.normal_eye,
    specular,
    params.specular_exponent
  );
}

#endif // R2_SURFACE_BASIC_H
