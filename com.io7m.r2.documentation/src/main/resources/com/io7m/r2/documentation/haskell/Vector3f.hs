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

module Vector3f (
  T (V3), x, y, z,
  add3,
  cross,
  dot3,
  magnitude,
  magnitude_squared,
  mult3,
  negation,
  normalize,
  scale,
  sub3
) where

data T = V3 {
  x :: Float,
  y :: Float,
  z :: Float
} deriving (Eq, Ord, Show)

-- | Add vectors, @v0 + v1@.
add3 :: T -> T -> T
add3 (V3 x0 y0 z0) (V3 x1 y1 z1) =
  V3 (x0 + x1) (y0 + y1) (z0 + z1)

-- | Subtract vectors, @v0 - v1@.
sub3 :: T -> T -> T
sub3 (V3 x0 y0 z0) (V3 x1 y1 z1) =
  V3 (x0 - x1) (y0 - y1) (z0 - z1)

-- | Component-wise multiply vectors, @v0 * v1@.
mult3 :: T -> T -> T
mult3 (V3 x0 y0 z0) (V3 x1 y1 z1) =
  V3 (x0 * x1) (y0 * y1) (z0 * z1)

-- | Scale vectors by scalars, @v * s@.
scale :: T -> Float -> T
scale (V3 x0 y0 z0) s =
  V3 (x0 * s) (y0 * s) (z0 * s)

-- | Dot product of @v0@ and @v1@.
dot3 :: T -> T -> Float
dot3 v0 v1 =
  case mult3 v0 v1 of
    V3 x y z -> x + y + z

-- | The squared magnitude of the @v = 'dot3' v v@.
magnitude_squared :: T -> Float
magnitude_squared v = dot3 v v

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
negation (V3 x y z) =
  V3 (0.0 - x) (0.0 - y) (0.0 - z)

-- | The cross product of @v0@ and @v1@.
cross :: T -> T -> T
cross (V3 x0 y0 z0) (V3 x1 y1 z1) =
  let
    x = (y0 * z1) - (z0 * y1)
    y = (z0 * x1) - (x0 * z1)
    z = (x0 * y1) - (y0 * x1)
  in
    V3 x y z

