#ifndef R2_SURFACE_BASIC_REFLECTIVE_H
#define R2_SURFACE_BASIC_REFLECTIVE_H

/// \file R2SurfaceBasicReflective.h
/// \brief Basic pseudo-reflective (environment mapped) deferred surface implementation

#include "R2SurfaceShaderMain.h"
#include "R2EnvironmentReflection.h"

/// Textures for reflections

struct R2_surface_reflective_textures_t {
  /// A right-handed cube map representing the reflected environment
  samplerCube environment;
};

/// Parameters for reflections

struct R2_surface_reflective_parameters_t {
  /// Eye-to-world matrix for transforming reflection vectors.
  mat4x4 transform_view_inverse;
  /// Mix factor in the range `[0, 1]`, where 1 indicates a fully reflective surface
  float  environment_mix;
};

uniform R2_surface_reflective_textures_t   R2_surface_reflective_textures;
uniform R2_surface_reflective_parameters_t R2_surface_reflective_parameters;

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
  vec4 albedo =
    mix (params.albedo_color, albedo_sample, params.albedo_mix * albedo_sample.w);

  vec4 environment_sample =
    R2_environmentReflection (
      R2_surface_reflective_textures.environment,
      data.position_eye.xyz,
      derived.normal_eye,
      R2_surface_reflective_parameters.transform_view_inverse
    );

  vec4 surface =
    mix (albedo, environment_sample, R2_surface_reflective_parameters.environment_mix);

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

#endif // R2_SURFACE_BASIC_REFLECTIVE_H
