#ifndef R2_DEFERRED_SURFACE_BASIC_H
#define R2_DEFERRED_SURFACE_BASIC_H

/// \file R2DeferredSurfaceBasic.h
/// \brief Basic deferred surface implementation

#include "R2DeferredSurfaceShaderMain.h"

R2_deferred_surface_output_t
R2_deferredSurfaceMain (
  const R2_deferred_surface_data_t data,
  const R2_deferred_surface_derived_t derived,
  const R2_deferred_surface_textures_t textures,
  const R2_deferred_surface_parameters_t params,
  const R2_deferred_surface_matrices_view_t matrices_view,
  const R2_deferred_surface_matrices_instance_t matrices_instance)
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

  return R2_deferred_surface_output_t (
    surface.xyz,
    emission,
    specular,
    params.specular_exponent
  );
}

#endif // R2_DEFERRED_SURFACE_BASIC_H
