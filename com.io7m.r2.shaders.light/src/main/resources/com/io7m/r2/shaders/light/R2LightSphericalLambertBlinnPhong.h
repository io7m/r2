#ifndef R2_LIGHT_SPHERICAL_LAMBERT_BLINN_PHONG_H
#define R2_LIGHT_SPHERICAL_LAMBERT_BLINN_PHONG_H

/// \file R2LightSphericalLambertBlinnPhong.h
/// \brief A spherical light with Blinn-Phong specular highlights

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

  vec3 diffuse =
    R2_lightSphericalDiffuseLambertTerm(R2_light_spherical, vectors);
  vec3 specular =
    R2_lightSphericalSpecularBlinnPhongTerm(
      R2_light_spherical,
      vectors,
      surface.specular,
      surface.specular_exponent);

  return R2_light_output_t(
    diffuse * attenuation, specular * attenuation);
}

#endif // R2_LIGHT_SPHERICAL_LAMBERT_BLINN_PHONG_H
