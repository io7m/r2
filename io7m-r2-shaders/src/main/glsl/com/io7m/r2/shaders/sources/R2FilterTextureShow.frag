/// \file R2FilterTextureShow.frag
/// \brief Texture display filter

in vec2 R2_uv;

uniform sampler2D R2_texture;
uniform float     R2_intensity;

out vec4 R2_out_rgba;

void
main (void)
{
  vec4 texture_sample =
    texture (R2_texture, R2_uv);
  R2_out_rgba =
    texture_sample * R2_intensity;
}
