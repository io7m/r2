
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

module Matrix4f (
  T (..),
  inverse,
  mult,
  mult_v,
  row,
  row_column
) where

import qualified Vector4f as V4

data T = M4 {
  column_0 :: V4.T,
  column_1 :: V4.T,
  column_2 :: V4.T,
  column_3 :: V4.T
} deriving (Eq, Ord, Show)

v4_get :: V4.T -> Integer -> Float
v4_get v 0 = V4.x v
v4_get v 1 = V4.y v
v4_get v 2 = V4.z v
v4_get v 3 = V4.w v
v4_get _ _ = undefined

row_column :: T -> (Integer, Integer) -> Float
row_column m (r, c) =
  case c of
    0 -> v4_get (column_0 m) r
    1 -> v4_get (column_1 m) r
    2 -> v4_get (column_2 m) r
    3 -> v4_get (column_3 m) r
    _ -> undefined

row :: T -> Integer -> V4.T
row m r =
  V4.V4
    (row_column m (r, 0))
    (row_column m (r, 1))
    (row_column m (r, 2))
    (row_column m (r, 3))

mult_v :: T -> V4.T -> V4.T
mult_v m v =
  V4.V4
    (V4.dot4 (row m 0) v)
    (V4.dot4 (row m 1) v)
    (V4.dot4 (row m 2) v)
    (V4.dot4 (row m 3) v)

-- XXX: Omitted
mult :: T -> T -> T
mult _ _ = undefined

-- XXX: Omitted
inverse :: T -> Maybe T
inverse _ = undefined
