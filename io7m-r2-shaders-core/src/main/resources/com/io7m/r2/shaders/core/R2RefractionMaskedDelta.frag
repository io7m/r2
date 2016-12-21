/// \file R2RefractionMaskedDelta.frag
/// \brief Apply masked delta refraction

#include "R2LogDepth.h"
#include "R2Refraction.h"
#include "R2SurfaceTypes.h"
#include "R2View.h"
#include "R2Viewport.h"

layout(location = 0) out vec4 R2_out;

in R2_vertex_data_t R2_vertex_data;

uniform R2_refraction_t R2_refraction;
uniform sampler2D       R2_refraction_delta;
uniform R2_view_t       R2_view;
uniform R2_viewport_t   R2_viewport;

void
main (void)
{
  float depth_log = R2_logDepthEncodePartial(
    R2_vertex_data.positive_eye_z, R2_view.depth_coefficient);
  vec3 delta_unsigned =
    texture (R2_refraction_delta, R2_vertex_data.uv).xyz;
  vec3 delta =
    (delta_unsigned * 2.0) - 1.0;

  R2_out = R2_refractionMasked(
    R2_refraction, R2_vertex_data.position_clip, delta);
  gl_FragDepth = depth_log;
}
