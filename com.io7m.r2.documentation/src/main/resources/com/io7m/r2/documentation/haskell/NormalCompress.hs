module NormalCompress where

import qualified Vector3f
import qualified Vector2f
import qualified Normal

compress :: Normal.T -> Vector2f.T
compress n =
  let p = sqrt ((Vector3f.z n * 8.0) + 8.0)
      x = (Vector3f.x n / p) + 0.5
      y = (Vector3f.y n / p) + 0.5
  in Vector2f.V2 x y