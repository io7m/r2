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

module Vector2f (
  T (V2), x, y,
  add2,
  dot2,
  magnitude,
  magnitude_squared,
  mult2,
  negation,
  normalize,
  scale,
  sub2
) where

data T = V2 {
  x :: Float,
  y :: Float
} deriving (Eq, Ord, Show)

-- | Add vectors, @v0 + v1@.
add2 :: T -> T -> T
add2 (V2 x0 y0) (V2 x1 y1) =
  V2 (x0 + x1) (y0 + y1)

-- | Subtract vectors, @v0 - v1@.
sub2 :: T -> T -> T
sub2 (V2 x0 y0) (V2 x1 y1) =
  V2 (x0 - x1) (y0 - y1)

-- | Component-wise multiply vectors, @v0 * v1@.
mult2 :: T -> T -> T
mult2 (V2 x0 y0) (V2 x1 y1) =
  V2 (x0 * x1) (y0 * y1)

-- | Scale vectors by scalars, @v * s@.
scale :: T -> Float -> T
scale (V2 x0 y0) s =
  V2 (x0 * s) (y0 * s)

-- | Dot product of @v0@ and @v1@.
dot2 :: T -> T -> Float
dot2 v0 v1 =
  case mult2 v0 v1 of
    V2 x y -> x + y

-- | The squared magnitude of the @v = 'dot2' v v@.
magnitude_squared :: T -> Float
magnitude_squared v = dot2 v v

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
negation (V2 x y) =
  V2 (0.0 - x) (0.0 - y)

