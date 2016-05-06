#ifndef R2FXAA_FRAGMENT_H
#define R2FXAA_FRAGMENT_H

/// \file R2FXAAFragment.h
/// \brief FXAA fragment shader

#include "R2FXAA.h"

in vec2 R2_uv;

uniform R2_fxaa_t R2_fxaa;

layout(location = 0) out vec4 R2_out;

void
main (void)
{
  R2_out = FxaaPixelShader(
    R2_uv,
    vec4 (0.0),              // Unused "fxaaConsolePosPos" parameter
    R2_fxaa.image,
    R2_fxaa.image,           // Unused "fxaaConsole360TexExpBiasNegOne" parameter
    R2_fxaa.image,           // Unused "fxaaConsole360TexExpBiasNegTwo" parameter
    R2_fxaa.screen_inverse,
    vec4 (0.0),              // Unused "fxaaConsoleRcpFrameOpt" parameter
    vec4 (0.0),              // Unused "fxaaConsoleRcpFrameOpt2" parameter
    vec4 (0.0),              // Unused "fxaaConsole360RcpFrameOpt2" parameter
    R2_fxaa.subpixel_aliasing_removal,
    R2_fxaa.edge_threshold,
    R2_fxaa.edge_threshold_minimum,
    0.0,                     // Unused "fxaaConsoleEdgeSharpness" parameter
    0.0,                     // Unused "fxaaConsoleEdgeThreshold" parameter
    0.0,                     // Unused "fxaaConsoleEdgeThresholdMin" parameter
    vec4 (0.0)               // Unused "fxaaConsole360ConstDir" parameter
  );
}

#endif // R2FXAA_FRAGMENT_H
