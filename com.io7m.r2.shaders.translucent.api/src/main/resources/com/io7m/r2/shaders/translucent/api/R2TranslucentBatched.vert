/// \file R2TranslucentBatched.vert
/// \brief A vertex shader for batched instances.

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2Normals.h>
#include <com.io7m.r2.shaders.core/R2MatricesInstance.h>
#include <com.io7m.r2.shaders.core/R2View.h>
#include <com.io7m.r2.shaders.core/R2Vertex.h>

#define R2_VERTEX_ATTRIBUTES_REQUIRE_BATCHED_TRANSFORM_MODEL
#include <com.io7m.r2.shaders.core/R2VertexAttributes.h>

out R2_vertex_data_t       R2_vertex_data;
out R2_matrices_instance_t R2_matrices_instance;

uniform R2_view_t R2_view;

void
main (void)
{
  mat4x4 m_modelview =
    (R2_view.transform_view * R2_vertex_transform_model);
  // Batched transforms are guaranteed to be orthogonal
  mat3x3 m_normal =
    mat3x3 (m_modelview);
  mat3x3 m_uv =
    mat3x3 (1.0); // Identity matrix

  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (m_modelview * position_hom);
  vec4 position_clip =
    ((R2_view.transform_projection * m_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_view.depth_coefficient),
      position_clip.w);

  float positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);

  vec2 uv        = R2_vertex_uv;
  vec4 tangent4  = R2_vertex_tangent4;
  vec3 tangent   = tangent4.xyz;
  vec3 normal    = R2_vertex_normal;
  vec3 bitangent = R2_normalsBitangent (normal, tangent4);

  R2_matrices_instance = R2_matrices_instance_t (
    m_modelview,
    m_normal,
    m_uv
  );

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

