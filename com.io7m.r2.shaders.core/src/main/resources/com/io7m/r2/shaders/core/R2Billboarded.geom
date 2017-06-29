/// \file R2Billboarded.geom
/// \brief A geometry shader for producing quads from eye-space points.

layout (points)           in;
layout (triangle_strip)   out;
layout (max_vertices = 4) out;

#include "R2LogDepth.h"
#include "R2MatricesInstance.h"
#include "R2Vertex.h"
#include "R2View.h"

uniform R2_view_t R2_view;

out R2_matrices_instance_t R2_matrices_instance;
out R2_vertex_data_t       R2_vertex_data;

in vec4 R2_billboard_scale_eye[1];

void
main (void)
{
  // Vertices are in world space, so ModelView matrix == ViewMatrix
  mat4x4 m_modelview = R2_view.transform_view;
  mat3x3 m_normal    = mat3x3 (R2_view.transform_view);
  mat3x3 m_uv        = mat3x3 (1.0); // Identity matrix

  vec4 p_eye           = gl_in[0].gl_Position;
  float positive_eye_z = R2_logDepthPrepareEyeZ (p_eye.z);
  vec2 size            = R2_billboard_scale_eye[0].xy;

  vec3 vp_normal    = vec3 (0.0, 0.0, 1.0);
  vec3 vp_tangent   = vec3 (1.0, 0.0, 0.0);
  vec3 vp_bitangent = vec3 (0.0, 1.0, 0.0);

  {
    // Bottom left
    vec4 vp_eye       = vec4 (p_eye.xy + (vec2(-0.5, -0.5) * size), p_eye.zw);
    vec4 vp_clip      = R2_view.transform_projection * vp_eye;
    vec4 vp_clip_log  = vec4 (vp_clip.xy, R2_logDepthEncodeFull (vp_clip.w, R2_view.depth_coefficient), vp_clip.w);
    vec2 vp_uv        = vec2 (0.0, 0.0);

    R2_vertex_data = R2_vertex_data_t(
      vp_eye,
      vp_clip,
      positive_eye_z,
      vp_uv,
      vp_normal,
      vp_tangent,
      vp_bitangent);

    R2_matrices_instance = R2_matrices_instance_t(
      m_modelview,
      m_normal,
      m_uv);

    gl_Position = vp_clip_log;
    EmitVertex();
  }

  {
    // Bottom right
    vec4 vp_eye       = vec4 (p_eye.xy + (vec2(0.5, -0.5) * size), p_eye.zw);
    vec4 vp_clip      = R2_view.transform_projection * vp_eye;
    vec4 vp_clip_log  = vec4 (vp_clip.xy, R2_logDepthEncodeFull (vp_clip.w, R2_view.depth_coefficient), vp_clip.w);
    vec2 vp_uv        = vec2 (1.0, 0.0);

    R2_vertex_data = R2_vertex_data_t(
      vp_eye,
      vp_clip,
      positive_eye_z,
      vp_uv,
      vp_normal,
      vp_tangent,
      vp_bitangent);

    R2_matrices_instance = R2_matrices_instance_t(
      m_modelview,
      m_normal,
      m_uv);

    gl_Position = vp_clip_log;
    EmitVertex();
  }

  {
    // Top left
    vec4 vp_eye       = vec4 (p_eye.xy + (vec2(-0.5, 0.5) * size), p_eye.zw);
    vec4 vp_clip      = R2_view.transform_projection * vp_eye;
    vec4 vp_clip_log  = vec4 (vp_clip.xy, R2_logDepthEncodeFull (vp_clip.w, R2_view.depth_coefficient), vp_clip.w);
    vec2 vp_uv        = vec2 (0.0, 1.0);

    R2_vertex_data = R2_vertex_data_t(
      vp_eye,
      vp_clip,
      positive_eye_z,
      vp_uv,
      vp_normal,
      vp_tangent,
      vp_bitangent);

    R2_matrices_instance = R2_matrices_instance_t(
      m_modelview,
      m_normal,
      m_uv);

    gl_Position = vp_clip_log;
    EmitVertex();
  }

  {
    // Top right
    vec4 vp_eye       = vec4 (p_eye.xy + (vec2(0.5, 0.5) * size), p_eye.zw);
    vec4 vp_clip      = R2_view.transform_projection * vp_eye;
    vec4 vp_clip_log  = vec4 (vp_clip.xy, R2_logDepthEncodeFull (vp_clip.w, R2_view.depth_coefficient), vp_clip.w);
    vec2 vp_uv        = vec2 (1.0, 1.0);

    R2_vertex_data = R2_vertex_data_t(
      vp_eye,
      vp_clip,
      positive_eye_z,
      vp_uv,
      vp_normal,
      vp_tangent,
      vp_bitangent);

    R2_matrices_instance = R2_matrices_instance_t(
      m_modelview,
      m_normal,
      m_uv);

    gl_Position = vp_clip_log;
    EmitVertex();
  }

  EndPrimitive();
}

