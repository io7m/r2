module FogFactorZ where

clamp :: Float -> (Float, Float) -> Float
clamp x (lower, upper) = max (min x upper) lower

fogLinear :: Float -> (Float, Float) -> Float
fogLinear z (near, far) =
  let r = (z - near) / (far - near) in
    clamp r (0.0, 1.0)

fogQuadratic :: Float -> (Float, Float) -> Float
fogQuadratic z (near, far) =
  let q = fogLinear z (near, far) in q * q

fogQuadraticInverse :: Float -> (Float, Float) -> Float
fogQuadraticInverse z (near, far) =
  let q = fogLinear z (near, far) in sqrt(q)
