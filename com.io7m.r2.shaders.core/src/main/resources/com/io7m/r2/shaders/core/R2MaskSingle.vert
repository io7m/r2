/// \file R2MaskSingle.vert
/// \brief A vertex shader for masking single instances.

#include "R2LogDepth.h"
#include "R2SurfaceTypes.h"
#include "R2SurfaceVertex.h"
#include "R2View.h"
#include "R2Viewport.h"

uniform R2_view_t R2_view;
uniform mat4x4    R2_transform_modelview;

out float R2_positive_eye_z;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    R2_transform_modelview * position_hom;
  vec4 position_clip =
    ((R2_view.transform_projection * R2_transform_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  R2_positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);
  gl_Position       = position_clip_log;
}
