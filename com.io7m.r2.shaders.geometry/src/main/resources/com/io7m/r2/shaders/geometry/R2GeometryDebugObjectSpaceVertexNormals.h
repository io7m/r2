#ifndef R2_GEOMETRY_DEBUG_OBJECT_SPACE_VERTEX_NORMALS_H
#define R2_GEOMETRY_DEBUG_OBJECT_SPACE_VERTEX_NORMALS_H

/// \file R2GeometryDebugObjectSpaceVertexNormals.h
/// \brief Debugging surface shader for displaying object-space normals

#include <com.io7m.r2.shaders.geometry.api/R2GeometryShaderMain.h>

#include "R2GeometryBasicTypes.h"

R2_geometry_output_t
R2_geometryMain (
  const R2_vertex_data_t data,
  const R2_geometry_derived_t derived,
  const R2_geometry_textures_t textures,
  const R2_view_t view,
  const R2_matrices_instance_t matrices_instance)
{
  float emission = 1.0;

  vec3 specular =
    vec3 (0.0, 0.0, 0.0);
  vec3 normal_eye =
    matrices_instance.transform_normal * data.normal_vertex;

  return R2_geometry_output_t (
    data.normal_vertex,
    emission,
    normal_eye,
    specular,
    0.0,
    false
  );
}

#endif // R2_GEOMETRY_DEBUG_OBJECT_SPACE_VERTEX_NORMALS_H

