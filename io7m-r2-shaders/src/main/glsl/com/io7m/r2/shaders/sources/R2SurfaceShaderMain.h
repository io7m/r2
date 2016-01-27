#ifndef R2_SURFACE_SHADER_MAIN_H
#define R2_SURFACE_SHADER_MAIN_H

/// \file R2SurfaceShaderMain.h
/// \brief The main function that all deferred surface shaders must implement.

#include "R2SurfaceTypes.h"
#include "R2View.h"

///
/// Calculate surface values for the current surface. Implementations of this
/// function are expected to calculate a description of the current surface.
/// The description is encoded into the G-buffer by the caller.
///
/// @param data              Surface data (typically coming from a vertex)
/// @param derived           Surface data calculated from the original vertex
/// @param textures          Textures that are required by all surfaces
/// @param view              Matrices and parameters related to the current view
/// @param matrices_instance Matrices related to the instance to which this surface belongs
///
/// @return Calculated surface values

R2_surface_output_t
R2_deferredSurfaceMain (
  const R2_vertex_data_t data,
  const R2_surface_derived_t derived,
  const R2_surface_textures_t textures,
  const R2_view_t view,
  const R2_surface_matrices_instance_t matrices_instance
);

#endif // R2_SURFACE_SHADER_MAIN_H
