#ifndef R2_DEFERRED_LIGHT_VERTEX_H
#define R2_DEFERRED_LIGHT_VERTEX_H

/// \file R2DeferredLightVertex.h
/// \brief Data required by each vertex in deferred light rendering, provided as vertex attributes.

/// Object-space position
layout(location = 0) in vec3 R2_vertex_position;

/// UV coordinates
layout(location = 1) in vec2 R2_vertex_uv;

#endif // R2_DEFERRED_LIGHT_VERTEX_H
