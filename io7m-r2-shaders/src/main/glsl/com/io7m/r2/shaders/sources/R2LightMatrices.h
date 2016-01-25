#ifndef R2_LIGHT_MATRICES_H
#define R2_LIGHT_MATRICES_H

/// \file R2LightMatrices.h
/// \brief Matrices required by standard light types

/// Input matrices for light volumes

struct R2_light_matrices_t {
  /// Object-space to Eye-space matrix
  mat4x4 transform_modelview;
  /// Eye-space to Clip-space matrix
  mat4x4 transform_projection;
  /// Clip-space to eye-space matrix
  mat4x4 transform_projection_inverse;
};

#endif // R2_LIGHT_MATRICES_H
