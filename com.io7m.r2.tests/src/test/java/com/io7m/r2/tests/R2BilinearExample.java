package com.io7m.r2.tests;

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors2D;

public final class R2BilinearExample
{
  private R2BilinearExample()
  {

  }

  public static void main(
    final String[] args)
  {
    final Vector2D x0y0 = Vector2D.of(0.0, 0.0);
    final Vector2D x1y0 = Vector2D.of(1.0, 0.0);
    final Vector2D x0y1 = Vector2D.of(0.0, 1.0);
    final Vector2D x1y1 = Vector2D.of(1.0, 1.0);
    final Vector2D r =
      Vectors2D.interpolateBilinear(x0y0, x1y0, x0y1, x1y1, 0.0, 1.0);

    System.out.println(r);
  }
}
