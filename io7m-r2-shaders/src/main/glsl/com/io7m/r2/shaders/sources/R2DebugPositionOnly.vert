layout(location = 0) in vec3 R2_vertex_position; // Position

void
main (void)
{
  gl_Position = vec4 (R2_vertex_position, 1.0);
}
