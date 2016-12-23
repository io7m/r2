#ifndef R2_FXAA_H
#define R2_FXAA_H

/// \file R2FXAA.h
/// \brief FXAA fragment shader

/// FXAA parameters

struct R2_fxaa_t {

  /// Input color texture.
  sampler2D image;

  /// The inverse width/height of the viewport
  vec2 screen_inverse;

  /// The amount of subpixel aliasing removal.
  /// The range of useful values is `[0.0, 1.0]`, where:
  ///   `1.00` - upper limit (softer)
  ///   `0.75` - default amount of filtering
  ///   `0.50` - lower limit (sharper, less sub-pixel aliasing removal)
  ///   `0.25` - almost off
  ///   `0.00` - completely off
  float subpixel_aliasing_removal;

  /// The minimum amount of local contrast required to apply the algorithm.
  /// The range of useful values is `[0.063, 0.333]`, where:
  ///   `0.333` - too little (faster)
  ///   `0.250` - low quality
  ///   `0.166` - default
  ///   `0.125` - high quality
  ///   `0.063` - overkill (slower)
  float edge_threshold;

  /// Trims the algorithm from processing darks.
  /// The range of useful values is `[0.0312, 0.0833]`, where:
  ///   `0.0833` - upper limit (default, the start of visible unfiltered edges)
  ///   `0.0625` - high quality (faster)
  ///   `0.0312` - visible limit (slower)
  float edge_threshold_minimum;
};

#ifndef R2_FXAA_PRESET
#error "Must define R2_FXAA_PRESET before including R2FXAA.h"
#endif

#define FXAA_PC 1
#define FXAA_GREEN_AS_LUMA 1
#define FXAA_GLSL_130 1
#define FXAA_QUALITY__PRESET R2_FXAA_PRESET

#include "Fxaa3_11.h"

#endif // R2_FXAA_H
