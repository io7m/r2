#ifndef R2_GBUFFER_INPUT_H
#define R2_GBUFFER_INPUT_H

/// \file R2GBufferInput.h
/// \brief The textures that make up the G-Buffer

/// A type representing the set of bound textures that make up the G-Buffer.

struct R2_gbuffer_input_t {
  /// The albedo texture, with the emission level stored in the `a` component.
  sampler2D albedo;
  /// The normal texture containing compressed normals (see R2_normalsDecompress).
  sampler2D normal;
  /// The specular texture, with the exponent stored in the `a` component.
  sampler2D specular;
  /// The logarithmic depth texture (see R2_logDepthDecode).
  sampler2D depth;
};

#endif // R2_GBUFFER_INPUT_H
