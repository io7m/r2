#ifndef R2_NORMALS_H
#define R2_NORMALS_H

//
// Functions for transforming normal vectors.
//

//
// Compress the (normalized) vector `n` into two elements
// using a spheremap transform (Lambert Azimuthal Equal-Area).
//

vec2
R2_normalsCompress(
  const vec3 n)
{
  float p = sqrt ((n.z * 8.0) + 8.0);
  float x = (n.x / p) + 0.5;
  float y = (n.y / p) + 0.5;
  return vec2 (x, y);
}

//
// Decompress the given vector, assuming it was encoded with
// a spheremap transform (Lambert Azimuthal Equal-Area).
//

vec3
R2_normalsDecompress(
  const vec2 n)
{
  vec2 fn = vec2 (
    (n.x * 4.0) - 2.0,
    (n.y * 4.0) - 2.0
  );
  float f = dot (fn, fn);
  float g = sqrt (1.0 - (f / 4.0));
  float x = fn.x * g;
  float y = fn.y * g;
  float z = 1.0 - (f / 2.0);
  return vec3 (x, y, z);
}

//
// Compute a bitangent vector given a normal `n` and tangent vector `t`,
// assuming a sign `[-1.0, 1.0]` stored in `t.w`.
//

vec3
R2_normalsBitangent(
  const vec3 n,
  const vec4 t)
{
  vec3 p = cross (n, t.xyz);
  return p * t.w;
}

//
// Unpack a tangent-space normal vector from the texture `map`.
//

vec3
R2_normalsUnpack(
  const sampler2D map,
  const vec2 uv)
{
  vec3 rgb = texture (map, uv).xyz;
  return (rgb * 2.0) + (-1.0);
}

//
// Transform the tangent-space vector `m` into the coordinate
// system given by the orthonormal vectors `{t, b, n}`.
//

vec3
R2_normalsTransform(
  const vec3 m,
  const vec3 t,
  const vec3 b,
  const vec3 n)
{
  mat3x3 mat = mat3x3 (t, b, n);
  return normalize (mat * m);
}

//
// Given a texture consisting of normal vectors `t_normal`, object-space
// normal `n`, object-space tangent `t`, object-space bitangent `b`,
// and texture coordinates `uv`, unpack and transform a
// vector from the texture, resulting in an object-space peturbed normal.
//

vec3
R2_normalsBumpLocal(
  const sampler2D t_normal,
  const vec3 n,
  const vec3 t,
  const vec3 b,
  const vec2 uv)
{
  vec3 m  = R2_normalsUnpack (t_normal, uv);
  vec3 nn = normalize (n);
  vec3 nt = normalize (t);
  vec3 nb = normalize (b);
  return R2_normalsTransform (m, nt, nb, nn);
}

//
// Given a texture consisting of normal vectors `t_normal`,
// an object-to-eye-space matrix `m_normal`, object-space
// normal `n`, object-space tangent `t`, object-space bitangent `b`,
// and texture coordinates `uv`, unpack and transform a
// vector from the texture, and transform it to eye-space.
//

vec3
R2_normalsBump(
  const sampler2D t_normal,
  const mat3x3 m_normal,
  const vec3 n,
  const vec3 t,
  const vec3 b,
  const vec2 uv)
{
  vec3 nl = R2_normalsBumpLocal (t_normal, n, t, b, uv);
  return normalize (m_normal * nl);
}

#endif // R2_NORMALS_H
