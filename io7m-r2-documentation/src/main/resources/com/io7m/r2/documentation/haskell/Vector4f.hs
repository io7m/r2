-- Copyright © 2014 <code@io7m.com> http://io7m.com
--
-- Permission to use, copy, modify, and/or distribute this software for any
-- purpose with or without fee is hereby granted, provided that the above
-- copyright notice and this permission notice appear in all copies.
--
-- THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
-- WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
-- MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
-- ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
-- WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
-- ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
-- OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

module Vector4f (
  T (V4),
  x, y, z, w,
  add4,
  dot4,
  div_s,
  interpolate,
  magnitude,
  magnitude_squared,
  mult4,
  negation,
  normalize,
  scale,
  sub4
) where

data T = V4 {
  x :: Float,
  y :: Float,
  z :: Float,
  w :: Float
} deriving (Eq, Ord, Show) 

-- | Add vectors, @v0 + v1@.
add4 :: T -> T -> T
add4 (V4 x0 y0 z0 w0) (V4 x1 y1 z1 w1) =
  V4 (x0 + x1) (y0 + y1) (z0 + z1) (w0 + w1)

-- | Subtract vectors, @v0 - v1@.
sub4 :: T -> T -> T
sub4 (V4 x0 y0 z0 w0) (V4 x1 y1 z1 w1) =
  V4 (x0 - x1) (y0 - y1) (z0 - z1) (w0 - w1)

-- | Component-wise multiply vectors, @v0 * v1@.
mult4 :: T -> T -> T
mult4 (V4 x0 y0 z0 w0) (V4 x1 y1 z1 w1) =
  V4 (x0 * x1) (y0 * y1) (z0 * z1) (w0 * w1)

-- | Scale vectors by scalars, @v * s@.
scale :: T -> Float -> T
scale (V4 x0 y0 z0 w0) s =
  V4 (x0 * s) (y0 * s) (z0 * s) (w0 * s)

-- | Divide vectors by scalars, @v * s@.
div_s :: T -> Float -> T
div_s (V4 x0 y0 z0 w0) s =
  V4 (x0 / s) (y0 / s) (z0 / s) (w0 / s)

-- | Dot product of @v0@ and @v1@.
dot4 :: T -> T -> Float
dot4 v0 v1 =
  case mult4 v0 v1 of
    V4 x y z w -> x + y + z + w

-- | The squared magnitude of the @v = 'dot4' v v@.
magnitude_squared :: T -> Float
magnitude_squared v = dot4 v v

-- | The magnitude of @v@.
magnitude :: T -> Float
magnitude = sqrt . magnitude_squared

-- | @v@ with unit length.
normalize :: T -> T
normalize v =
  let m = magnitude_squared v in
    if m > 0.0 then
      scale v (1.0 / m)
    else
      v

-- | The negation of @v@.
negation :: T -> T
negation (V4 x y z w) =
  V4 (0.0 - x) (0.0 - y) (0.0 - z) (0.0 - w)

-- | The linear interpolation between @v0@ and @v1@ based on @r@.
interpolate :: T -> Float -> T -> T
interpolate v0 r v1 =
  add4 (scale v0 (1 - r)) (scale v1 r)
