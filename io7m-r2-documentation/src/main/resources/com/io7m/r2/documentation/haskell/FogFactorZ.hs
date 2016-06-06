module FogFactorZ where

clamp :: Float -> (Float, Float) -> Float
clamp x (lower, upper) = max (min x upper) lower

fog_factor :: Float -> (Float, Float) -> Float
fog_factor z (near, far) =
  let r = (z - near) / (far - near) in
    clamp r (0.0, 1.0)
