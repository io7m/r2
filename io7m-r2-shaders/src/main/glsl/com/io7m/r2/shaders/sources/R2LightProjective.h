#ifndef R2_LIGHT_PROJECTIVE_H
#define R2_LIGHT_PROJECTIVE_H

/// \file R2LightProjective.h
/// \brief Functions and types related to projective lighting

#include "R2LightPositional.h"

/// Vectors used when calculating projective lighting

struct R2_light_projective_vectors_t {
  /// The vectors for positional lighting
  R2_light_positional_vectors_t positional;

  /// The surface position in light-eye-space
  vec4 surface_light_eye;

  /// The surface position in light-clip-space
  vec4 surface_light_clip;

  /// The surface position as NDC coordinates from the perspective of the light
  vec3 surface_light_ndc;

  /// The surface position as UV coordinates from the perspective of the light
  vec2 surface_light_uv;

  /// A value that indicates whether or not the light fragment is back projected (0.0 means back projected, 1.0 means not)
  float back_projected;
};

/// Calculate the vectors required to calculate projective lighting.
///
/// @param light               The light parameters
/// @param p                   The surface position (eye-space)
/// @param n                   The surface normal (eye-space)
/// @param m_eye_to_light_eye  A matrix to transform eye-space positions to light-eye-space
/// @param m_light_projection  The light's projection matrix
///
/// @return A set of lighting vectors

R2_light_projective_vectors_t
R2_lightProjectiveVectors(
  const R2_light_positional_t light,
  const vec3 p,
  const vec3 n,
  const mat4x4 m_eye_to_light_eye,
  const mat4x4 m_light_projection)
{
  R2_light_positional_vectors_t positional =
    R2_lightPositionalVectors (light, p, n);

  vec4 surface_hom =
    vec4 (p, 1.0);
  vec4 surface_light_eye =
    m_eye_to_light_eye * surface_hom;
  vec4 surface_light_clip =
    m_light_projection * surface_light_eye;
  vec3 surface_light_ndc =
    surface_light_clip.xyz / surface_light_clip.w;
  vec2 surface_light_uv =
    (surface_light_ndc.xy + 1.0) * 0.5;

  // Back-projection test.
  float back_projected =
    (surface_light_clip.w < 0.0) ? 0.0 : 1.0;

  return R2_light_projective_vectors_t (
    positional,
    surface_light_eye,
    surface_light_clip,
    surface_light_ndc,
    surface_light_uv,
    back_projected
  );
}

#endif // R2_LIGHT_PROJECTIVE_H
