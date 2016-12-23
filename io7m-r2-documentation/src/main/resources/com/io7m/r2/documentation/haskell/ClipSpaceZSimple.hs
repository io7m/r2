module ClipSpaceZSimple where

import qualified Matrix4f as M4x4;
import qualified Vector4f as V4;

clip_z_simple :: M4x4.T -> V4.T -> Float
clip_z_simple m eye =
  let
    m22 = M4x4.row_column m (2, 2)
    m23 = M4x4.row_column m (2, 3)
  in
    ((V4.z eye) * m22) + m23
