/// \file R2DeferredLightDirectionalSingle.vert
/// \brief A vertex shader for full-screen single-instance directional lights

#include "R2DeferredLightVertex.h"

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);

  gl_Position = position_hom;
}
