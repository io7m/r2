module FogFactorY where

clamp :: Float -> (Float, Float) -> Float
clamp x (lower, upper) = max (min x upper) lower

fog_factor :: Float -> (Float, Float) -> Float
fog_factor y (lower, upper) =
  let r = (y - lower) / (upper - lower) in
    clamp r (0.0, 1.0)
