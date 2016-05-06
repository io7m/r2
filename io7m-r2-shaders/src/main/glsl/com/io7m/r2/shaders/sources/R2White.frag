/// \file R2White.frag
/// \brief Mindlessly write opaque white to the first fragment output.

layout(location = 0) out vec4 R2_out;

void
main (void)
{
  R2_out = vec4 (1.0, 1.0, 1.0, 1.0);
}
