#ifndef R2_VIEW_RAYS_H
#define R2_VIEW_RAYS_H

/// \file R2ViewRays.h
/// \brief View ray types

///
/// The type of view rays used to reconstruct positions during
/// deferred rendering. The view ray type defines the four near
/// corners of the view, and the four corresponding rays.
///

struct R2_view_rays_t {
  /// The bottom left origin
  vec3 origin_x0y0;
  /// The bottom right origin
  vec3 origin_x1y0;
  /// The top left origin
  vec3 origin_x0y1;
  /// The top right origin
  vec3 origin_x1y1;
  /// The view ray pointing out of the bottom left origin
  vec3 ray_x0y0;
  /// The view ray pointing out of the bottom right origin
  vec3 ray_x1y0;
  /// The view ray pointing out of the top left origin
  vec3 ray_x0y1;
  /// The view ray pointing out of the top right origin
  vec3 ray_x1y1;
};

#endif // R2_VIEW_RAYS_H
