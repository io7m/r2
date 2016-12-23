/// \file R2FilterEmission.frag
/// \brief Emission filter

in vec2 R2_uv;

uniform sampler2D R2_albedo_emission;
uniform sampler2D R2_glow;
uniform float R2_emission_intensity;
uniform float R2_glow_intensity;

out vec4 R2_out_rgba;

void
main (void)
{
  vec4 map_sample =
    texture (R2_albedo_emission, R2_uv);
  vec3 glow_sample =
    texture (R2_glow, R2_uv).xyz;

  vec3 albedo =
    map_sample.xyz * map_sample.w * R2_emission_intensity;
  vec3 glow =
    glow_sample.xyz * R2_glow_intensity;

  R2_out_rgba =
    vec4 (albedo + glow, 1.0);
}
