#ifndef R2_DEFERRED_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H
#define R2_DEFERRED_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H

/// \file R2DeferredLightDirectionalDebugConstant.h
/// \brief A trivial directional light that simply ignores the surface and applies a constant color

#include "R2DeferredLightShaderMain.h"
#include "R2LightDirectional.h"

uniform R2_light_directional_t R2_light_directional;

R2_deferred_light_output_t
R2_deferredLightMain(
  const R2_deferred_reconstructed_surface_t surface)
{
  return R2_deferred_light_output_t(
    R2_light_directional.color,
    R2_light_directional.color);
}

#endif // R2_DEFERRED_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H
