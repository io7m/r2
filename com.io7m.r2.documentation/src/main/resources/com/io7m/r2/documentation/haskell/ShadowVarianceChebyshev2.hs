module ShadowVarianceChebyshev2 where

data T = T {
  minimum_variance :: Float,
  bleed_reduction  :: Float
} deriving (Eq, Show)

chebyshev :: (Float, Float) -> Float -> Float -> Float
chebyshev (d, ds) min_variance t =
  let p        = if t <= d then 1.0 else 0.0
      variance = max (ds - (d * d)) min_variance
      du       = t - d
      p_max    = variance / (variance + (du * du))
  in max p p_max

clamp :: Float -> (Float, Float) -> Float
clamp x (lower, upper) = max (min x upper) lower

linear_step :: Float -> Float -> Float -> Float
linear_step lower upper x = clamp ((x - lower) / (upper - lower)) (0.0, 1.0)

factor :: T -> (Float, Float) -> Float -> Float
factor shadow (d, ds) t =
  let u = chebyshev (d, ds) (minimum_variance shadow) t in
    linear_step (bleed_reduction shadow) 1.0 u
