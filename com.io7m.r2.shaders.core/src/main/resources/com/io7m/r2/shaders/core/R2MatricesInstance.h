#ifndef R2_MATRICES_INSTANCE_H
#define R2_MATRICES_INSTANCE_H

/// \file R2MatricesInstance.h
/// \brief Instance matrices

/// Matrices related to the rendered instance that shaders will receive.

struct R2_matrices_instance_t {
  /// Object-space to Eye-space matrix
  mat4x4 transform_modelview;
  /// Object-space to Eye-space normal matrix
  mat3x3 transform_normal;
  /// UV matrix
  mat3x3 transform_uv;
};

#endif // R2_MATRICES_INSTANCE_H
