/// \file R2DebugPositionOnly.vert
/// \brief Assumes vertex positions are in clip space and ignores other attributes

layout(location = 0) in vec3 R2_vertex_position;

void
main (void)
{
  gl_Position = vec4 (R2_vertex_position, 1.0);
}
