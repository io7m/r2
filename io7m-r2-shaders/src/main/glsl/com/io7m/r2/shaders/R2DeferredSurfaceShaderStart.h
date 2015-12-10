#ifndef R2_DEFERRED_SURFACE_SHADER_START_H
#define R2_DEFERRED_SURFACE_SHADER_START_H

#include "R2DeferredSurfaceShader.h"

//
// The main function that all deferred shaders must implement.
//

R2_deferred_surface_output_t
R2_deferred_main (
  const R2_deferred_surface_data_t data,
  const R2_deferred_surface_textures_t textures,
  const R2_deferred_surface_parameters_t params
);

#endif
