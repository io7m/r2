#include "R2DeferredSurfaceShaderStart.h"

//
// Basic deferred surface
//

R2_deferred_surface_output_t
R2_deferred_main (
  const R2_deferred_surface_data_t data,
  const R2_deferred_surface_textures_t textures,
  const R2_deferred_surface_parameters_t params)
{
  vec4 albedo_sample =
    texture (textures.albedo, data.uv);
  vec4 albedo =
    mix (params.albedo_color, albedo_sample, params.albedo_mix * albedo_sample.w);

  float emission =
    params.emission_amount * texture (textures.emission, data.uv).x;

  vec3 specular_sample =
    texture (textures.specular, data.uv).xyz;
  vec3 specular =
    specular_sample * params.specular_color;

  return R2_deferred_surface_output_t (
    albedo.xyz,
    emission,
    specular,
    params.specular_exponent
  );
}

#include "R2DeferredSurfaceShaderMain.h"
