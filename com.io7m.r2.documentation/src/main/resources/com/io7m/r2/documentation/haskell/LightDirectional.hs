module LightDirectional where

import qualified Color3
import qualified Direction
import qualified Spaces

data T = LD {
  color     :: Color3.T,
  intensity :: Float,
  direction :: Direction.T Spaces.Eye
} deriving (Eq, Ord, Show)
