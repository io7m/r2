module Directional where

import qualified Color4
import qualified Direction
import qualified LightDirectional
import qualified LightDiffuse
import qualified LightSpecular
import qualified Normal
import qualified Position3
import qualified Spaces
import qualified Specular
import qualified Vector3f
import qualified Vector4f

directional :: Direction.T Spaces.Eye -> Normal.T -> Position3.T Spaces.Eye -> LightDirectional.T -> Specular.T -> Color4.T -> Vector3f.T
directional view n position light specular (Vector4f.V4 sr sg sb _) =
  let
    stl             = Vector3f.normalize (Vector3f.negation position)
    light_color     = LightDirectional.color light
    light_intensity = LightDirectional.intensity light
    light_d         = LightDiffuse.diffuse stl n light_color light_intensity
    light_s         = LightSpecular.specular stl view n light_color light_intensity specular
    lit_d           = Vector3f.mult3 (Vector3f.V3 sr sg sb) light_d
    lit_s           = Vector3f.add3 lit_d light_s
  in
    lit_s
