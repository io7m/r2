module NormalDecompress where

import qualified Vector3f
import qualified Vector2f
import qualified Normal

decompress :: Vector2f.T -> Normal.T
decompress v =
  let fn = Vector2f.V2 ((Vector2f.x v * 4.0) - 2.0) ((Vector2f.y v * 4.0) - 2.0)
      f  = Vector2f.dot2 fn fn
      g  = sqrt (1.0 - (f / 4.0))
      x  = (Vector2f.x fn) * g
      y  = (Vector2f.y fn) * g
      z  = 1.0 - (f / 2.0)
  in Vector3f.V3 x y z