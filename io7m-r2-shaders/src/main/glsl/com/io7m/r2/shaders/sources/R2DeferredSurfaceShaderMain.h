#ifndef R2_DEFERRED_SURFACE_SHADER_MAIN_H
#define R2_DEFERRED_SURFACE_SHADER_MAIN_H

#include "R2DeferredSurfaceTypes.h"

//
// The main function that all deferred shaders must implement.
//

R2_deferred_surface_output_t
R2_deferred_main (
  const R2_deferred_surface_data_t data,
  const R2_deferred_surface_derived_t derived,
  const R2_deferred_surface_textures_t textures,
  const R2_deferred_surface_parameters_t params,
  const R2_deferred_surface_matrices_view_t matrices_view,
  const R2_deferred_surface_matrices_instance_t matrices_instance
);

#endif // R2_DEFERRED_SURFACE_SHADER_MAIN_H
