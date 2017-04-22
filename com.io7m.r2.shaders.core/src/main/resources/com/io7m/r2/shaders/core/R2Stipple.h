#ifndef R2_STIPPLE_H
#define R2_STIPPLE_H

/// \file R2Stipple.h
/// \brief Stippling types and functions

#include "R2Viewport.h"

/// The type of stippling parameters.

struct R2_stipple_t {
  /// A texture giving the stippling pattern. This texture is typically
  /// tiled over the whole screen.
  sampler2D  pattern;
  /// A scaling value for the noise texture UV coordinates
  vec2       pattern_uv_scale;
  /// A stippling threshold; The stippling algorithm discards pixels
  /// with a stipple values less than this threshold. 0.0 discards no pixels,
  /// 1.0 discards all pixels.
  float      threshold;
};

bool
R2_stippleRun(
  const R2_stipple_t stipple,
  const R2_viewport_t viewport,
  const vec2 frag_coord)
{
  // Tile the noise texture across the screen and sample it
  // The sample is discarded if the sampled stipple value is less than the
  // given stipple threshold.
  vec2 screen_uv =
    R2_viewportFragmentPositionToUV (viewport, frag_coord);
  vec2 noise_uv =
    screen_uv * stipple.pattern_uv_scale;
  float noise_sample =
    texture(stipple.pattern, noise_uv).r;
  return noise_sample < stipple.threshold;
}

#endif // R2_STIPPLE_H
