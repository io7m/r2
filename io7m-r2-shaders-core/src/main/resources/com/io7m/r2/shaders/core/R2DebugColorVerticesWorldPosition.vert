/// \file R2DebugWorldPositionColorVertices.vert
/// \brief A vertex shader for single instances.

#include "R2LogDepth.h"
#include "R2View.h"

/// World-space position
layout(location = 0) in vec3 R2_vertex_world_position;

/// RGBA color
layout(location = 1) in vec4 R2_vertex_color;

out vec4  R2_frag_color;
out float R2_positive_eye_z;

uniform R2_view_t R2_view;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_world_position, 1.0);
  vec4 position_eye =
    R2_view.transform_view * position_hom;
  vec4 position_clip =
    R2_view.transform_projection * position_eye;
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  R2_frag_color     = R2_vertex_color;
  R2_positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);
  gl_Position       = position_clip_log;
}
