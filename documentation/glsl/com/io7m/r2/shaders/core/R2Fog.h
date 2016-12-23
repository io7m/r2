#ifndef R2_FOG_H
#define R2_FOG_H

/// \file R2Fog.h
/// \brief Types related to fog rendering

/// The type of fog parameters

struct R2_fog_t {
  /// The positive eye-space Z distance that fog starts
  /// Anything closer than this distance will not have any fog applied.
  float z_near;
  /// The positive eye-space Z distance that fog ends.
  /// Anything beyond this distance will be fully fogged.
  float z_far;
  /// The fog color
  vec3 color;
};

///
/// Calculate the linear fog amount for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param z The positive eye-space Z value of the surface
///

float
R2_fogAmountLinear(
  const R2_fog_t fog,
  const float z)
{
  return clamp((z - fog.z_near) / (fog.z_far - fog.z_near), 0.0, 1.0);
}

///
/// Calculate the quadratic fog amount for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param z The positive eye-space Z value of the surface
///

float
R2_fogAmountQuadratic(
  const R2_fog_t fog,
  const float z)
{
  float linear = R2_fogAmountLinear(fog, z);
  return linear * linear;
}

///
/// Calculate the inverse quadratic fog amount for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param z The positive eye-space Z value of the surface
///

float
R2_fogAmountQuadraticInverse(
  const R2_fog_t fog,
  const float z)
{
  return sqrt(R2_fogAmountLinear(fog, z));
}

///
/// Calculate linear fog for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param surface The current surface color
/// @param z The positive eye-space Z value of the surface
///

vec3
R2_fogLinear(
  const R2_fog_t fog,
  const vec3 surface,
  const float z)
{
  return mix(surface, fog.color, R2_fogAmountLinear(fog, z));
}

///
/// Calculate quadratic fog for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param surface The current surface color
/// @param z The positive eye-space Z value of the surface
///

vec3
R2_fogQuadratic(
  const R2_fog_t fog,
  const vec3 surface,
  const float z)
{
  return mix(surface, fog.color, R2_fogAmountQuadratic(fog, z));
}

///
/// Calculate inverse quadratic fog for the given positive eye-space Z value \a z.
///
/// @param fog Fog parameters
/// @param surface The current surface color
/// @param z The positive eye-space Z value of the surface
///

vec3
R2_fogQuadraticInverse(
  const R2_fog_t fog,
  const vec3 surface,
  const float z)
{
  return mix(surface, fog.color, R2_fogAmountQuadraticInverse(fog, z));
}

#endif // R2_FOG_H
