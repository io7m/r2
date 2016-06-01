module EyeSpaceZ where

import qualified Matrix4f as M4x4;

eye_z :: M4x4.T -> Float -> Float
eye_z m ndc_z =
  let
    m22 = M4x4.row_column m (2, 2)
    m23 = M4x4.row_column m (2, 3)
    m32 = M4x4.row_column m (3, 2)
    m33 = M4x4.row_column m (3, 3)
    
    a = (ndc_z * m33) - m32
    b = (ndc_z * m23) - m22
  in
    - (a / b)
