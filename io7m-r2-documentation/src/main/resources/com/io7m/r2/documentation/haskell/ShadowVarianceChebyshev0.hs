module ShadowVarianceChebyshev0 where

chebyshev :: (Float, Float) -> Float -> Float
chebyshev (d, ds) t =
  let p        = if t <= d then 1.0 else 0.0
      variance = ds - (d * d)
      du       = t - d
      p_max    = variance / (variance + (du * du))
  in max p p_max

factor :: (Float, Float) -> Float -> Float
factor = chebyshev
