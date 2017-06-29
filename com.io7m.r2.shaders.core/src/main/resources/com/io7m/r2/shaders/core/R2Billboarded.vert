/// \file R2Billboarded.vert
/// \brief A vertex shader for billboarded instances.

#include "R2LogDepth.h"
#include "R2View.h"

/// World-space position
layout(location = 0) in vec3 R2_vertex_world_position;

/// Eye-space scale
layout(location = 1) in float R2_vertex_scale;

uniform R2_view_t R2_view;

out vec4 R2_billboard_scale_eye;

void
main (void)
{
  vec4 scale =
    vec4 (R2_vertex_scale, R2_vertex_scale, R2_vertex_scale, 1.0);

  vec4 position_hom =
    vec4 (R2_vertex_world_position, 1.0);
  vec4 position_eye =
    R2_view.transform_view * position_hom;

  gl_Position            = position_eye;
  R2_billboard_scale_eye = scale;
}

