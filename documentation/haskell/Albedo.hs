module Albedo where

import qualified Color4
import qualified Vector4f

albedo :: Color4.T -> Float -> Color4.T -> Color4.T
albedo base mix t =
  Vector4f.interpolate base ((Vector4f.w t) * mix) t