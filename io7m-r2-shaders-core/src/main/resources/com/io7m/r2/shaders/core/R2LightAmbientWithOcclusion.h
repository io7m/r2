#ifndef R2_LIGHT_AMBIENT_WITH_OCCLUSION_H
#define R2_LIGHT_AMBIENT_WITH_OCCLUSION_H

/// \file R2LightAmbientWithOcclusion.h
/// \brief A trivial ambient light with mapped occlusion

#include "R2LightShaderMain.h"
#include "R2LightAmbient.h"

uniform R2_light_ambient_t R2_light_ambient;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  vec3 diffuse =
    R2_lightAmbientTerm (R2_light_ambient, surface.uv);
  vec3 specular =
    vec3 (0.0);

  return R2_light_output_t (diffuse, specular);
}

#endif // R2_LIGHT_AMBIENT_WITH_OCCLUSION_H
