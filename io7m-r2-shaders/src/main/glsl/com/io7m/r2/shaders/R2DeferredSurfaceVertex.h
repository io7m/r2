#ifndef R2_DEFERRED_SURFACE_VERTEX_H
#define R2_DEFERRED_SURFACE_VERTEX_H

//
// Data required by each vertex in deferred rendering.
//

layout(location = 0) in vec3 R2_vertex_position; // Object-space position
layout(location = 1) in vec2 R2_vertex_uv;       // UV coordinates
layout(location = 2) in vec3 R2_vertex_normal;   // Object-space normal vector
layout(location = 3) in vec4 R2_vertex_tangent4; // Object-space tangent vector

#endif // R2_DEFERRED_SURFACE_VERTEX_H
