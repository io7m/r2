#ifndef R2_DEFERRED_LIGHT_SHADER_MAIN_H
#define R2_DEFERRED_LIGHT_SHADER_MAIN_H

/// \file R2DeferredLightShaderMain.h
/// \brief The main function that all deferred light shaders must implement.

#include "R2DeferredLightOutput.h"
#include "R2DeferredReconstructedSurface.h"

///
/// Calculate light values for the current light, based on the current contents
/// of the G-buffer.
///
/// @param surface Reconstructed surface data taken from the G-Buffer
///
/// @return Calculated light values

R2_deferred_light_output_t
R2_deferredLightMain (
  const R2_deferred_reconstructed_surface_t surface
);

#endif // R2_DEFERRED_LIGHT_SHADER_MAIN_H
