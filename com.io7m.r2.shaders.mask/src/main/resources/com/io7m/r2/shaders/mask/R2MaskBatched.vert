/// \file R2MaskBatched.vert
/// \brief A vertex shader for masking batched instances.

#include <com.io7m.r2.shaders.core/R2LogDepth.h>
#include <com.io7m.r2.shaders.core/R2View.h>

#define R2_VERTEX_ATTRIBUTES_REQUIRE_BATCHED_TRANSFORM_MODEL
#include <com.io7m.r2.shaders.core/R2VertexAttributes.h>

uniform R2_view_t R2_view;

out float R2_positive_eye_z;

void
main (void)
{
  mat4x4 m_modelview =
    (R2_view.transform_view * R2_vertex_transform_model);

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

  R2_positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);
  gl_Position       = position_clip_log;
}
