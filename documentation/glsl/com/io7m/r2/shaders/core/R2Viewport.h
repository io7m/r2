#ifndef R2_VIEWPORT_H
#define R2_VIEWPORT_H

/// \file R2Viewport.h
/// \brief Viewport types and functions

/// The type of viewports.

struct R2_viewport_t {
  /// 1.0 / width of viewport
  float inverse_width;
  /// 1.0 / height of viewport
  float inverse_height;
};

///
/// Transform the fragment coordinate \a f_pos to UV
/// coordinates based on the given viewport \a v.
///
/// @param v     The viewport
/// @param f_pos The current fragment coordinate (in window coordinates)

vec2
R2_viewportFragmentPositionToUV(
  const R2_viewport_t v,
  const vec2 f_pos)
{
  return vec2 (
    f_pos.x * v.inverse_width,
    f_pos.y * v.inverse_height
  );
}

#endif // R2_VIEWPORT_H
