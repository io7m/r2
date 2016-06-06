module Bilinear4 where

import qualified Vector2f as V2
import qualified Vector4f as V4

interpolate :: (V4.T, V4.T, V4.T, V4.T) -> V2.T -> V4.T
interpolate (x0y0, x1y0, x0y1, x1y1) position =
  let u0 = V4.interpolate x0y0 (V2.x position) x1y0
      u1 = V4.interpolate x0y1 (V2.x position) x1y1
  in V4.interpolate u0 (V2.y position) u1
