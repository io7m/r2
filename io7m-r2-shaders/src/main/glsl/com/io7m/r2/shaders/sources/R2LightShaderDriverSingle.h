#ifndef R2_LIGHT_SHADER_DRIVER_SINGLE_H
#define R2_LIGHT_SHADER_DRIVER_SINGLE_H

/// \file R2LightShaderDriverSingle.h
/// \brief A fragment shader driver for single instance lights.

#include "R2LightOutput.h"
#include "R2ReconstructedSurface.h"
#include "R2GBufferInput.h"
#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2Viewport.h"
#include "R2ViewRays.h"

layout(location = 0) out vec4 R2_out_diffuse;
layout(location = 1) out vec4 R2_out_specular;

uniform R2_viewport_t      R2_light_viewport;
uniform R2_gbuffer_input_t R2_light_gbuffer;
uniform float              R2_light_depth_coefficient;
uniform R2_view_rays_t     R2_light_view_rays;

in float R2_light_volume_positive_eye_z;

void
main (void)
{
  // Rendering of light volumes is expected to occur with depth
  // writes disabled. However, it's necessary to calculate the
  // correct logarithmic depth value for each fragment of the light
  // volume in order to get correct depth testing with respect to the
  // contents of the G-Buffer.

  float depth_log = R2_logDepthEncodePartial (
    R2_light_volume_positive_eye_z,
    R2_light_depth_coefficient);

  // Reconstruct the surface
  R2_reconstructed_surface_t surface =
    R2_deferredSurfaceReconstruct(
      R2_light_gbuffer,
      R2_light_viewport,
      R2_light_view_rays,
      R2_light_depth_coefficient,
      gl_FragCoord.xy);

  // Evaluate light
  R2_light_output_t o = R2_deferredLightMain (surface);
  R2_out_diffuse      = vec4 (o.diffuse, 1.0);
  R2_out_specular     = vec4 (o.specular, 1.0);
  gl_FragDepth        = depth_log;
}

#endif // R2_LIGHT_SHADER_DRIVER_SINGLE_H
