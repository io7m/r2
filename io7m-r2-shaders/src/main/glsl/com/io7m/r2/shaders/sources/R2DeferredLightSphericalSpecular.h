#ifndef R2_DEFERRED_LIGHT_SPHERICAL_SPECULAR_H
#define R2_DEFERRED_LIGHT_SPHERICAL_SPECULAR_H

/// \file R2DeferredLightSphericalSpecular.h
/// \brief A spherical light with Phong specular highlights

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

  vec3 diffuse =
    R2_lightSphericalDiffuseTerm(R2_light_spherical, vectors);
  vec3 specular =
    R2_lightSphericalSpecularTerm(
      R2_light_spherical,
      vectors,
      surface.specular,
      surface.specular_exponent);

  return R2_deferred_light_output_t(
    diffuse * attenuation, specular * attenuation);
}

#endif // R2_DEFERRED_LIGHT_SPHERICAL_SPECULAR_H
