module DProjectionExample0 where

import qualified Vector4f
import qualified Matrix4f

eye_to_ndc :: Vector4f.T -> Matrix4f.T -> Vector4f.T
eye_to_ndc eye m =
  let clip = Matrix4f.mult_v m eye in
    Vector4f.div_s clip (Vector4f.w clip)

