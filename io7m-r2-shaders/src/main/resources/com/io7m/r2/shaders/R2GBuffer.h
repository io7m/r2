#ifndef R2_GBUFFER_H
#define R2_GBUFFER_H

//
// The G-Buffer format.
//

struct R2_gbuffer_output_t {
  vec3  albedo;       // 8-bit unsigned normalized RGB color
  float emission;     // 8-bit unsigned normalized emission level
  vec2  normal;       // Compressed 16-bit half-precision normals
  vec3  specular;     // 8-bit unsigned normalized RGB specular color
  float specular_exp; // 8-bit unsigned normalized specular exponent
  float depth;        // Logarithmic depth value
};

#endif // R2_GBUFFER_H
