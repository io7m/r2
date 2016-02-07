/// \file R2FilterTextureShow.vert
/// \brief Full-screen filter vertex shader

/// Object-space position
layout(location = 0) in vec3 R2_vertex_position;
/// UV coordinates
layout(location = 1) in vec2 R2_vertex_uv;

out vec2 R2_uv;

void
main (void)
{
  R2_uv       = R2_vertex_uv;
  gl_Position = vec4 (R2_vertex_position, 1.0);
}
