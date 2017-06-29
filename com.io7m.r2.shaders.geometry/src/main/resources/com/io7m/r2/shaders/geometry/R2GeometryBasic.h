#ifndef R2_GEOMETRY_BASIC_H
#define R2_GEOMETRY_BASIC_H

/// \file R2GeometryBasic.h
/// \brief Basic surface implementation

#include <com.io7m.r2.shaders.geometry.api/R2GeometryShaderMain.h>
#include <com.io7m.r2.shaders.geometry.api/R2GeometryOutput.h>

#include <com.io7m.r2.shaders.core/R2MatricesInstance.h>

#include "R2GeometryBasicTypes.h"

uniform R2_basic_surface_textures_t   R2_basic_surface_textures;
uniform R2_basic_surface_parameters_t R2_basic_surface_parameters;

R2_geometry_output_t
R2_geometryMain (
  const R2_vertex_data_t data,
  const R2_geometry_derived_t derived,
  const R2_geometry_textures_t textures,
  const R2_view_t view,
  const R2_matrices_instance_t matrices_instance)
{
  vec4 albedo_sample =
    texture (R2_basic_surface_textures.albedo, data.uv);
  vec4 albedo =
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

  bool discarded =
    albedo.w < R2_basic_surface_parameters.alpha_discard_threshold;

  return R2_geometry_output_t (
    albedo.xyz,
    emission,
    derived.normal_bumped,
    specular,
    R2_basic_surface_parameters.specular_exponent,
    discarded
  );
}

#endif // R2_GEOMETRY_BASIC_H

