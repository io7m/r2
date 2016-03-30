/// \file R2LogDepthOnlySingle.vert
/// \brief Single-instance logarithmic depth only vertex shader.

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2SurfaceTypes.h"
#include "R2View.h"

layout(location = 0) in vec3 R2_vertex_position; // Object-space position

out     R2_vertex_data_t R2_vertex_data;
uniform mat4x4           R2_transform_modelview;
uniform R2_view_t        R2_view;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (R2_transform_modelview * position_hom);
  vec4 position_clip =
    ((R2_view.transform_projection * R2_transform_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  float positive_eye_z =
    R2_logDepthPrepareEyeZ (position_eye.z);

  R2_vertex_data = R2_vertex_data_t (
    position_eye,
    position_clip,
    positive_eye_z,
    vec2 (0.0),
    vec3 (0.0),
    vec3 (0.0),
    vec3 (0.0)
  );

  gl_Position = position_clip_log;
}
