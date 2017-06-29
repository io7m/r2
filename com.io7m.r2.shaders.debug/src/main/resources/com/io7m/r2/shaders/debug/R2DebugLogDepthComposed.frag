/// \file R2DebugLogDepthComposed.frag
/// \brief Writes the logarithmic depth of the fragment to the first fragment output.

#include <com.io7m.r2.shaders.core/R2LogDepth.h>

uniform vec4 data;

layout(location = 0) out vec4 R2_out;

void
main (void)
{
  float e = R2_logDepthEncodePartial (1.0 + data.x, data.y);
  float d = R2_logDepthDecode (e, data.y);
  R2_out = vec4 (d, 0.0, 0.0, 0.0);
}
