module LightProjective where

import qualified Color3
import qualified Position3
import qualified Spaces

data T = LS {
  color     :: Color3.T,
  intensity :: Float,
  origin    :: Position3.T Spaces.Eye,
  radius    :: Float,
  falloff   :: Float
} deriving (Eq, Ord, Show)
