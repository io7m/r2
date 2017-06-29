#ifndef R2_VERTEX_ATTRIBUTES_FULL_H
#define R2_VERTEX_ATTRIBUTES_FULL_H

/// \file R2VertexAttributes.h
/// \brief Vertex attribute conventions

/// Object-space position
layout(location = 0) in vec3 R2_vertex_position;

/// UV coordinates
layout(location = 1) in vec2 R2_vertex_uv;

/// Object-space normal vector
layout(location = 2) in vec3 R2_vertex_normal;

/// Object-space tangent vector
layout(location = 3) in vec4 R2_vertex_tangent4;

#ifdef R2_VERTEX_ATTRIBUTES_REQUIRE_BATCHED_TRANSFORM_MODEL
/// Object-space to World-space matrix
layout(location = 4) in mat4x4 R2_vertex_transform_model;
#endif

#endif // R2_VERTEX_ATTRIBUTES_FULL_H
