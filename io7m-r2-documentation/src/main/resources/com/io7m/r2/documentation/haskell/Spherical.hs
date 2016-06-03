module Spherical where

import qualified Attenuation
import qualified Color4
import qualified Direction
import qualified LightDiffuse
import qualified LightSpecular
import qualified LightSpherical
import qualified Normal
import qualified Position3
import qualified Specular
import qualified Spaces
import qualified Vector3f
import qualified Vector4f

spherical :: Direction.T Spaces.Eye -> Normal.T -> Position3.T Spaces.Eye -> LightSpherical.T -> Specular.T -> Color4.T -> Vector3f.T
spherical view n surface_position light specular (Vector4f.V4 sr sg sb _) =
  let
    position_diff   = Position3.sub3 surface_position (LightSpherical.origin light)
    stl             = Vector3f.normalize (Vector3f.negation position_diff)
    distance        = Vector3f.magnitude (position_diff)
    attenuation     = Attenuation.attenuation (LightSpherical.radius light) (LightSpherical.falloff light) distance
    light_color     = LightSpherical.color light
    light_intensity = LightSpherical.intensity light
    light_d         = LightDiffuse.diffuse stl n light_color light_intensity
    light_s         = LightSpecular.specularBlinnPhong stl view n light_color light_intensity specular
    light_da        = Vector3f.scale light_d attenuation
    light_sa        = Vector3f.scale light_s attenuation
    lit_d           = Vector3f.mult3 (Vector3f.V3 sr sg sb) light_da
    lit_s           = Vector3f.add3 lit_d light_sa
  in
    lit_s
