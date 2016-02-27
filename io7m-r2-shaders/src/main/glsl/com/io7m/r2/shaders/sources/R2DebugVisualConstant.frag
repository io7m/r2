/// \file R2DebugVisualConstant.frag
/// \brief Write the given color to the first fragment output.

#include "R2LogDepth.h"
#include "R2SurfaceTypes.h"
#include "R2View.h"

layout(location = 0) out vec4 R2_out;

in      R2_vertex_data_t R2_vertex_data;
uniform vec4             R2_color;
uniform R2_view_t        R2_view;

void
main (void)
{
  float depth_log = R2_logDepthEncodePartial(
    R2_vertex_data.positive_eye_z,
    R2_view.depth_coefficient);

  R2_out       = R2_color;
  gl_FragDepth = depth_log;
}
