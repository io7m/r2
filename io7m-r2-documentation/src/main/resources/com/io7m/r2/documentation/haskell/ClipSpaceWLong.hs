module ClipSpaceWLong where

import qualified Matrix4f as M4x4;
import qualified Vector4f as V4;

clip_w_long :: M4x4.T -> V4.T -> Float
clip_w_long m eye =
  let
    m30 = M4x4.row_column m (3, 0)
    m31 = M4x4.row_column m (3, 1)
    m32 = M4x4.row_column m (3, 2)
    m33 = M4x4.row_column m (3, 3)

    k0 = (V4.x eye) * m30
    k1 = (V4.y eye) * m31
    k2 = (V4.z eye) * m32
    k3 = (V4.w eye) * m33
  in
    k0 + k1 + k2 + k3
