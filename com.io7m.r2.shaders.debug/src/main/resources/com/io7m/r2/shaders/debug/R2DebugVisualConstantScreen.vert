/// \file R2DebugVisualConstantScreen.vert
/// \brief A vertex shader for full-screen debug geometry

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2Normals.h>
#include <com.io7m.r2.shaders.core/R2Vertex.h>
#include <com.io7m.r2.shaders.core/R2View.h>

#include <com.io7m.r2.shaders.core/R2VertexAttributes.h>

uniform R2_view_t R2_view;
uniform mat4x4    R2_transform_projection_inverse;

out R2_vertex_data_t R2_vertex_data;

void
main (void)
{
  // The input vertices are assumed to be in clip-space already.
  vec4 position_clip =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    R2_transform_projection_inverse * position_clip;
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  vec4 tangent4  = R2_vertex_tangent4;
  vec3 tangent   = tangent4.xyz;
  vec3 normal    = R2_vertex_normal;
  vec3 bitangent = R2_normalsBitangent (normal, tangent4);

  float positive_eye_z =
    R2_logDepthPrepareEyeZ (position_eye.z);

  R2_vertex_data = R2_vertex_data_t (
    position_eye,
    position_clip,
    positive_eye_z,
    R2_vertex_uv,
    normal,
    tangent,
    bitangent
  );

  gl_Position = position_clip_log;
}
