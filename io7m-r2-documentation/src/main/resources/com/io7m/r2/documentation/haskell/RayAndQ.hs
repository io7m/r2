module RayAndQ where

import qualified Matrix4f as M4x4
import qualified Vector4f as V4

-- | Calculate @(ray, q)@ for the given inverse projection matrix and frustum corners
ray_and_q :: M4x4.T -> (V4.T, V4.T) -> (V4.T, V4.T)
ray_and_q inverse_m (near, far) =
  let
    -- Unproject the NDC coordinates to eye-space
    near_hom    = M4x4.mult_v inverse_m near
    near_eye    = V4.div_s near_hom (V4.w near_hom)
    far_hom     = M4x4.mult_v inverse_m far
    far_eye     = V4.div_s far_hom (V4.w far_hom)
    
    -- Calculate a ray with ray.z == 1.0
    ray_initial = V4.sub4 far_eye near_eye
    ray = V4.div_s ray_initial (V4.z ray_initial)
    
    -- Subtract the scaled ray from the near corner to calculate q
    q = V4.sub4 near_eye (V4.scale ray (V4.z near_eye))
  in
    (ray, q)
