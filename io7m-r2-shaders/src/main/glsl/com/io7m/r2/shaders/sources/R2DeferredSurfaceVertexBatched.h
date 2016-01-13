#ifndef R2_DEFERRED_SURFACE_VERTEX_BATCHED_H
#define R2_DEFERRED_SURFACE_VERTEX_BATCHED_H

#include "R2DeferredSurfaceVertex.h"

/// \file R2DeferredSurfaceVertexBatched.h
/// \brief Data delivered via vertex attributes to batched instances in deferred rendering.

layout(location = 4) in mat4x4 R2_vertex_transform_model; /// Object-space to World-space matrix

#endif // R2_DEFERRED_SURFACE_VERTEX_BATCHED_H
