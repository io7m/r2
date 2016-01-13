#ifndef R2_LIGHT_DIRECTIONAL_H
#define R2_LIGHT_DIRECTIONAL_H

/// \file R2LightDirectional.h
/// \brief Functions and types related to directional lighting

#include "R2Light.h"

/// Vectors used when calculating directional lighting

struct R2_light_directional_vectors_t {
  /// Direction from observer to surface (referred to as `V` in most texts)
  vec3 observer_to_surface;
  /// Direction from surface to light source (referred to as `L` in most texts)
  vec3 surface_to_light;
  /// The surface normal (referred to as `N` in most texts)
  vec3 normal;
  /// Reflection between observer and normal (referred to as `R` in most texts)
  vec3 reflection;
};

/// A directional light type

struct R2_light_directional_t {
  /// The light color. The components are assumed to be in the range `[0, 1]`.
  vec3 color;
  /// The light direction. Assumed to be normalized.
  vec3 direction;
  /// The light intensity.
  float intensity;
};

/// Calculate the vectors required to calculate directional lighting.
///
/// @param light The light parameters
/// @param p     The surface position
/// @param n     The surface normal
///
/// @return A set of lighting vectors

R2_light_directional_vectors_t
R2_lightDirectionalVectors (
  const R2_light_directional_t light,
  const vec3 p,
  const vec3 n)
{
  vec3 observer_to_surface = normalize (p);
  vec3 surface_to_light    = normalize (-light.direction);
  vec3 reflection          = reflect (observer_to_surface, n);
  return R2_light_directional_vectors_t (observer_to_surface, surface_to_light, n, reflection);
}

/// Calculate the diffuse term for a directional light.
///
/// @param light The light parameters
/// @param v     The calculated light vectors
///
/// @return The diffuse term

vec3
R2_lightDirectionalDiffuseTerm (
  const R2_light_directional_t light,
  const R2_light_directional_vectors_t v)
{
  float factor = max (0.0, dot (v.surface_to_light, v.normal));
  return (light.color * light.intensity) * factor;
}

/// Calculate the specular term for a directional light
///
/// @param light             The light parameters
/// @param v                 The calculated light vectors
/// @param specular_color    The surface specular color
/// @param specular_exponent The surface specular exponent
///
/// @return The specular term

vec3
R2_lightDirectionalSpecularTerm (
  const R2_light_directional_t light,
  const R2_light_directional_vectors_t v,
  const vec3 specular_color,
  const float specular_exponent)
{
  float base_factor =
    max (0.0, dot (v.reflection, v.surface_to_light));
  float factor =
    pow (base_factor, specular_exponent);
  vec3 color =
    (light.color * light.intensity) * factor;
  return color * specular_color;
}

#endif
