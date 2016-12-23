module RayAndQAll where

import qualified NDCCorners
import qualified RayAndQ
import qualified Matrix4f as M4x4
import qualified Vector4f as V4

data T = T {
  q_x0y0 :: V4.T,
  q_x1y0 :: V4.T,
  q_x0y1 :: V4.T,
  q_x1y1 :: V4.T,
  ray_x0y0 :: V4.T,
  ray_x1y0 :: V4.T,
  ray_x0y1 :: V4.T,
  ray_x1y1 :: V4.T
} deriving (Eq, Ord, Show)

-- | Calculate all rays and qs for the four pairs of near/far frustum corners
calculate :: M4x4.T -> T
calculate inverse_m =
  let
    (x0y0_ray, x0y0_q) = RayAndQ.ray_and_q inverse_m (NDCCorners.near_x0y0, NDCCorners.far_x0y0)
    (x1y0_ray, x1y0_q) = RayAndQ.ray_and_q inverse_m (NDCCorners.near_x1y0, NDCCorners.far_x1y0)
    (x0y1_ray, x0y1_q) = RayAndQ.ray_and_q inverse_m (NDCCorners.near_x0y1, NDCCorners.far_x0y1)
    (x1y1_ray, x1y1_q) = RayAndQ.ray_and_q inverse_m (NDCCorners.near_x1y1, NDCCorners.far_x1y1)
  in
    T {
      q_x0y0 = x0y0_q,
      q_x1y0 = x1y0_q,
      q_x0y1 = x0y1_q,
      q_x1y1 = x1y1_q,
      ray_x0y0 = x0y0_ray,
      ray_x1y0 = x1y0_ray,
      ray_x0y1 = x0y1_ray,
      ray_x1y1 = x1y1_ray
    }
