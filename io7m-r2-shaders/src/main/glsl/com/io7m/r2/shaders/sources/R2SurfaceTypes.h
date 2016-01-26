#ifndef R2_SURFACE_TYPES_H
#define R2_SURFACE_TYPES_H

/// \file R2SurfaceTypes.h
/// \brief Types for deferred surface shading

#include "R2SurfaceOutput.h"

/// Interpolated surface data that all deferred surface shaders will receive.

struct R2_surface_data_t {
  /// Eye-space surface position
  vec4 position_eye;
  /// Clip-space surface position
  vec4 position_clip;
  /// Positive eye-space Z position
  float positive_eye_z;
  /// Object-space UV
  vec2 uv;
  /// Object-space vertex normal
  vec3 normal;
  /// Tangent vector
  vec3 tangent;
  /// Bitangent vector
  vec3 bitangent;
};

/// Derived surface data that all deferred surface shaders will receive.

struct R2_surface_derived_t {
  /// Final uncompressed eye-space normal produced by bump/normal mapping
  vec3 normal_eye;
  /// Compressed eye-space normal
  vec2 normal_compressed;
};

/// Matrices related to the rendered instance that all deferred surface shaders will receive.

struct R2_surface_matrices_instance_t {
  /// Object-space to Eye-space matrix
  mat4x4 transform_modelview;
  /// Object-space to Eye-space normal matrix
  mat3x3 transform_normal;
  /// UV matrix
  mat3x3 transform_uv;
};

/// Matrices related to the view that all deferred surface shaders will receive.

struct R2_surface_matrices_view_t {
  /// World-space to Eye-space matrix
  mat4x4 transform_view;
  /// Eye-space to Clip-space matrix
  mat4x4 transform_projection;
};

/// Constant surface parameters that all deferred surface shaders will receive.

struct R2_surface_parameters_t {
  /// Logarithmic depth coefficient
  float depth_coefficient;

  /// Emission level in the range `[0, 1]`
  float emission_amount;

  /// Base RGBA albedo color
  vec4  albedo_color;
  /// Albedo color/texture mix
  float albedo_mix;

  /// RGB specular color
  vec3  specular_color;
  /// Specular exponent in the range `[0, 256]`
  float specular_exponent;
};

/// Input textures that all deferred surface shaders will receive.

struct R2_surface_textures_t {
  /// RGBA albedo texture
  sampler2D albedo;
  /// RGB normal map texture
  sampler2D normal;
  /// RGB specular map texture
  sampler2D specular;
  /// R emission level texture
  sampler2D emission;
};

#endif // R2_SURFACE_TYPES_H
