#ifndef R2_DEFERRED_SURFACE_VERTEX_H
#define R2_DEFERRED_SURFACE_VERTEX_H

/// \file R2DeferredSurfaceVertex.h
/// \brief Data required by each vertex in deferred rendering, provided as vertex attributes.

/// Object-space position
layout(location = 0) in vec3 R2_vertex_position;

/// UV coordinates
layout(location = 1) in vec2 R2_vertex_uv;

/// Object-space normal vector
layout(location = 2) in vec3 R2_vertex_normal;

/// Object-space tangent vector
layout(location = 3) in vec4 R2_vertex_tangent4;

#endif // R2_DEFERRED_SURFACE_VERTEX_H
