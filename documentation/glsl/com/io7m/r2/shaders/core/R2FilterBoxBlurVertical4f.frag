/// \file R2FilterBoxBlurVertical4f.frag
/// \brief Vertical RGBA box blur filter

#include "R2BoxBlur.h"

in vec2 R2_uv;

uniform sampler2D R2_texture;
uniform float     R2_blur_size;

out vec4 R2_out_rgba;

void
main (void)
{
  R2_out_rgba = R2_boxBlurVertical4f (R2_texture, R2_uv, R2_blur_size);
}
