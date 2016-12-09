#ifndef R2_SURFACE_VERTEX_BATCHED_H
#define R2_SURFACE_VERTEX_BATCHED_H

#include "R2SurfaceVertex.h"

/// \file R2SurfaceVertexBatched.h
/// \brief Data delivered via vertex attributes to batched instances in deferred rendering.

layout(location = 4) in mat4x4 R2_vertex_transform_model; /// Object-space to World-space matrix

#endif // R2_SURFACE_VERTEX_BATCHED_H
