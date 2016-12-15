#ifndef R2_REFRACTION_H
#define R2_REFRACTION_H

/// \file R2Refraction.h
/// \brief Functions and types to render generic refraction.

struct R2_refraction_t {
  /// A scaling values for sampled refraction values
  float scale;
  /// A color by which to multiply the final refracted image
  vec3 color;
  /// An image to act as the source for refraction; typically an image of the
  /// rendered scene.
  sampler2D scene;
  /// An image to mask sampling to avoid bleeding
  sampler2D mask;
};

///
/// Calculate masked refraction using a surface normal.
///
/// @param refraction         Basic refraction parameters
/// @param position_here_clip The clip-space coordinates
/// @param delta              The delta vector
///
/// @return A refracted sample
///

vec4
R2_refractionMasked(
  const R2_refraction_t refraction,
  const vec4 position_here_clip,
  const vec3 delta)
{
  vec3 delta_scaled = delta * refraction.scale;

  vec3 position_here_ndc = vec3 (position_here_clip.xyz / position_here_clip.w);
  vec2 position_here_uv  = (position_here_ndc.xy + 1.0) * 0.5;

  vec3 position_there_ndc = position_here_ndc + delta_scaled;
  vec2 position_there_uv  = (position_there_ndc.xy + 1.0) * 0.5;

  vec4 scene_there = texture (refraction.scene, position_there_uv);
  float mask       = texture (refraction.mask,  position_there_uv).x;
  vec4 scene_here  = texture (refraction.scene, position_here_uv);

  return mix (scene_here, scene_there, mask) * vec4 (refraction.color, 1.0);
}

#endif // R2_REFRACTION_H
