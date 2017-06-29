#ifndef R2_GEOMETRY_TEXTURES_H
#define R2_GEOMETRY_TEXTURES_H

/// \file R2GeometryTextures.h
/// \brief Textures for deferred geometry shading

/// Textures that are required by all surfaces

struct R2_geometry_textures_t {
  /// RGB normal map texture
  sampler2D normal;
};

#endif // R2_GEOMETRY_TEXTURES_H

