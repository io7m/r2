#ifndef R2_DEFERRED_LIGHT_SHADER_MAIN_H
#define R2_DEFERRED_LIGHT_SHADER_MAIN_H

/// \file R2DeferredLightShaderMain.h
/// \brief The main function that all deferred light shaders must implement.

#include "R2DeferredLightOutput.h"

///
/// Calculate light values for the current light, based on the current contents
/// of the G-buffer.
///
/// @return Calculated light values

R2_deferred_light_output_t
R2_deferredLightMain (

);

#endif // R2_DEFERRED_LIGHT_SHADER_MAIN_H
