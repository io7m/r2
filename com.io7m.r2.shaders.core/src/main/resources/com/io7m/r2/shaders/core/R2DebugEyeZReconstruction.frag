#ifndef R2_DEBUG_EYE_Z_RECONSTRUCTION_H
#define R2_DEBUG_EYE_Z_RECONSTRUCTION_H

/// \file R2DebugEyeZReconstruction.frag
/// \brief A fragment shader driver for reconstructing eye-space Z positions.

#include "R2GBufferInput.h"
#include "R2LogDepth.h"
#include "R2Viewport.h"

layout(location = 0) out float R2_out_z;

uniform R2_viewport_t      R2_viewport;
uniform R2_gbuffer_input_t R2_gbuffer;
uniform float              R2_depth_coefficient;

void
main (void)
{
  // Get the current screen coordinates in UV coordinate form
  vec2 screen_uv =
    R2_viewportFragmentPositionToUV (R2_viewport, gl_FragCoord.xy);

  // Reconstruct the eye-space Z from the depth texture
  float log_depth =
    texture (R2_gbuffer.depth, screen_uv).x;
  float eye_z_positive =
    R2_logDepthDecode (log_depth, R2_depth_coefficient);
  float eye_z =
    -eye_z_positive;

  R2_out_z = eye_z;
}

#endif // R2_DEBUG_EYE_Z_RECONSTRUCTION_H
