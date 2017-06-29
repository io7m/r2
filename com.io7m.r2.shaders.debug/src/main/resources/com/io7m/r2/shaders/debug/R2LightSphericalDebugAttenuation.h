#ifndef R2_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H
#define R2_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H

/// \file R2LightSphericalDebugAttenuation.h
/// \brief A spherical light that ignores everything about the surface other than the position

#include <com.io7m.r2.shaders.light.api/R2LightShaderMain.h>
#include <com.io7m.r2.shaders.light.api/R2LightPositional.h>
#include <com.io7m.r2.shaders.light.api/R2LightSpherical.h>

uniform R2_light_positional_t R2_light_spherical;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  R2_light_positional_vectors_t vectors = R2_lightPositionalVectors(
    R2_light_spherical, surface.position.xyz, surface.normal);
  float attenuation = R2_lightPositionalAttenuation(
    R2_light_spherical, vectors.distance);

  return R2_light_output_t(
    R2_light_spherical.color * attenuation,
    R2_light_spherical.color * attenuation);
}

#endif // R2_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H
