#ifndef R2_SURFACE_DEBUG_EYE_SPACE_VERTEX_NORMALS_H
#define R2_SURFACE_DEBUG_EYE_SPACE_VERTEX_NORMALS_H

/// \file R2SurfaceDebugEyeSpaceVertexNormals.h
/// \brief Debugging surface shader for displaying eye-space vertex normals.

#include "R2SurfaceShaderMain.h"
#include "R2SurfaceBasicTypes.h"

R2_surface_output_t
R2_deferredSurfaceMain (
  const R2_vertex_data_t data,
  const R2_surface_derived_t derived,
  const R2_surface_textures_t textures,
  const R2_view_t view,
  const R2_surface_matrices_instance_t matrices_instance)
{
  float emission = 1.0;

  vec3 specular =
    vec3 (0.0, 0.0, 0.0);
  vec3 normal_eye =
    matrices_instance.transform_normal * data.normal_vertex;

  return R2_surface_output_t (
    normal_eye,
    emission,
    normal_eye,
    specular,
    0.0,
    false
  );
}

#endif // R2_SURFACE_DEBUG_EYE_SPACE_VERTEX_NORMALS_H
