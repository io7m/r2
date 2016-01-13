#ifndef R2_DEFERRED_LIGHT_DIRECTIONAL_SPECULAR_H
#define R2_DEFERRED_LIGHT_DIRECTIONAL_SPECULAR_H

#include "R2DeferredLightShaderMain.h"

R2_deferred_light_output_t
R2_deferredLightMain()
{
  out.diffuse = vec3 (1.0, 0.0, 0.0);
  out.specular = vec3 (0.0, 0.0, 1.0);
}

#endif
