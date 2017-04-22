/// \file R2DepthSingle.vert
/// \brief A vertex shader for single (depth-only) instances.

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2SurfaceTypes.h"
#include "R2SurfaceVertex.h"
#include "R2View.h"

out     R2_vertex_data_t               R2_vertex_data;
uniform R2_view_t                      R2_view;
uniform R2_surface_matrices_instance_t R2_surface_matrices_instance;

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

  vec2 uv        = (R2_surface_matrices_instance.transform_uv * vec3 (R2_vertex_uv, 1.0)).xy;
  vec4 tangent4  = R2_vertex_tangent4;
  vec3 tangent   = tangent4.xyz;
  vec3 normal    = R2_vertex_normal;
  vec3 bitangent = R2_normalsBitangent (normal, tangent4);

  R2_vertex_data = R2_vertex_data_t (
    position_eye,
    position_clip,
    positive_eye_z,
    uv,
    normal,
    tangent,
    bitangent
  );

  gl_Position = position_clip_log;
}
