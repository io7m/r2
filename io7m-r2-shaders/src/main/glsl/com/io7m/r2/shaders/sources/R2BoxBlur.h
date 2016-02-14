#ifndef R2_BOX_BLUR_H
#define R2_BOX_BLUR_H

/// \file R2BoxBlur.h
/// \brief Functions for performing box blurs.

const float R2_box_blur_gauss_weight_2 = 0.0702702703;
const float R2_box_blur_gauss_offset_2 = 3.2307692308;
const float R2_box_blur_gauss_weight_1 = 0.3162162162;
const float R2_box_blur_gauss_offset_1 = 1.3846153846;
const float R2_box_blur_gauss_weight_0 = 0.2270270270;

///
/// Perform a single vertical blur sample for texel \a uv in \a t.
///
/// @param t    A texture
/// @param uv   The coordinates of the current texel
/// @param size The blur size
///

vec4
R2_box_blur_vertical_4f(
  const sampler2D t,
  const vec2 uv,
  const float size)
{
  float size_inv = 1.0 / size;
  float y;
  vec2 c;

  vec4 sum = textureLod (t, uv, 0.0) * R2_box_blur_gauss_weight_0;

  y = uv.y + (size_inv * R2_box_blur_gauss_offset_1);
  c = vec2 (uv.x, y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_1;

  y = uv.y + (size_inv * R2_box_blur_gauss_offset_2);
  c = vec2 (uv.x, y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_2;

  y = uv.y - (size_inv * R2_box_blur_gauss_offset_1);
  c = vec2 (uv.x, y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_1;

  y = uv.y - (size_inv * R2_box_blur_gauss_offset_2);
  c = vec2 (uv.x, y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_2;

  return sum;
}

///
/// Perform a single horizontal blur sample for texel \a uv in \a t.
///
/// @param t    A texture
/// @param uv   The coordinates of the current texel
/// @param size The blur size
///

vec4
R2_box_blur_horizontal_4f(
  const sampler2D t,
  const vec2 uv,
  const float size)
{
  float size_inv = 1.0 / size;
  float x;
  vec2 c;

  vec4 sum = textureLod (t, uv, 0.0) * R2_box_blur_gauss_weight_0;

  x = uv.x + (size_inv * R2_box_blur_gauss_offset_1);
  c = vec2 (x, uv.y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_1;

  x = uv.x + (size_inv * R2_box_blur_gauss_offset_2);
  c = vec2 (x, uv.y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_2;

  x = uv.x - (size_inv * R2_box_blur_gauss_offset_1);
  c = vec2 (x, uv.y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_1;

  x = uv.x - (size_inv * R2_box_blur_gauss_offset_2);
  c = vec2 (x, uv.y);
  sum += textureLod (t, c, 0.0) * R2_box_blur_gauss_weight_2;

  return sum;
}

#endif // R2_BOX_BLUR_H
