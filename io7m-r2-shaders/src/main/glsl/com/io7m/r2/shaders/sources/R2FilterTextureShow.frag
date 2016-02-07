/// \file R2FilterTextureShow.frag
/// \brief Texture display filter

in vec2 R2_uv;

uniform sampler2D R2_texture;

out vec4 R2_out_rgba;

void
main (void)
{
  vec3 texture_sample =
    texture (R2_texture, R2_uv).xyz;
  R2_out_rgba =
    vec4 (texture_sample, 1.0);
}
