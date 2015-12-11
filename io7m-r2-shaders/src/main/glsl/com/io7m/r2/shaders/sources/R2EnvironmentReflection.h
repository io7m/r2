#ifndef R2_ENVIRONMENT_REFLECTION_H
#define R2_ENVIRONMENT_REFLECTION_H

#include "R2CubeMaps.h"

//
// Functions for environment-mapped reflections.
//

//
// Calculate a reflection based on the eye-space view direction `v_eye`
// and eye-space surface normal `n_eye`. The reflection vector is transformed
// to world-space using the inverse view transform `view_inv` and sampled
// from the right-handed cube map `t`.
//

vec4
R2_environmentReflection(
  const samplerCube t,
  const vec3 v_eye,
  const vec3 n_eye,
  const mat4x4 view_inv)
{
  vec3 v_eye_n = normalize (v_eye);
  vec3 n_eye_n = normalize (n_eye);
  vec3 r       = reflect (v_eye_n, n_eye_n);
  vec4 r_world = view_inv * vec4 (r, 0.0);
  return R2_cubeMapTextureRH (t, r_world.xyz);
}

#endif // R2_ENVIRONMENT_REFLECTION_H
