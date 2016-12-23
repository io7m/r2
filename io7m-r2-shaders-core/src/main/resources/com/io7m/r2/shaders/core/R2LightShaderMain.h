#ifndef R2_LIGHT_SHADER_MAIN_H
#define R2_LIGHT_SHADER_MAIN_H

/// \file R2LightShaderMain.h
/// \brief The main function that all deferred light shaders must implement.

#include "R2LightOutput.h"
#include "R2ReconstructedSurface.h"

///
/// Calculate light values for the current light, based on the current contents
/// of the G-buffer.
///
/// @param surface Reconstructed surface data taken from the G-Buffer
///
/// @return Calculated light values

R2_light_output_t
R2_deferredLightMain (
  const R2_reconstructed_surface_t surface
);

#endif // R2_LIGHT_SHADER_MAIN_H
