#ifndef R2_VERTEX_H
#define R2_VERTEX_H

/// \file R2Vertex.h
/// \brief Vertex types

/// Interpolated vertex data that can be received by shaders.

struct R2_vertex_data_t {
  /// Eye-space surface position
  vec4 position_eye;
  /// Clip-space surface position
  vec4 position_clip;
  /// Positive eye-space Z position
  float positive_eye_z;
  /// Object-space UV
  vec2 uv;
  /// Object-space vertex normal
  vec3 normal_vertex;
  /// Tangent vector
  vec3 tangent;
  /// Bitangent vector
  vec3 bitangent;
};

#endif // R2_VERTEX_H
