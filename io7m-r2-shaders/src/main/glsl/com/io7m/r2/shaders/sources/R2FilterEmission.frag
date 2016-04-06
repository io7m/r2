/// \file R2FilterEmission.frag
/// \brief Emission filter

in vec2 R2_uv;

uniform sampler2D R2_albedo_emission;

out vec4 R2_out_rgba;

void
main (void)
{
  vec4 texture_sample =
    texture (R2_albedo_emission, R2_uv);
  R2_out_rgba =
    vec4 (texture_sample.rgb * texture_sample.a, 1.0);
}
