#ifndef R2_LIGHT_AMBIENT_H
#define R2_LIGHT_AMBIENT_H

/// \file R2LightAmbient.h
/// \brief Functions and types related to ambient lighting

/// A ambient light type

struct R2_light_ambient_t {
  /// The light color. The components are assumed to be in the range `[0, 1]`.
  vec3 color;
  /// The light intensity.
  float intensity;
  /// The occlusion map.
  sampler2D occlusion;
};

/// Calculate the "diffuse" term for a ambient light. "Diffuse" in this case
/// appears to be slight misnomer, because the light is supposed to be providing an
/// ambient term. However, the `R2` package treats ambient light as simply low
/// intensity non-directional diffuse light.
///
/// @param light The light parameters
/// @param uv    The screen position in UV coordinates (used for sampling from the occlusion map)
///
/// @return The diffuse term

vec3
R2_lightAmbientTerm (
  const R2_light_ambient_t light,
  const vec2 uv)
{
  float occ = texture (light.occlusion, uv).x;
  return light.color * (light.intensity * occ);
}

#endif
