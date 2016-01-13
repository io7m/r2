#ifndef R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H
#define R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H

/// \file R2DeferredLightShaderDriverSingle.h
/// \brief A fragment shader driver for single instance lights.

#include "R2DeferredLightOutput.h"

layout(location = 0) out vec4 R2_out_diffuse;
layout(location = 1) out vec2 R2_out_specular;

void
main (void)
{
  R2_deferred_light_output_t out = R2_deferredLightMain ();
  R2_out_diffuse = vec4 (out.diffuse, 1.0);
  R2_out_specular = vec4 (out.specular, 1.0);
}

#endif // R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H
