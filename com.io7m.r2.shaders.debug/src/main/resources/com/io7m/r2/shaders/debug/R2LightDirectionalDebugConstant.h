#ifndef R2_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H
#define R2_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H

/// \file R2LightDirectionalDebugConstant.h
/// \brief A trivial directional light that simply ignores the surface and applies a constant color

#include <com.io7m.r2.shaders.light.api/R2LightShaderMain.h>
#include <com.io7m.r2.shaders.light.api/R2LightDirectional.h>

uniform R2_light_directional_t R2_light_directional;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  return R2_light_output_t(
    R2_light_directional.color,
    R2_light_directional.color);
}

#endif // R2_LIGHT_DIRECTIONAL_DEBUG_CONSTANT_H
