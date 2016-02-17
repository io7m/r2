/// \file R2FilterBilateralBlurDepthAwareVertical4f.frag
/// \brief Vertical RGBA bilateral depth-aware blur filter

#include "R2BilateralBlur.h"

in vec2 R2_uv;

uniform sampler2D                       R2_texture_image;
uniform sampler2D                       R2_texture_depth;
uniform R2_bilateral_blur_depth_aware_t R2_blur;

out vec4 R2_out_rgba;

void
main (void)
{
  R2_out_rgba = R2_bilateralBlurDepthAwareVertical4f(
    R2_texture_image,
    R2_texture_depth,
    R2_blur,
    R2_uv);
}
