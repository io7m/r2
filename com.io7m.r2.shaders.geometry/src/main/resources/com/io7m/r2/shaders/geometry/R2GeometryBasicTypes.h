#ifndef R2_GEOMETRY_BASIC_TYPES_H
#define R2_GEOMETRY_BASIC_TYPES_H

/// \file R2GeometryBasicTypes.h
/// \brief Types for basic surfaces

/// Constant surface parameters that all basic surface shaders will receive.

struct R2_basic_surface_parameters_t {
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

  /// Alpha discard threshold in the range `[0, 1]`.
  float alpha_discard_threshold;
};

/// Input textures that all basic surface shaders will receive.

struct R2_basic_surface_textures_t {
  /// RGBA albedo texture
  sampler2D albedo;
  /// RGB specular map texture
  sampler2D specular;
  /// R emission level texture
  sampler2D emission;
};

#endif // R2_GEOMETRY_BASIC_TYPES_H

