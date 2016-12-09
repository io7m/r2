#ifndef R2_DEPTH_TYPES_H
#define R2_DEPTH_TYPES_H

/// \file R2DepthTypes.h
/// \brief Types related to depth/depth-variance rendering.

/// The type of values produced by depth shader executions.

struct R2_depth_shader_result_t {
  /// `true` iff the fragment should be discarded
  bool discarded;
  /// The calculated logarithmic depth of the surface
  float depth;
};

#endif // R2_DEPTH_TYPES_H
