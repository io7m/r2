#ifndef R2_DEFERRED_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H
#define R2_DEFERRED_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H

/// \file R2DeferredLightSphericalDebugAttenuation.h
/// \brief A spherical light that ignores everything about the surface other than the position

#include "R2DeferredLightShaderMain.h"
#include "R2LightPositional.h"
#include "R2LightSpherical.h"

uniform R2_light_positional_t R2_light_spherical;

R2_deferred_light_output_t
R2_deferredLightMain(
  const R2_deferred_reconstructed_surface_t surface)
{
  R2_light_positional_vectors_t vectors = R2_lightPositionalVectors(
    R2_light_spherical, surface.position.xyz, surface.normal);
  float attenuation = R2_lightPositionalAttenuation(
    R2_light_spherical, vectors.distance);

  return R2_deferred_light_output_t(
    R2_light_spherical.color * attenuation,
    R2_light_spherical.color * attenuation);
}

#endif // R2_DEFERRED_LIGHT_SPHERICAL_DEBUG_ATTENUATION_H
