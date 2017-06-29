#ifndef R2_DEPTH_SHADER_MAIN_H
#define R2_DEPTH_SHADER_MAIN_H

/// \file R2DepthShaderMain.h
/// \brief The main function that all depth shaders must implement.

#include <com.io7m.r2.shaders.core/R2MatricesInstance.h>
#include <com.io7m.r2.shaders.core/R2Vertex.h>
#include <com.io7m.r2.shaders.core/R2View.h>

///
/// Calculate surface values for the current surface. Implementations of this
/// function are expected to calculate a description of the current surface and
/// then return `true` if the surface fragment should not be discarded.
///
/// @param data              Surface data (typically coming from a vertex)
/// @param derived           Surface data calculated from the original vertex
/// @param view              Matrices and parameters related to the current view
/// @param matrices_instance Matrices related to the instance to which this surface belongs
///
/// @return `true` iff the depth value should not be discarded

bool
R2_depthShaderMain (
  const R2_vertex_data_t data,
  const R2_view_t view,
  const R2_matrices_instance_t matrices_instance
);

#endif // R2_DEPTH_SHADER_MAIN_H
