/// \file R2FilterLightApplicator.frag
/// \brief Light application filter

in vec2 R2_uv;

uniform sampler2D R2_textures_albedo;
uniform sampler2D R2_textures_diffuse;
uniform sampler2D R2_textures_specular;

out vec4 R2_out_rgba;

void
main (void)
{
  vec3 albedo_sample =
    texture (R2_textures_albedo, R2_uv).xyz;
  vec3 diffuse_sample =
    texture (R2_textures_diffuse, R2_uv).xyz;
  vec3 specular_sample =
    texture (R2_textures_specular, R2_uv).xyz;

  R2_out_rgba =
    vec4 ((albedo_sample * diffuse_sample) + specular_sample, 1.0);
}
