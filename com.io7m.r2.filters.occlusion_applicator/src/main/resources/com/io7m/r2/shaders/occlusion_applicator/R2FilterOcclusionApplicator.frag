/// \file R2FilterOcclusionApplicator.frag
/// \brief Occlusion application filter

in vec2 R2_uv;

uniform sampler2D R2_texture;
uniform float     R2_intensity;

layout (location = 0) out vec4 R2_out_diffuse;

void
main (void)
{
  vec3 occlusion_sample =
    texture (R2_texture, R2_uv).xxx;
  vec3 occlusion =
    (1.0 - occlusion_sample) * R2_intensity;
  R2_out_diffuse =
    vec4 (occlusion, 1.0);
}
