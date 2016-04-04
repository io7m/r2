#ifndef R2_RECONSTRUCTED_SURFACE_H
#define R2_RECONSTRUCTED_SURFACE_H

/// \file R2ReconstructedSurface.h
/// \brief Surface data reconstructed from the G-Buffer

#include "R2GBufferInput.h"
#include "R2LogDepth.h"
#include "R2Normals.h"
#include "R2PositionReconstruction.h"
#include "R2Viewport.h"
#include "R2ViewRays.h"

/// Reconstructed surface data, taken from the G-Buffer

struct R2_reconstructed_surface_t {
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
/// The implementation of this function is affected by the following preprocessor
/// defines:
///
/// If `R2_RECONSTRUCT_DIFFUSE_ONLY` is defined, the function only samples those parts
/// of the geometry buffer that are required for diffuse lighting (specifically depth
/// and normals).
///
/// If `R2_RECONSTRUCT_DIFFUSE_SPECULAR_ONLY` is defined, the function only samples
/// those parts of the geometry buffer that are required for diffuse lighting
/// (specifically depth, normals, and specular).
///
/// Otherwise, the surface is fully reconstructed.
///
/// @param gbuffer         The G-Buffer that will be sampled
/// @param viewport        The current viewport
/// @param view_rays       The current view rays
/// @param screen_position The current screen position (typically `gl_FragCoord.xy`).
///
/// @return The fully reconstructed surface
///

R2_reconstructed_surface_t
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

#if defined(R2_RECONSTRUCT_DIFFUSE_ONLY) || defined(R2_RECONSTRUCT_DIFFUSE_SPECULAR_ONLY)
  vec3 albedo    = vec3 (0.0);
  float emission = 0.0;
#else
  // Sample albedo/emission
  vec4 albedo_raw =
    texture (gbuffer.albedo, screen_uv);
  vec3 albedo =
    albedo_raw.rgb;
  float emission =
    albedo_raw.a;
#endif

  // Sample normals
  vec2 normal_compressed =
    texture (gbuffer.normal, screen_uv).xy;
  vec3 normal =
    R2_normalsDecompress (normal_compressed);

#if !defined(R2_RECONSTRUCT_DIFFUSE_ONLY)
  // Sample specular
  vec4 specular_raw =
    texture (gbuffer.specular, screen_uv);
  vec3 specular_color =
    specular_raw.rgb;
  float specular_exponent =
    specular_raw.a * 256.0;
#else
  vec3 specular_color     = vec3 (0.0);
  float specular_exponent = 0.0;
#endif

  // Construct surface data
  R2_reconstructed_surface_t surface =
    R2_reconstructed_surface_t (
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

#endif // R2_RECONSTRUCTED_SURFACE_H
