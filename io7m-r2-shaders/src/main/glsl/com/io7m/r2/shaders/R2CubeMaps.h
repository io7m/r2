#ifndef R2_CUBE_MAPS_H
#define R2_CUBE_MAPS_H

//
// Functions for dealing with cube maps.
//

//
// Sample from a right-handed cube map `t` using the world-space vector `v`.
//

vec4
R2_cubeMapTextureRH(
  const samplerCube t,
  const vec3 v)
{
  return textureCube (t, vec3 (
    v.x,
    0.0 - v.y,
    0.0 - v.z
  ));
}

#endif // R2_CUBE_MAPS_H
