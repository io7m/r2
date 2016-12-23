#ifndef R2_ENVIRONMENT_REFLECTION_H
#define R2_ENVIRONMENT_REFLECTION_H

#include "R2CubeMaps.h"

///
/// \file R2EnvironmentReflection.h
/// \brief Functions for environment-mapped reflections.
///

///
/// Calculate a reflection based on the eye-space view direction \a v_eye
/// and eye-space surface normal \a n_eye. The reflection vector is transformed
/// to world-space using the inverse view transform \a view_inv and sampled
/// from the right-handed cube map \a t.
///
/// @param t        A right-handed cube map
/// @param v_eye    An eye-space view direction
/// @param n_eye    An eye-space surface normal
/// @param view_inv An eye-space-to-world-space matrix
///

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
