// Single-instance stencil vertex shader.

layout(location = 0) in vec3 R2_vertex_position; // Object-space position

struct R2_stencil_parameters_t {
  mat4x4 transform_modelview;  // Object-space to Eye-space matrix
  mat4x4 transform_projection; // Eye-space to Clip-space matrix
};

uniform R2_stencil_parameters_t R2_stencil_parameters;

void
main (void)
{
  vec4 position_hom =
    vec4 (R2_vertex_position, 1.0);
  vec4 position_eye =
    (R2_stencil_parameters.transform_modelview * position_hom);
  vec4 position_clip =
    ((R2_stencil_parameters.transform_projection * R2_stencil_parameters.transform_modelview) * position_hom);

  gl_Position = position_clip;
}