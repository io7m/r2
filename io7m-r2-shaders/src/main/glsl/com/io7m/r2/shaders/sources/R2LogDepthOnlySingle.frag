/// \file R2LogDepthOnlySingle.frag
/// \brief Single-instance logarithmic depth only fragment shader.

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2SurfaceTypes.h"
#include "R2View.h"

in      R2_vertex_data_t R2_vertex_data;
uniform R2_view_t        R2_view;

void
main (void)
{
  float depth_log = R2_logDepthEncodePartial (
    R2_vertex_data.positive_eye_z,
    R2_view.depth_coefficient);

  gl_FragDepth = depth_log;
}
