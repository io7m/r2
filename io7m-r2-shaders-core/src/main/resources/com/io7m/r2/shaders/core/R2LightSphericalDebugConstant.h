#ifndef R2_LIGHT_SPHERICAL_DEBUG_CONSTANT_H
#define R2_LIGHT_SPHERICAL_DEBUG_CONSTANT_H

/// \file R2LightSphericalDebugConstant.h
/// \brief A spherical light that simply ignores the surface and applies a constant color

#include "R2LightShaderMain.h"
#include "R2LightPositional.h"
#include "R2LightSpherical.h"

uniform R2_light_positional_t R2_light_spherical;

R2_light_output_t
R2_deferredLightMain(
  const R2_reconstructed_surface_t surface)
{
  return R2_light_output_t(
    R2_light_spherical.color,
    R2_light_spherical.color);
}

#endif // R2_LIGHT_SPHERICAL_DEBUG_CONSTANT_H
