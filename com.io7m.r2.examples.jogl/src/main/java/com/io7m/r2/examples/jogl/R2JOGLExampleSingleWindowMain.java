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

package com.io7m.r2.examples.jogl;

import com.io7m.jcamera.JCameraFPSStyle;
import com.io7m.jcamera.JCameraFPSStyleInput;
import com.io7m.jcamera.JCameraFPSStyleInputType;
import com.io7m.jcamera.JCameraFPSStyleIntegrator;
import com.io7m.jcamera.JCameraFPSStyleIntegratorType;
import com.io7m.jcamera.JCameraFPSStyleMouseRegion;
import com.io7m.jcamera.JCameraFPSStyleSnapshot;
import com.io7m.jcamera.JCameraFPSStyleSnapshots;
import com.io7m.jcamera.JCameraFPSStyleType;
import com.io7m.jcamera.JCameraRotationCoefficients;
import com.io7m.jcamera.JCameraScreenOrigin;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLCubeMapFaceRH;
import com.io7m.jcanephora.core.JCGLExceptionNonCompliant;
import com.io7m.jcanephora.core.JCGLExceptionUnsupported;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUpdateType;
import com.io7m.jcanephora.core.JCGLTextureCubeType;
import com.io7m.jcanephora.core.JCGLTextureCubeUpdateType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLType;
import com.io7m.jcanephora.texture.loader.awt.JCGLAWTTextureDataProvider;
import com.io7m.jcanephora.texture.loader.core.JCGLTLTextureDataProviderType;
import com.io7m.jcanephora.texture.loader.core.JCGLTLTextureDataType;
import com.io7m.jcanephora.texture.loader.core.JCGLTLTextureUpdateProvider;
import com.io7m.jcanephora.texture.loader.core.JCGLTLTextureUpdateProviderType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.core.R2Texture2DStatic;
import com.io7m.r2.core.R2Texture2DType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureCubeStatic;
import com.io7m.r2.core.R2TextureCubeType;
import com.io7m.r2.core.R2TextureCubeUsableType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.examples.R2ExampleType;
import com.io7m.r2.facade.R2FacadeProvider;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingExceptionIO;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.sombrero.serviceloader.SoShaderResolverServiceLoader;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.DebugGL3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.io7m.jcanephora.core.JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR;
import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR_MIPMAP_NEAREST;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP;
import static com.io7m.jcanephora.core.JCGLTextureWrapR.TEXTURE_WRAP_CLAMP_TO_EDGE;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_REPEAT;
import static com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE;

/**
 * JOGL example frontend.
 */

public final class R2JOGLExampleSingleWindowMain implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2JOGLExampleSingleWindowMain.class);
  }

  private final Class<? extends R2ExampleType> example_type;

  private R2JOGLExampleSingleWindowMain(
    final Class<? extends R2ExampleType> c)
  {
    this.example_type = NullCheck.notNull(c, "Class");
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   *
   * @throws Exception On errors
   */

  public static void main(final String[] args)
    throws Exception
  {
    if (args.length < 1) {
      LOG.error("usage: class-name");
      System.exit(1);
    }

    LOG.debug("start {}", args[0]);

    @SuppressWarnings("unchecked") final Class<? extends R2ExampleType> ck =
      (Class<? extends R2ExampleType>) Class.forName(args[0]);
    new R2JOGLExampleSingleWindowMain(ck).run();
  }

  @Override
  public void run()
  {
    LOG.debug("running");

    try {
      final R2ExampleType example = this.example_type.newInstance();

      final GLProfile profile = GLProfile.get(GLProfile.GL3);
      final GLCapabilities caps = new GLCapabilities(profile);

      final GLWindow win = GLWindow.create(caps);
      win.setSize(1024, 768);
      win.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      win.setTitle("R2");

      final ExampleListener el = new ExampleListener(win, example);
      win.addGLEventListener(el);
      win.addKeyListener(el);
      win.addMouseListener(el);

      final Animator anim = new Animator(win);

      /*
       * Close the program when the window closes.
       */

      win.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowDestroyed(
          final WindowEvent e)
        {
          LOG.debug("Stopping animator");
          anim.stop();
          LOG.debug("Exiting");
          System.exit(0);
        }
      });

      anim.setUpdateFPSFrames(60 * 10, System.out);
      anim.start();
      win.setVisible(true);
    } catch (final InstantiationException e) {
      LOG.error("instantiation error: {}", e);
    } catch (final IllegalAccessException e) {
      LOG.error("access error: {}", e);
    }
  }

  private static final class ExampleListener extends MouseAdapter implements
    GLEventListener,
    KeyListener
  {
    private final R2ExampleType example;
    private final GLWindow window;
    private JCGLContextType rendering;
    private int frame;
    private R2FacadeType r2_facade;
    private AreaSizeL area;
    private Services services;
    private boolean want_cursor_warp;

    ExampleListener(
      final GLWindow in_window,
      final R2ExampleType in_example)
    {
      this.window = NullCheck.notNull(in_window, "Window");
      this.example = NullCheck.notNull(in_example, "Example");
      this.want_cursor_warp = false;
    }

    @Override
    public void mouseMoved(
      final MouseEvent e)
    {
      assert e != null;

      /*
       * If the camera is enabled, get the rotation coefficients for the mouse
       * movement.
       */

      if (this.services != null) {
        if (this.services.camera_enabled) {
          this.services.camera_rotations =
            this.services.camera_mouse_region.coefficients(
              (double) e.getX(),
              (double) e.getY());
          this.services.camera_input.addRotationAroundHorizontal(
            this.services.camera_rotations.horizontal());
          this.services.camera_input.addRotationAroundVertical(
            this.services.camera_rotations.vertical());
        }
      }
    }

    @Override
    public void init(final GLAutoDrawable drawable)
    {
      final Thread th = Thread.currentThread();
      th.setName("com.io7m.r2.example-renderer:" + th.getId());

      this.resize(drawable);
    }

    @Override
    public void dispose(
      final GLAutoDrawable drawable)
    {
      LOG.debug("finishing example");
      this.example.onFinish(this.rendering.contextGetGL33(), this.r2_facade);
    }

    @Override
    public void display(
      final GLAutoDrawable drawable)
    {
      try {

        /*
         * On the first frame, clear the screen. The second frame does
         * potentially very-long running resource initialization, so clearing
         * the frame here prevents the user from seeing the raw uncleared
         * contents of whatever was in memory for the framebuffer.
         */

        if (this.frame == 0) {
          final GL gl = drawable.getGL();
          gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
          gl.glClear(
            GL.GL_COLOR_BUFFER_BIT
              | GL.GL_DEPTH_BUFFER_BIT
              | GL.GL_STENCIL_BUFFER_BIT);
          return;
        }

        /*
         * On the second frame, initialize all resources for the example.
         */

        if (this.frame == 1) {
          final JCGLImplementationJOGLType g =
            JCGLImplementationJOGL.getInstance();

          this.rendering = g.newContextFromWithSupplier(
            drawable.getContext(),
            (c) -> new DebugGL3(c.getGL().getGL3()),
            "main");

          final JCGLInterfaceGL33Type render_g33 =
            this.rendering.contextGetGL33();

          this.r2_facade =
            new R2FacadeProvider().create(
              render_g33,
              SoShaderResolverServiceLoader.create());
          this.services =
            new Services(
              this.window,
              this.r2_facade,
              render_g33);

          LOG.debug("initializing example");
          this.example.onInitialize(
            this.services, render_g33, this.area, this.r2_facade);
          LOG.debug("initialized example");
          return;
        }

        /*
         * Integrate the camera for the current frame.
         */

        if (this.services.camera_enabled) {
          this.services.integrateCamera();
          this.want_cursor_warp = true;
        }

        /*
         * Render the current example.
         */

        final JCGLInterfaceGL33Type g33 = this.rendering.contextGetGL33();
        this.example.onRender(
          this.services,
          g33,
          this.area,
          this.r2_facade,
          this.frame - 2);

        /*
         * The camera has requested that the cursor be warped to the center
         * of the screen.
         */

        if (this.want_cursor_warp) {
          final Window w = this.window;
          if (w != null) {
            w.warpPointer(w.getWidth() / 2, w.getHeight() / 2);
            this.want_cursor_warp = false;
          }
        }

      } catch (final JCGLExceptionUnsupported x) {
        LOG.error("unsupported: ", x);
      } catch (final JCGLExceptionNonCompliant x) {
        LOG.error("non compliant: ", x);
      } finally {
        ++this.frame;
      }
    }

    @Override
    public void reshape(
      final GLAutoDrawable drawable,
      final int x,
      final int y,
      final int width,
      final int height)
    {
      this.resize(drawable);
    }

    private void resize(
      final GLAutoDrawable drawable)
    {
      this.area = AreaSizeL.of(
        (long) drawable.getSurfaceWidth(),
        (long) drawable.getSurfaceHeight());
    }

    @Override
    public void keyPressed(
      final KeyEvent e)
    {
      assert e != null;

      /*
       * Services are not available until the second frame. As unlikely as it
       * is that the user will be able to press a key in the 1/60th of a second
       * before they're available...
       */

      if (this.services == null) {
        return;
      }

      /*
       * Ignore events that are the result of keyboard auto-repeat. This means
       * there's one single event when a key is pressed, and another when it is
       * released (as opposed to an endless stream of both when the key is held
       * down).
       */

      if ((e.getModifiers() & InputEvent.AUTOREPEAT_MASK) == InputEvent
        .AUTOREPEAT_MASK) {
        return;
      }

      switch (e.getKeyCode()) {

        /*
         * Standard WASD camera controls, with E and Q moving up and down,
         * respectively.
         */

        case KeyEvent.VK_A: {
          this.services.camera_input.setMovingLeft(true);
          break;
        }
        case KeyEvent.VK_W: {
          this.services.camera_input.setMovingForward(true);
          break;
        }
        case KeyEvent.VK_S: {
          this.services.camera_input.setMovingBackward(true);
          break;
        }
        case KeyEvent.VK_D: {
          this.services.camera_input.setMovingRight(true);
          break;
        }
        case KeyEvent.VK_E: {
          this.services.camera_input.setMovingUp(true);
          break;
        }
        case KeyEvent.VK_Q: {
          this.services.camera_input.setMovingDown(true);
          break;
        }

        default: {
          // Nothing
        }
      }
    }

    @Override
    public void keyReleased(
      final KeyEvent e)
    {
      assert e != null;

      /*
       * Services are not available until the second frame. As unlikely as it
       * is that the user will be able to press a key in the 1/60th of a second
       * before they're available...
       */

      if (this.services == null) {
        return;
      }

      /*
       * Ignore events that are the result of keyboard auto-repeat. This means
       * there's one single event when a key is pressed, and another when it is
       * released (as opposed to an endless stream of both when the key is held
       * down).
       */

      if ((e.getModifiers() & InputEvent.AUTOREPEAT_MASK) == InputEvent
        .AUTOREPEAT_MASK) {
        return;
      }

      switch (e.getKeyCode()) {

        /*
         * Pressing 'M' enables/disables the camera.
         */

        case KeyEvent.VK_M: {
          this.toggleCameraEnabled();
          break;
        }

        /*
         * Pressing 'P' makes the mouse cursor visible/invisible.
         */

        case KeyEvent.VK_P: {
          LOG.debug(
            "Making pointer {}\n",
            this.window.isPointerVisible() ? "invisible" : "visible");
          this.window.setPointerVisible(!this.window.isPointerVisible());
          break;
        }

        /*
         * Standard WASD camera controls, with E and Q moving up and down,
         * respectively.
         */

        case KeyEvent.VK_A: {
          this.services.camera_input.setMovingLeft(false);
          break;
        }
        case KeyEvent.VK_W: {
          this.services.camera_input.setMovingForward(false);
          break;
        }
        case KeyEvent.VK_S: {
          this.services.camera_input.setMovingBackward(false);
          break;
        }
        case KeyEvent.VK_D: {
          this.services.camera_input.setMovingRight(false);
          break;
        }
        case KeyEvent.VK_E: {
          this.services.camera_input.setMovingUp(false);
          break;
        }
        case KeyEvent.VK_Q: {
          this.services.camera_input.setMovingDown(false);
          break;
        }

        default: {
          // Nothing
        }
      }
    }

    private void toggleCameraEnabled()
    {
      if (this.services.camera_enabled) {
        LOG.debug("Disabling camera");
        this.window.confinePointer(false);
      } else {
        LOG.debug("Enabling camera");
        this.window.confinePointer(true);
        this.want_cursor_warp = true;
        this.services.camera_input.setRotationHorizontal(0.0);
        this.services.camera_input.setRotationVertical(0.0);
      }

      this.services.camera_enabled = !this.services.camera_enabled;
    }
  }

  private static final class Services implements R2ExampleServicesType
  {
    private final JCGLTLTextureDataProviderType data_prov;
    private final JCGLTLTextureUpdateProviderType update_prov;
    private final JCGLTexturesType textures;
    private final List<JCGLTextureUnitType> units;
    private final Map<String, R2Texture2DType> texture_2d_cache;
    private final Map<String, R2TextureCubeType> texture_cube_cache;
    private final Map<String, JCGLArrayObjectType> mesh_cache;
    private final JCameraFPSStyleType camera;
    private final JCameraFPSStyleInputType camera_input;
    private final JCameraFPSStyleIntegratorType camera_integrator;
    private final JCameraFPSStyleMouseRegion camera_mouse_region;
    private final JCGLArrayObjectsType array_objects;
    private final R2FacadeType r2_facade;
    private JCameraRotationCoefficients camera_rotations;
    private long camera_time_then;
    private double camera_time_accum;
    private JCameraFPSStyleSnapshot camera_snap_current;
    private JCameraFPSStyleSnapshot camera_snap_prev;
    private boolean camera_enabled;
    private PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> camera_matrix;

    Services(
      final GLWindow window,
      final R2FacadeType in_r2_facade,
      final JCGLInterfaceGL33Type render_g33)
    {
      this.r2_facade = NullCheck.notNull(in_r2_facade, "Facade");

      this.textures = render_g33.textures();
      this.array_objects = render_g33.arrayObjects();

      this.units = this.textures.textureGetUnits();
      this.data_prov = JCGLAWTTextureDataProvider.newProvider();
      this.update_prov = JCGLTLTextureUpdateProvider.newProvider();
      this.texture_2d_cache = new HashMap<>(128);
      this.texture_cube_cache = new HashMap<>(128);
      this.mesh_cache = new HashMap<>(128);

      this.camera = JCameraFPSStyle.newCamera();
      this.camera.cameraSetPosition3(0.0, 0.0, 5.0);

      this.camera_input = JCameraFPSStyleInput.newInput();
      this.camera_integrator =
        JCameraFPSStyleIntegrator.newIntegrator(this.camera, this.camera_input);
      this.camera_time_then = System.nanoTime();
      this.camera_enabled = false;
      this.camera_mouse_region =
        JCameraFPSStyleMouseRegion.of(
          JCameraScreenOrigin.SCREEN_ORIGIN_TOP_LEFT,
          (double) window.getWidth(),
          (double) window.getHeight());
      this.camera_rotations =
        JCameraRotationCoefficients.of(0.0, 0.0);
    }

    @Override
    public R2Texture2DUsableType getTexture2D(
      final String name)
    {
      if (this.texture_2d_cache.containsKey(name)) {
        return this.texture_2d_cache.get(name);
      }

      final Class<R2ExampleServicesType> c = R2ExampleServicesType.class;
      try (final InputStream is = c.getResourceAsStream(name)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureUnitType u0 = this.units.get(0);
        final JCGLTexture2DType t =
          this.textures.texture2DAllocate(
            u0,
            data.sizeX(),
            data.sizeY(),
            TEXTURE_FORMAT_RGBA_8_4BPP,
            TEXTURE_WRAP_REPEAT,
            JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
            TEXTURE_FILTER_LINEAR_MIPMAP_NEAREST,
            TEXTURE_FILTER_LINEAR);
        final JCGLTexture2DUpdateType update =
          this.update_prov.createTextureUpdate2D(t, data);
        this.textures.texture2DUpdate(u0, update);
        final R2Texture2DType r2 = R2Texture2DStatic.of(t);
        this.texture_2d_cache.put(name, r2);
        return r2;
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public R2TextureCubeUsableType getTextureCube(
      final String name)
    {
      if (this.texture_cube_cache.containsKey(name)) {
        return this.texture_cube_cache.get(name);
      }

      final Class<R2ExampleServicesType> c = R2ExampleServicesType.class;

      final String name_positive_x = name + "/positive_x.png";
      final String name_positive_y = name + "/positive_y.png";
      final String name_positive_z = name + "/positive_z.png";
      final String name_negative_x = name + "/negative_x.png";
      final String name_negative_y = name + "/negative_y.png";
      final String name_negative_z = name + "/negative_z.png";

      final JCGLTextureUnitType u0 = this.units.get(0);
      final JCGLTextureCubeType t;

      try (final InputStream is = c.getResourceAsStream(name_positive_x)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);

        t =
          this.textures.textureCubeAllocate(
            u0,
            data.sizeX(),
            TEXTURE_FORMAT_RGBA_8_4BPP,
            TEXTURE_WRAP_CLAMP_TO_EDGE,
            JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
            JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
            TEXTURE_FILTER_LINEAR_MIPMAP_NEAREST,
            TEXTURE_FILTER_LINEAR);

        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_POSITIVE_X, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      try (final InputStream is = c.getResourceAsStream(name_positive_y)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_POSITIVE_Y, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      try (final InputStream is = c.getResourceAsStream(name_positive_z)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_POSITIVE_Z, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      try (final InputStream is = c.getResourceAsStream(name_negative_x)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_NEGATIVE_X, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      try (final InputStream is = c.getResourceAsStream(name_negative_y)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_NEGATIVE_Y, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      try (final InputStream is = c.getResourceAsStream(name_negative_z)) {
        final JCGLTLTextureDataType data = this.data_prov.loadFromStream(is);
        final JCGLTextureCubeUpdateType update =
          this.update_prov.createTextureUpdateCube(t, data);
        this.textures.textureCubeUpdateRH(
          u0, JCGLCubeMapFaceRH.CUBE_MAP_RH_NEGATIVE_Z, update);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      final R2TextureCubeType r2 = R2TextureCubeStatic.of(t);
      this.texture_cube_cache.put(name, r2);
      return r2;
    }

    @Override
    public JCGLArrayObjectType getMesh(final String name)
    {
      if (this.mesh_cache.containsKey(name)) {
        return this.mesh_cache.get(name);
      }

      LOG.debug("loading mesh {}", name);

      try {
        final Class<R2ExampleServicesType> c = R2ExampleServicesType.class;
        final URL url = c.getResource(name);

        final R2MeshLoaded r =
          this.r2_facade.meshLoader().loadSynchronously(
            this.r2_facade.rendererGL33(),
            R2MeshLoaderRequest.of(
              url.toURI(),
              R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
              R2MeshRequireUV.R2_UV_OPTIONAL,
              JCGLUsageHint.USAGE_STATIC_DRAW,
              JCGLUsageHint.USAGE_STATIC_DRAW));

        final JCGLArrayObjectType ao = r.newArrayObject(this.array_objects);
        this.array_objects.arrayObjectUnbind();
        this.mesh_cache.put(name, ao);
        return ao;
      } catch (final URISyntaxException e) {
        LOG.error("could not load mesh: {}: ", name, e);
        throw new R2MeshLoadingExceptionIO(e);
      }
    }

    @Override
    public boolean isFreeCameraEnabled()
    {
      return this.camera_enabled;
    }

    @Override
    public PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType>
    getFreeCameraViewMatrix()
    {
      return this.camera_matrix;
    }

    void integrateCamera()
    {
      /*
       * Integrate the camera as many times as necessary for each rendering
       * frame interval.
       */

      final long time_now = System.nanoTime();
      final long time_diff = time_now - this.camera_time_then;
      final double time_diff_s = (double) time_diff / 1000000000.0;
      this.camera_time_accum = this.camera_time_accum + time_diff_s;
      this.camera_time_then = time_now;

      final double sim_delta = 1.0 / 60.0;
      while (this.camera_time_accum >= sim_delta) {
        this.camera_integrator.integrate(sim_delta);
        this.camera_snap_prev = this.camera_snap_current;
        this.camera_snap_current = JCameraFPSStyleSnapshots.of(this.camera);
        this.camera_time_accum -= sim_delta;
      }

      /*
       * Determine how far the current time is between the current camera state
       * and the next, and use that value to interpolate between the two saved
       * states.
       */

      final double alpha = this.camera_time_accum / sim_delta;
      final JCameraFPSStyleSnapshot snap_interpolated =
        JCameraFPSStyleSnapshots.interpolate(
          this.camera_snap_prev,
          this.camera_snap_current,
          alpha);

      this.camera_matrix = snap_interpolated.cameraMakeViewPMatrix();
    }
  }
}
