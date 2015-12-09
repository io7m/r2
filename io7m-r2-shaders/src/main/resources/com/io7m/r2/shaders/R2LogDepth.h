#ifndef R2_LOG_DEPTH_H
#define R2_LOG_DEPTH_H

//
// Logarithmic depth functions.
//

float
R2_logDepthPrepareEyeZ(
  const float z)
{
  return 1.0 + (-z);
}

float
R2_logDepthEncodePartial(
  const float z,
  const float depth_coefficient)
{
  float half_co = depth_coefficient * 0.5;
  float clamp_z = max (0.000001, z);
  return log2 (clamp_z) * half_co;
}

float
R2_logDepthEncodeFull(
  const float z,
  const float depth_coefficient)
{
  float half_co = depth_coefficient * 0.5;
  float clamp_z = max (0.000001, z + 1.0);
  return log2 (clamp_z) * half_co;
}

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
