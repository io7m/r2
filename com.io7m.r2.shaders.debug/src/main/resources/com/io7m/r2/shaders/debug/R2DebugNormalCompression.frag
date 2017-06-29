/// \file R2DebugNormalCompression.frag
/// \brief Treats the input data as a normal vector, compresses it, and writes it to the first fragment output.

#include <com.io7m.r2.shaders.core/R2Normals.h>

uniform vec4 data;

layout(location = 0) out vec4 R2_out;

void
main (void)
{
  vec3 n  = data.xyz;
  vec2 nc = R2_normalsCompress (n);
  vec3 nd = R2_normalsDecompress (nc);
  R2_out = vec4 (nd, 1.0);
}
