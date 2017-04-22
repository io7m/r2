module NDCCorners where

import qualified Vector4f as V4

near_x0y0 :: V4.T
near_x0y0 = V4.V4 (-1.0) (-1.0) (-1.0) 1.0

near_x1y0 :: V4.T
near_x1y0 = V4.V4 1.0 (-1.0) (-1.0) 1.0

near_x0y1 :: V4.T
near_x0y1 = V4.V4 (-1.0) 1.0 (-1.0) 1.0

near_x1y1 :: V4.T
near_x1y1 = V4.V4 1.0 1.0 (-1.0) 1.0

far_x0y0 :: V4.T
far_x0y0 = V4.V4 (-1.0) (-1.0) 1.0 1.0

far_x1y0 :: V4.T
far_x1y0 = V4.V4 1.0 (-1.0) 1.0 1.0

far_x0y1 :: V4.T
far_x0y1 = V4.V4 (-1.0) 1.0 1.0 1.0

far_x1y1 :: V4.T
far_x1y1 = V4.V4 1.0 1.0 1.0 1.0

