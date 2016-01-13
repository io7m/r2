#ifndef R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H
#define R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H

/// \file R2DeferredLightShaderDriverSingle.h
/// \brief A fragment shader driver for single instance lights.

#include "R2DeferredLightOutput.h"
#include "R2DeferredReconstructedSurface.h"
#include "R2GBufferInput.h"
#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2Viewport.h"
#include "R2ViewRays.h"

layout(location = 0) out vec4 R2_out_diffuse;
layout(location = 1) out vec4 R2_out_specular;

uniform R2_viewport_t      R2_deferred_light_viewport;
uniform R2_gbuffer_input_t R2_deferred_light_gbuffer;
uniform float              R2_deferred_light_depth_coefficient;
uniform R2_view_rays_t     R2_deferred_light_view_rays;

void
main (void)
{
  // Reconstruct the surface
  R2_deferred_reconstructed_surface_t surface =
    R2_deferredSurfaceReconstruct(
      R2_deferred_light_gbuffer,
      R2_deferred_light_viewport,
      R2_deferred_light_view_rays,
      R2_deferred_light_depth_coefficient,
      gl_FragCoord.xy);

  // Evaluate light
  R2_deferred_light_output_t o = R2_deferredLightMain (surface);
  R2_out_diffuse = vec4 (o.diffuse, 1.0);
  R2_out_specular = vec4 (o.specular, 1.0);
}

#endif // R2_DEFERRED_LIGHT_SHADER_DRIVER_SINGLE_H
