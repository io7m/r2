/// \file R2DebugColorVertices.frag
/// \brief Write the given color to the first fragment output.

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2View.h>

layout(location = 0) out vec4 R2_out;

in      vec4      R2_frag_color;
in      float     R2_positive_eye_z;
uniform R2_view_t R2_view;

void
main (void)
{
  float depth_log = R2_logDepthEncodePartial(
    R2_positive_eye_z,
    R2_view.depth_coefficient);

  R2_out       = R2_frag_color;
  gl_FragDepth = depth_log;
}
