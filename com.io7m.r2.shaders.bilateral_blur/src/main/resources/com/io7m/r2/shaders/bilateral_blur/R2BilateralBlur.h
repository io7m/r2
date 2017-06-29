#ifndef R2_BILATERAL_BLUR_H
#define R2_BILATERAL_BLUR_H

/// \file R2BilateralBlur.h
/// \brief Functions for performing bilateral blurs.

#include <com.io7m.r2.shaders.core/R2LogDepth.h>

/// Parameters for the blur effect

struct R2_bilateral_blur_depth_aware_t {
  /// The depth coefficient that was used to produce the scene's depth values
  float depth_coefficient;
  /// The blur radius in texels (typically between 3 and 7)
  float radius;
  /// The blur sharpness value (typically 16)
  float sharpness;
  /// The blur falloff value
  float falloff;
  /// The inverse of the output image size: `(1 / width, 1 / height)`
  vec2  output_image_size_inverse;
};

vec4
R2_bilateralBlurDepthAwareSample4f(
  const sampler2D t,
  const sampler2D d,
  const R2_bilateral_blur_depth_aware_t blur,
  const vec2 uv,
  const float radius,
  const vec4 center_val,
  const float center_depth,
  inout float weight_total)
{
  vec4 next_val =
    textureLod (t, uv, 0.0);
  float next_depth =
    R2_logDepthDecode (textureLod (d, uv, 0.0).x, blur.depth_coefficient);

  float falloff =
    -radius * radius * blur.falloff;
  float depth_diff =
    next_depth - center_depth;
  float sharpness =
    depth_diff * depth_diff * blur.sharpness;

  float weight = exp (falloff - sharpness);
  weight_total += weight;

  return weight * next_val;
}

///
/// Perform a single depth-aware horizontal blur sample for texel \a uv in \a t.
///
/// The texture `d` is assumed to hold logarithmicaly-encoded depth samples,
/// and the values contained within will be used to determine how much each
/// sample contributes to the blur effect.
///
/// @param t    A texture
/// @param uv   The coordinates of the current texel
///

vec4
R2_bilateralBlurDepthAwareHorizontal4f(
  const sampler2D t,
  const sampler2D d,
  const R2_bilateral_blur_depth_aware_t blur,
  const vec2 uv)
{
  float weight_total = 0.0;

  vec4 center_val =
    textureLod (t, uv, 0.0);
  float center_depth =
    R2_logDepthDecode (textureLod (d, uv, 0.0).x, blur.depth_coefficient);

  vec4 sum = vec4 (0.0);
  for (float r = -blur.radius; r <= blur.radius; ++r)
  {
    vec2 uv_off =
      uv + vec2 (r * blur.output_image_size_inverse.x, 0);
    sum += R2_bilateralBlurDepthAwareSample4f(
      t, d, blur, uv_off, r, center_val, center_depth, weight_total);
  }

  return sum / weight_total;
}

///
/// Perform a single depth-aware vertical blur sample for texel \a uv in \a t.
///
/// The texture `d` is assumed to hold logarithmicaly-encoded depth samples,
/// and the values contained within will be used to determine how much each
/// sample contributes to the blur effect.
///
/// @param t    A texture
/// @param uv   The coordinates of the current texel
///

vec4
R2_bilateralBlurDepthAwareVertical4f(
  const sampler2D t,
  const sampler2D d,
  const R2_bilateral_blur_depth_aware_t blur,
  const vec2 uv)
{
  float weight_total = 0.0;

  vec4 center_val =
    textureLod (t, uv, 0.0);
  float center_depth =
    R2_logDepthDecode (textureLod (d, uv, 0.0).x, blur.depth_coefficient);

  vec4 sum = vec4 (0.0);
  for (float r = -blur.radius; r <= blur.radius; ++r)
  {
    vec2 uv_off =
      uv + vec2 (0, r * blur.output_image_size_inverse.y);
    sum += R2_bilateralBlurDepthAwareSample4f(
      t, d, blur, uv_off, r, center_val, center_depth, weight_total);
  }

  return sum / weight_total;
}

#endif // R2_BILATERAL_BLUR_H
