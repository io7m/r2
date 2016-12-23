#ifndef R2_SURFACE_BASIC_STIPPLED_H
#define R2_SURFACE_BASIC_STIPPLED_H

/// \file R2SurfaceBasicStippled.h
/// \brief Basic surface implementation

#include "R2SurfaceShaderMain.h"
#include "R2SurfaceBasicTypes.h"
#include "R2Viewport.h"
#include "R2Stipple.h"

uniform R2_basic_surface_textures_t   R2_basic_surface_textures;
uniform R2_basic_surface_parameters_t R2_basic_surface_parameters;

uniform R2_stipple_t  R2_stipple;
uniform R2_viewport_t R2_viewport;

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

  bool discarded =
    R2_stippleRun(R2_stipple, R2_viewport, gl_FragCoord.xy)
    || surface.w < R2_basic_surface_parameters.alpha_discard_threshold;

  return R2_surface_output_t (
    surface.xyz,
    emission,
    derived.normal_bumped,
    specular,
    R2_basic_surface_parameters.specular_exponent,
    discarded
  );
}

#endif // R2_SURFACE_BASIC_STIPPLED_H
