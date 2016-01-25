/// \file R2DeferredLightPositionalSingle.vert
/// \brief Single-instance positional light vertex shader.

#include "R2LogDepth.h"
#include "R2LightMatrices.h"

layout(location = 0) in vec3 R2_vertex_position; // Object-space position

uniform float               R2_deferred_light_depth_coefficient;
uniform R2_light_matrices_t R2_light_matrices;

out vec4  R2_light_volume_position_eye;
out float R2_light_volume_positive_eye_z;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (R2_light_matrices.transform_modelview * position_hom);
  vec4 position_clip =
    ((R2_light_matrices.transform_projection * R2_light_matrices.transform_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_deferred_light_depth_coefficient),
      position_clip.w);

  R2_light_volume_position_eye   = position_eye;
  R2_light_volume_positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);
  gl_Position                    = position_clip_log;
}
