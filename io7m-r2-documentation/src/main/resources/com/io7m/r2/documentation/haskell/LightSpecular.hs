module LightSpecular where

import qualified Color3
import qualified Direction
import qualified Normal
import qualified Reflection
import qualified Spaces
import qualified Specular
import qualified Vector3f

specularPhong :: Direction.T Spaces.Eye -> Direction.T Spaces.Eye -> Normal.T -> Color3.T -> Float -> Specular.T -> Vector3f.T
specularPhong stl view n light_color light_intensity (Specular.S surface_spec surface_exponent) =
  let 
    reflection   = Reflection.reflection view n
    factor       = (max 0.0 (Vector3f.dot3 reflection stl)) ** surface_exponent
    light_raw    = Vector3f.scale light_color light_intensity
    light_scaled = Vector3f.scale light_raw factor
  in 
    Vector3f.mult3 light_scaled surface_spec

specularBlinnPhong :: Direction.T Spaces.Eye -> Direction.T Spaces.Eye -> Normal.T -> Color3.T -> Float -> Specular.T -> Vector3f.T
specularBlinnPhong stl view n light_color light_intensity (Specular.S surface_spec surface_exponent) =
  let
    reflection   = Reflection.reflection view n
    factor       = (max 0.0 (Vector3f.dot3 reflection stl)) ** surface_exponent
    light_raw    = Vector3f.scale light_color light_intensity
    light_scaled = Vector3f.scale light_raw factor
  in
    Vector3f.mult3 light_scaled surface_spec