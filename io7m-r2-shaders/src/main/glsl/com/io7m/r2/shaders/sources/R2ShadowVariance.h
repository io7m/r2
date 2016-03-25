#ifndef R2_SHADOW_VARIANCE_H
#define R2_SHADOW_VARIANCE_H

/// \file R2ShadowVariance.h
/// \brief Functions for variance shadows

#include "R2LogDepth.h"

/// A variance shadow
struct R2_shadow_variance_t {
  /// The minimum level of attenuation by the shadow (0.0 means "completely attenuated", 1.0 means "no attenuation")
  float factor_minimum;

  /// The minimum variance level. Used to eliminate shadow bias. Typically `0.00002f` will suffice for all scenes.
  float variance_minimum;

  /// The amount of bleed reduction (typically `[0.2, 1.0]`). Setting this value too high results in loss of detail in shadows.
  float bleed_reduction;

  /// The logarithmic depth coefficient that was used to encode depth values
  float depth_coefficient;

  /// The variance shadow map
  sampler2D map;
};

/// Compute an upper bound on the probability that the position at
/// `depth` is in shadow.
///
/// @param s       The shadow
/// @param moments The sampled shadow map depth moments
/// @param depth   The current depth
///
/// @return A value between `0.0` and `1.0`, where 1.0 indicates that the
///         area is certainly in shadow.

float
R2_varianceChebyshevUpperBound(
  const R2_shadow_variance_t s,
  const vec2 moments,
  const float depth)
{
  float p        = float (depth <= moments.x);
  float variance = max (s.variance_minimum, moments.y - (moments.x * moments.x));
  float delta    = depth - moments.x;
  float p_max    = variance / (variance + (delta * delta));
  return max (p, p_max);
}

/// A linear step function that maps all values less than `min` to `min`, all
/// values greater than `max` to `max`, and linearly scales all values in `[min, max]`
/// to `[min, max]`.
///
/// @param min The minimum value
/// @param max The maximum value
/// @param x   The value to scale
///
/// @return A linearly scaled value

float
R2_varianceLinearStep(
  const float min,
  const float max,
  const float x)
{
  float vsm = x - min;
  float msm = max - min;
  return clamp (vsm / msm, 0.0, 1.0);
}

/// Apply light bleed reduction to the given shadow probability.
///
/// @param s The shadow
/// @param p The shadow probability
///
/// @return `p` with light bleed reduction applied

float
R2_varianceLightBleedReduction(
  const R2_shadow_variance_t s,
  const float p_max)
{
  return R2_varianceLinearStep (s.bleed_reduction, 1.0, p_max);
}

/// Calculate a shadow factor for the given position.
///
/// @param s  The shadow
/// @param v  The projective light vectors, including the position in various light coordinate spaces
///
/// @return `s.factor_minimum` if the point is fully in shadow, or `1.0` if the point is
//          definitely not in shadow.

float
R2_varianceShadowFactor(
  const R2_shadow_variance_t s,
  const R2_light_projective_vectors_t v)
{
  float pos_light_depth =
    R2_logDepthEncodePartial(
      R2_logDepthPrepareEyeZ (v.surface_light_eye.z),
      s.depth_coefficient);

  // Sample the variance map for the depth distribution
  vec2 moments =
    texture (s.map, v.surface_light_uv.xy).xy;

  // Calculate the probability that the point is in shadow.
  float p_max =
    R2_varianceChebyshevUpperBound (s, moments, pos_light_depth);

  // Apply light bleeding reduction
  float p_reduced =
    R2_varianceLightBleedReduction (s, p_max);

  return max (p_reduced, s.factor_minimum);
}

#endif // R2_SHADOW_VARIANCE_H
