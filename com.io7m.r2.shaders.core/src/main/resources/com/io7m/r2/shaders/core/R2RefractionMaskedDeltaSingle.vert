/// \file R2RefractionMaskedDeltaSingle.vert
/// \brief A vertex shader for refracting single instances.

#include "R2LogDepth.h"
#include "R2SurfaceTypes.h"
#include "R2SurfaceVertex.h"
#include "R2View.h"
#include "R2Viewport.h"

uniform R2_view_t                      R2_view;
uniform R2_surface_matrices_instance_t R2_surface_matrices_instance;

out R2_vertex_data_t R2_vertex_data;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (R2_surface_matrices_instance.transform_modelview * position_hom);
  vec4 position_clip =
    ((R2_view.transform_projection * R2_surface_matrices_instance.transform_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  float positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);

  vec2 uv     = (R2_surface_matrices_instance.transform_uv * vec3 (R2_vertex_uv, 1.0)).xy;
  vec3 normal = R2_vertex_normal;

  R2_vertex_data = R2_vertex_data_t (
    position_eye,
    position_clip,
    positive_eye_z,
    uv,
    normal,
    vec3(1.0, 0.0, 0.0),
    vec3(0.0, 1.0, 0.0)
  );

  gl_Position = position_clip_log;
}