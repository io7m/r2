#ifndef R2_SURFACE_BASIC_H
#define R2_SURFACE_BASIC_H

/// \file R2SurfaceBasic.h
/// \brief Basic surface implementation

#include "R2SurfaceShaderMain.h"
#include "R2SurfaceBasicTypes.h"

uniform R2_basic_surface_textures_t   R2_basic_surface_textures;
uniform R2_basic_surface_parameters_t R2_basic_surface_parameters;

R2_surface_output_t
R2_deferredSurfaceMain (
  const R2_vertex_data_t data,
  const R2_surface_derived_t derived,
  const R2_surface_textures_t textures,
  const R2_view_t view,
  const R2_surface_matrices_instance_t matrices_instance)
{
  vec4 albedo_sample =
    texture (R2_basic_surface_textures.albedo, data.uv);
  vec4 surface =
    mix (R2_basic_surface_parameters.albedo_color,
         albedo_sample,
         R2_basic_surface_parameters.albedo_mix * albedo_sample.w);

  float emission_sample =
    texture (R2_basic_surface_textures.emission, data.uv).x;
  float emission =
    R2_basic_surface_parameters.emission_amount * emission_sample;

  vec3 specular_sample =
    texture (R2_basic_surface_textures.specular, data.uv).xyz;
  vec3 specular =
    specular_sample * R2_basic_surface_parameters.specular_color;

  return R2_surface_output_t (
    surface.xyz,
    emission,
    derived.normal_eye,
    specular,
    R2_basic_surface_parameters.specular_exponent
  );
}

#endif // R2_SURFACE_BASIC_H
