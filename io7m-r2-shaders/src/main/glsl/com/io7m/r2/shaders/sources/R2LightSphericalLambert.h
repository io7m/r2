#ifndef R2_LIGHT_SPHERICAL_LAMBERT_H
#define R2_LIGHT_SPHERICAL_LAMBERT_H

/// \file R2LightSphericalLambert.h
/// \brief A spherical light with no specular highlights

#include "R2LightShaderMain.h"
#include "R2LightPositional.h"
#include "R2LightSpherical.h"

uniform R2_light_positional_t R2_light_spherical;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  R2_light_positional_vectors_t vectors = R2_lightPositionalVectors(
    R2_light_spherical, surface.position.xyz, surface.normal);
  float attenuation = R2_lightPositionalAttenuation(
    R2_light_spherical, vectors.distance);

  vec3 diffuse =
    R2_lightSphericalDiffuseLambertTerm(R2_light_spherical, vectors);

  return R2_light_output_t(
    diffuse * attenuation, vec3 (0.0));
}

#endif // R2_LIGHT_SPHERICAL_LAMBERT_H
