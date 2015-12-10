#ifndef R2_DEFERRED_SURFACE_VERTEX_SHADER_H
#define R2_DEFERRED_SURFACE_VERTEX_SHADER_H

#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2DeferredSurfaceShader.h"
#include "R2DeferredSurfaceVertex.h"

out     R2_deferred_surface_data_t       R2_deferred_surface_data;
uniform R2_deferred_surface_parameters_t R2_deferred_surface_parameters;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (R2_deferred_surface_parameters.transform_modelview * position_hom);
  vec4 position_clip =
    ((R2_deferred_surface_parameters.transform_projection * R2_deferred_surface_parameters.transform_modelview) * position_hom);
  vec4 position_clip_log =
    vec4 (
      position_clip.xy,
      R2_logDepthEncodeFull (position_clip.w, R2_deferred_surface_parameters.depth_coefficient),
      position_clip.w);

  float positive_eye_z = R2_logDepthPrepareEyeZ (position_eye.z);

  vec2 uv        = (R2_deferred_surface_parameters.transform_uv * vec3 (R2_vertex_uv, 1.0)).xy;
  vec4 tangent4  = R2_vertex_tangent4;
  vec3 tangent   = tangent4.xyz;
  vec3 normal    = R2_vertex_normal;
  vec3 bitangent = R2_normalsBitangent (normal, tangent4);

  R2_deferred_surface_data = R2_deferred_surface_data_t (
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

#endif // R2_DEFERRED_SURFACE_VERTEX_SHADER_H
