package com.io7m.r2.tests;

import com.io7m.jcanephora.async.JCGLAsyncInterfaceGL33;
import com.io7m.jcanephora.async.JCGLAsyncInterfaceGL33Type;
import com.io7m.jcanephora.async.JCGLAsyncInterfaceUsableGL33Type;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jnull.NullCheck;
import org.junit.rules.ExternalResource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public final class R2AsyncGLRule extends ExternalResource
{
  private final Supplier<JCGLContextType> supplier;
  private JCGLAsyncInterfaceGL33Type async_gl33;
  private ExecutorService exec;

  public R2AsyncGLRule(
    final Supplier<JCGLContextType> supplier)
  {
    this.supplier = NullCheck.notNull(supplier, "Supplier");
  }

  public JCGLAsyncInterfaceUsableGL33Type gl()
  {
    return this.async_gl33;
  }

  public ExecutorService executor()
  {
    return this.exec;
  }

  @Override
  protected void before()
    throws Throwable
  {
    this.exec = Executors.newFixedThreadPool(1);
    this.async_gl33 =
      JCGLAsyncInterfaceGL33.newAsync(
        () -> {
          final JCGLContextType c = this.supplier.get();
          c.contextReleaseCurrent();
          return c;
        });
  }

  @Override
  protected void after()
  {
    try {
      this.exec.shutdownNow();
      this.exec.awaitTermination(10L, TimeUnit.SECONDS);
      this.async_gl33.shutDown().get(10L, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (final ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }
}
