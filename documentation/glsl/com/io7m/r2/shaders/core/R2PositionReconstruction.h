#ifndef R2_POSITION_RECONSTRUCTION_H
#define R2_POSITION_RECONSTRUCTION_H

/// \file R2PositionReconstruction.h
/// \brief Functions for performing position reconstruction during deferred rendering.

#include "R2Bilinear.h"
#include "R2ViewRays.h"

///
/// Reconstruct an eye-space position from the given parameters.
///
/// @param eye_z     The eye-space Z value of the position
/// @param uv        The current position on the screen in UV coordinates
/// @param view_rays The current set of view rays
///

vec4
R2_positionReconstructFromEyeZ(
  const float eye_z,
  const vec2 uv,
  const R2_view_rays_t view_rays)
{
  vec3 origin =
    R2_bilinearInterpolate3(
      view_rays.origin_x0y0,
      view_rays.origin_x1y0,
      view_rays.origin_x0y1,
      view_rays.origin_x1y1,
      uv
    );

  vec3 ray_normal =
    R2_bilinearInterpolate3(
      view_rays.ray_x0y0,
      view_rays.ray_x1y0,
      view_rays.ray_x0y1,
      view_rays.ray_x1y1,
      uv
    );

  vec3 ray =
    (ray_normal * eye_z) + origin;

  return vec4 (ray, 1.0);
}

#endif // R2_POSITION_RECONSTRUCTION_H
