#ifndef R2_GEOMETRY_DERIVED_H
#define R2_GEOMETRY_DERIVED_H

/// \file R2GeometryDerived.h
/// \brief Derived surface data for deferred geometry shading

/// Derived surface data that all deferred geometry shaders will receive.

struct R2_geometry_derived_t {
  /// Final uncompressed eye-space normal produced by bump/normal mapping
  vec3 normal_bumped;
};

#endif // R2_GEOMETRY_DERIVED_H

