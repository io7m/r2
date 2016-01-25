#ifndef R2_DEFERRED_RECONSTRUCTED_SURFACE_H
#define R2_DEFERRED_RECONSTRUCTED_SURFACE_H

/// \file R2DeferredReconstructedSurface.h
/// \brief Surface data reconstructed from the G-Buffer

#include "R2GBufferInput.h"
#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2PositionReconstruction.h"
#include "R2Viewport.h"
#include "R2ViewRays.h"

/// Reconstructed surface data, taken from the G-Buffer

struct R2_deferred_reconstructed_surface_t {
  /// The sampled surface albedo
  vec3 albedo;
  /// The sampled surface emission level
  float emission;

  /// The sampled surface specular color
  vec3 specular;
  /// The sampled surface specular exponent in the range `[0, 256]`
  float specular_exponent;

  /// The eye-space normal vector of the surface
  vec3 normal;
  /// The eye-space position of the surface
  vec4 position;
  /// The UV coordinates of the current screen fragment
  vec2 uv;
};

///
/// Perform a full reconstruction of the surface in the given G-Buffer.
///
/// @param gbuffer         The G-Buffer that will be sampled
/// @param viewport        The current viewport
/// @param view_rays       The current view rays
/// @param screen_position The current screen position (typically `gl_FragCoord.xy`).
///
/// @return The fully reconstructed surface
///

R2_deferred_reconstructed_surface_t
R2_deferredSurfaceReconstruct(
  const R2_gbuffer_input_t gbuffer,
  const R2_viewport_t viewport,
  const R2_view_rays_t view_rays,
  const float depth_coefficient,
  const vec2 screen_position)
{
  // Get the current screen coordinates in UV coordinate form
  vec2 screen_uv =
    R2_viewportFragmentPositionToUV (viewport, screen_position);

  // Reconstruct the eye-space Z from the depth texture
  float log_depth =
    texture (gbuffer.depth, screen_uv).x;
  float eye_z_positive =
    R2_logDepthDecode (log_depth, depth_coefficient);
  float eye_z =
    -eye_z_positive;

  // Reconstruct the full eye-space position
  vec4 position_eye =
    R2_positionReconstructFromEyeZ (eye_z, screen_uv, view_rays);

  // Sample albedo/emission
  vec4 albedo_raw =
    texture (gbuffer.albedo, screen_uv);
  vec3 albedo =
    albedo_raw.rgb;
  float emission =
    albedo_raw.a;

  // Sample normals
  vec2 normal_compressed =
    texture (gbuffer.normal, screen_uv).xy;
  vec3 normal =
    R2_normalsDecompress (normal_compressed);

  // Sample specular
  vec4 specular_raw =
    texture (gbuffer.specular, screen_uv);
  vec3 specular_color =
    specular_raw.rgb;
  float specular_exponent =
    specular_raw.a * 256.0;

  // Construct surface data
  R2_deferred_reconstructed_surface_t surface =
    R2_deferred_reconstructed_surface_t (
      albedo,
      emission,
      specular_color,
      specular_exponent,
      normal,
      position_eye,
      screen_uv
    );

  return surface;
}

#endif // R2_DEFERRED_RECONSTRUCTED_SURFACE_H
