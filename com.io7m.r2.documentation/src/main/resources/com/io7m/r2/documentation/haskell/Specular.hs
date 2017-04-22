module Specular where

import qualified Color3

data T = S {
  color    :: Color3.T,
  exponent :: Float 
} deriving (Eq, Ord, Show)
