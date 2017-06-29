/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.r2.tests.jogl;

import com.io7m.jcanephora.core.JCGLExceptionNonCompliant;
import com.io7m.jcanephora.core.JCGLExceptionUnsupported;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.core.SoShaderResolverType;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;
import com.io7m.sombrero.serviceloader.SoShaderResolverServiceLoader;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static com.io7m.r2.tests.jogl.Compiler.ShaderType.FRAGMENT_SHADER;
import static com.io7m.r2.tests.jogl.Compiler.ShaderType.GEOMETRY_SHADER;
import static com.io7m.r2.tests.jogl.Compiler.ShaderType.VERTEX_SHADER;

public final class Compiler
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(Compiler.class);
  }

  private Compiler()
  {

  }

  enum ShaderType
  {
    VERTEX_SHADER,
    FRAGMENT_SHADER,
    GEOMETRY_SHADER
  }

  private static final class Shader
  {
    private final ShaderType type;
    private final String name;

    Shader(
      final String in_name,
      final ShaderType in_type)
    {
      this.type = in_type;
      this.name = in_name;
    }
  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final JCGLInterfaceGL33Type g33 =
      getShaders();
    final JCGLShadersType gs =
      g33.shaders();

    final SoShaderResolverType resolver =
      SoShaderResolverServiceLoader.create();
    final SoShaderPreprocessorConfig config =
      SoShaderPreprocessorConfig.of(resolver, OptionalInt.of(330));
    final SoShaderPreprocessorType pp =
      SoShaderPreprocessorJCPP.create(config);

    final ArrayList<Shader> shaders = new ArrayList<>();
    shaders.add(new Shader(
      "com.io7m.r2.shaders.core/R2Billboarded.geom", GEOMETRY_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.core/R2Billboarded.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.core/R2Nothing.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.core/R2White.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugColorVertices.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugColorVerticesWorldPosition.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugEyePositionReconstruction.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugEyeZReconstruction.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugIdentity.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugLogDepth.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugLogDepthComposed.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugNormalCompression.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugPositionOnly.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugVisualConstant.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.debug/R2DebugVisualConstantScreen.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2DepthBasicBatched.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2DepthBasicSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2DepthBasicStippledSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2DepthBatched.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2DepthSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2LogDepthOnlySingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.depth/R2LogDepthOnlySingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBasicBatched.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBasicBillboarded.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBasicReflectiveSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBasicSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBasicStippledSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceBatched.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceDebugEyeSpaceVertexNormalsSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceDebugObjectSpaceVertexNormalsSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.geometry/R2SurfaceSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightAmbientSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightAmbientSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightDirectionalDebugConstantSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightDirectionalSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightDirectionalSpecularSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightPositionalSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightProjectiveLambertBlinnPhongShadowVarianceSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightProjectiveLambertBlinnPhongSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightProjectiveLambertShadowVarianceSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightProjectiveLambertSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightSphericalDebugAttenuationSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightSphericalDebugConstantSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightSphericalLambertBlinnPhongSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightSphericalLambertPhongSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.light/R2LightSphericalLambertSingle.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.mask/R2Mask.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.mask/R2MaskBatched.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.mask/R2MaskSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.translucent/R2TranslucentBasicPremultiplied.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.stencil/R2StencilScreen.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.stencil/R2StencilSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.ssao/R2SSAO.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.refraction/R2RefractionMaskedDelta.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.refraction/R2RefractionMaskedDeltaBatched.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.refraction/R2RefractionMaskedDeltaSingle.vert", VERTEX_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.bilateral_blur/R2FilterBilateralBlurDepthAwareHorizontal4f.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.bilateral_blur/R2FilterBilateralBlurDepthAwareVertical4f.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.box_blur/R2FilterBoxBlurHorizontal4f.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.box_blur/R2FilterBoxBlurVertical4f.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.compositor/R2FilterTextureShow.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.emission/R2FilterEmission.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fog/R2FilterFogDepthLinear.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fog/R2FilterFogDepthQuadratic.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fog/R2FilterFogDepthQuadraticInverse.frag", FRAGMENT_SHADER
    ));

    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_10.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_15.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_20.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_25.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_29.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.fxaa/R2FXAA_39.frag", FRAGMENT_SHADER
    ));

    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.light_applicator/R2FilterLightApplicator.frag", FRAGMENT_SHADER
    ));
    shaders.add(new Shader(
      "com.io7m.r2.shaders.filters.occlusion_applicator/R2FilterOcclusionApplicator.frag", FRAGMENT_SHADER
    ));

    shaders.add(new Shader(
      "com.io7m.r2.shaders.core/R2Filter.vert", VERTEX_SHADER
    ));

    for (final Shader s : shaders) {
      LOG.info("COMPILE {} {}", s.name, s.type);

      final Map<String, String> defines = new HashMap<>();
      final List<String> r = pp.preprocessFile(defines, s.name);
      for (int index = 0; index < r.size(); ++index) {
        final String line = r.get(index);
        LOG.debug(
          "{}: {}: {}",
          String.format("%32s", s.name),
          String.format("%4d", Integer.valueOf(index + 1)),
          line.substring(0, line.length() - 1));
      }
      compileSources(gs, s.name, r, s.type);
    }
  }

  private static void compileSources(
    final JCGLShadersType gs,
    final String name,
    final List<String> r,
    final ShaderType type)
    throws JCGLExceptionUnsupported, JCGLExceptionNonCompliant
  {
    switch (type) {
      case VERTEX_SHADER: {
        gs.shaderCompileVertex(name, r);
        return;
      }
      case FRAGMENT_SHADER: {
        gs.shaderCompileFragment(name, r);
        return;
      }
      case GEOMETRY_SHADER: {
        gs.shaderCompileGeometry(name, r);
        return;
      }
    }

    throw new UnreachableCodeException();
  }

  private static JCGLInterfaceGL33Type getShaders()
    throws JCGLExceptionUnsupported, JCGLExceptionNonCompliant
  {
    final GLProfile pro =
      GLProfile.get(GLProfile.GL3);
    final GLCapabilities caps =
      new GLCapabilities(pro);
    final GLDrawableFactory f =
      GLDrawableFactory.getFactory(pro);
    final GLOffscreenAutoDrawable drawable =
      f.createOffscreenAutoDrawable(null, caps, null, 640, 480);
    drawable.display();

    final GLContext gc = drawable.getContext();
    gc.makeCurrent();

    final JCGLImplementationJOGLType gi =
      JCGLImplementationJOGL.getInstance();
    final JCGLContextType c =
      gi.newContextFrom(drawable.getContext(), "Main");
    return c.contextGetGL33();
  }
}
