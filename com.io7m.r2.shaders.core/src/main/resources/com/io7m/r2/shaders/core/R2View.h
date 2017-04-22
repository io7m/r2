#ifndef R2_VIEW_H
#define R2_VIEW_H

/// \file R2View.h
/// \brief Types relating to the view

/// Matrices and parameters related to the view that all surface shaders will receive.

struct R2_view_t {
  /// The scene's logarithmic depth coefficient
  float  depth_coefficient;
  /// World-space to Eye-space matrix
  mat4x4 transform_view;
  /// Eye-space to Clip-space matrix
  mat4x4 transform_projection;
};

#endif // R2_VIEW_H
