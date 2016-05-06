#ifndef R2_LIGHT_PROJECTIVE_LAMBERT_BLINN_PHONG_SHADOW_VARIANCE_H
#define R2_LIGHT_PROJECTIVE_LAMBERT_BLINN_PHONG_SHADOW_VARIANCE_H

/// \file R2LightProjectiveLambertBlinnPhongShadowVariance.h
/// \brief A projective light with Blinn-Phong specular highlights and a variance shadow

#include "R2LightShaderMain.h"
#include "R2LightPositional.h"
#include "R2LightProjective.h"
#include "R2LightSpherical.h"
#include "R2ShadowVariance.h"

uniform R2_light_positional_t R2_light_projective;
uniform R2_shadow_variance_t  R2_shadow_variance;
uniform sampler2D             R2_light_projective_image;
uniform mat4x4                R2_transform_eye_to_light_eye;
uniform mat4x4                R2_transform_light_projection;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  R2_light_projective_vectors_t vectors = R2_lightProjectiveVectors(
    R2_light_projective,
    surface.position.xyz,
    surface.normal,
    R2_transform_eye_to_light_eye,
    R2_transform_light_projection);

  float shadow =
    R2_varianceShadowFactor (R2_shadow_variance, vectors);

  float attenuation =
    R2_lightPositionalAttenuation(
      R2_light_projective,
      vectors.positional.distance) * vectors.back_projected * shadow;

  vec3 image_sample =
    texture (R2_light_projective_image, vectors.surface_light_uv).rgb;

  vec3 diffuse =
    R2_lightSphericalDiffuseLambertTerm (R2_light_projective, vectors.positional);
  vec3 specular =
    R2_lightSphericalSpecularBlinnPhongTerm(
      R2_light_projective,
      vectors.positional,
      surface.specular,
      surface.specular_exponent);

  return R2_light_output_t(
    diffuse * image_sample * attenuation,
    specular * image_sample * attenuation);
}

#endif // R2_LIGHT_PROJECTIVE_LAMBERT_BLINN_PHONG_SHADOW_VARIANCE_H
