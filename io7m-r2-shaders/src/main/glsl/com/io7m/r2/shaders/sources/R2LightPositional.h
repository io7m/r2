#ifndef R2_LIGHT_POSITIONAL_H
#define R2_LIGHT_POSITIONAL_H

/// \file R2LightPositional.h
/// \brief Functions and types related to positional lighting

/// Vectors used when calculating positional lighting

struct R2_light_positional_vectors_t {
  /// Direction from observer to surface (referred to as `V` in most texts)
  vec3 observer_to_surface;
  /// Direction from surface to light source (referred to as `L` in most texts)
  vec3 surface_to_light;
  /// Direction from light source to surface (referred to as `-L` in most texts)
  vec3 light_to_surface;
  /// The surface normal (referred to as `N` in most texts)
  vec3 normal;
  /// Reflection between observer and normal (referred to as `R` in most texts)
  vec3 reflection;
  /// The distance between the surface and light source
  float distance;
};

/// A positional light type

struct R2_light_positional_t {
  /// The light color. The components are assumed to be in the range `[0, 1]`.
  vec3 color;
  /// The light intensity.
  float intensity;
  /// The eye-space light position.
  vec3 position;
  /// The inverse light range `(1.0 / range)`.
  float inverse_range;
  /// The inverse falloff value `(1.0 / falloff)`.
  float inverse_falloff;
};

/// Calculate the vectors required to calculate positional lighting.
///
/// @param light The light parameters
/// @param p     The surface position
/// @param n     The surface normal
///
/// @return A set of lighting vectors

R2_light_positional_vectors_t
R2_lightPositionalVectors (
  const R2_light_positional_t light,
  const vec3 p,
  const vec3 n)
{
  vec3 position_diff       = p - light.position;
  vec3 observer_to_surface = normalize (p);
  vec3 light_to_surface    = normalize (position_diff);
  vec3 surface_to_light    = -light_to_surface;
  float distance           = length (position_diff);
  vec3 reflection          = reflect (observer_to_surface, n);

  return R2_light_positional_vectors_t (
    observer_to_surface,
    surface_to_light,
    light_to_surface,
    n,
    reflection,
    distance
  );
}

///
/// Given a `light` at `distance` from the current point on the lit
/// surface, calculate the amount of attenuation. The returned value
/// is `1.0` for "no attenuation" and `0.0` for "fully attenuated".
///
/// @param light    The light
/// @param distance The distance from the light to the surface
///
/// @return An attenuation value in the range `[0.0, 1.0]`
///

float
R2_lightPositionalAttenuation(
  const R2_light_positional_t light,
  const float distance)
{
  float linear      = distance * light.inverse_range;
  float exponential = pow (linear, light.inverse_falloff);
  return 1.0 - clamp (exponential, 0.0, 1.0);
}

#endif // R2_LIGHT_POSITIONAL_H
