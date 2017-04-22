module ClipSpaceWSimple where

import qualified Matrix4f as M4x4;
import qualified Vector4f as V4;

clip_w_simple :: M4x4.T -> V4.T -> Float
clip_w_simple m eye =
  let
    m32 = M4x4.row_column m (3, 2)
    m33 = M4x4.row_column m (3, 3)
  in
    ((V4.z eye) * m32) + m33
