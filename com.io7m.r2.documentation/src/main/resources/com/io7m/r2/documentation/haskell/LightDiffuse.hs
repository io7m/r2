module LightDiffuse where

import qualified Color3
import qualified Direction
import qualified Normal
import qualified Spaces
import qualified Vector3f

diffuse ::  Direction.T Spaces.Eye -> Normal.T -> Color3.T -> Float -> Vector3f.T
diffuse stl n light_color light_intensity =
  let 
    factor       = max 0.0 (Vector3f.dot3 stl n)
    light_scaled = Vector3f.scale light_color light_intensity
  in 
    Vector3f.scale light_scaled factor