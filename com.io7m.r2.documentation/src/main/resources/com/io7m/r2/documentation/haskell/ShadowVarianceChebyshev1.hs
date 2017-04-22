module ShadowVarianceChebyshev1 where

data T = T {
  minimum_variance :: Float
} deriving (Eq, Show)

chebyshev :: (Float, Float) -> Float -> Float -> Float
chebyshev (d, ds) min_variance t =
  let p        = if t <= d then 1.0 else 0.0
      variance = max (ds - (d * d)) min_variance
      du       = t - d
      p_max    = variance / (variance + (du * du))
  in max p p_max

factor :: T -> (Float, Float) -> Float -> Float
factor shadow (d, ds) t =
  chebyshev (d, ds) (minimum_variance shadow) t
