#ifndef R2_BILINEAR_H
#define R2_BILINEAR_H

///
/// \file R2Bilinear.h
/// \brief Functions for performing bilinear interpolation.
///

///
/// Bilinearly interpolate between the set of four vectors.
/// Iff \a p == (0,0) then the function yields \a x0y0. Iff
/// \a p == (1,1) then the function yields \a x1y1. Any intermediate
/// values of \a p yield a bilinear mix of the four vectors.
///
/// @param x0y0 The upper left vector
/// @param x1y0 The upper right vector
/// @param x0y1 The lower left vector
/// @param x1y1 The lower right vector
/// @param p    The interpolation vector
///

vec3
R2_bilinearInterpolate3(
  const vec3 x0y0,
  const vec3 x1y0,
  const vec3 x0y1,
  const vec3 x1y1,
  const vec2 p)
{
  vec3 u0 = mix (x0y0, x1y0, p.x);
  vec3 u1 = mix (x0y1, x1y1, p.x);
  return mix (u0, u1, p.y);
}

#endif // R2_BILINEAR_H
