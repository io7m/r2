module LogDepth where

newtype LogDepth =
  LogDepth Float
    deriving (Eq, Ord, Show)

type Depth = Float

log2 :: Float -> Float
log2 = logBase 2.0

depth_coefficient :: Float -> Float
depth_coefficient far = 2.0 / log2 (far + 1.0)

encode :: Float -> Depth -> LogDepth
encode depth_co depth =
  let hco = depth_co * 0.5 in
    LogDepth $ log2 (depth + 1.0) * hco

decode :: Float -> LogDepth -> Depth
decode depth_co (LogDepth depth) =
  let hco = depth_co * 0.5 in
    (2.0 ** (depth / hco)) - 1
