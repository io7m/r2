#ifndef R2_LOG_DEPTH_H
#define R2_LOG_DEPTH_H

/// \file R2LogDepth.h
/// \brief Logarithmic depth functions.

///
/// Prepare an eye-space Z value for encoding. See R2_logDepthEncodePartial.
///
/// @param z An eye-space Z value
/// @return The prepared value
///

float
R2_logDepthPrepareEyeZ(
  const float z)
{
  return 1.0 + (-z);
}

///
/// Partially encode the given _positive_ eye-space Z value. This partial encoding
/// can be used when performing part of the encoding in a vertex shader
/// and the rest in a fragment shader (for efficiency reasons) - See R2_logDepthPrepareEyeZ.
///
/// @param z                 An eye-space Z value
/// @param depth_coefficient The depth coefficient used to encode \a z
///
/// @return The encoded depth
///

float
R2_logDepthEncodePartial(
  const float z,
  const float depth_coefficient)
{
  float half_co = depth_coefficient * 0.5;
  float clamp_z = max (0.000001, z);
  return log2 (clamp_z) * half_co;
}

///
/// Fully encode the given eye-space Z value.
///
/// @param z                 An eye-space Z value
/// @param depth_coefficient The depth coefficient used to encode \a z
/// @return The fully encoded depth
///

float
R2_logDepthEncodeFull(
  const float z,
  const float depth_coefficient)
{
  float half_co = depth_coefficient * 0.5;
  float clamp_z = max (0.000001, z + 1.0);
  return log2 (clamp_z) * half_co;
}

///
/// Decode a depth value that was encoded with the given depth coefficient.
/// Note that in most cases, this will yield a _positive_ eye-space Z value,
/// and must be negated to yield a conventional negative eye-space Z value.
///
/// @param z                 The depth value
/// @param depth_coefficient The coefficient used during encoding
///
/// @return The original (positive) eye-space Z value
///

float
R2_logDepthDecode(
  const float z,
  const float depth_coefficient)
{
  float half_co  = depth_coefficient * 0.5;
  float exponent = z / half_co;
  return pow (2.0, exponent) - 1.0;
}

#endif // R2_LOG_DEPTH_H
