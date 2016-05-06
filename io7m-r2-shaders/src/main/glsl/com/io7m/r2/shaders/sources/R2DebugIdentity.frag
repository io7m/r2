/// \file R2DebugIdentity.frag
/// \brief Writes a constant color to the first fragment output.

layout(location = 0) out vec4 R2_out;

uniform vec4 data;

void
main (void)
{
  R2_out = data;
}
