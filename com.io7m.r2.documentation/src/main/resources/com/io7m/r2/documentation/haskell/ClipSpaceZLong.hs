module ClipSpaceZLong where

import qualified Matrix4f as M4x4;
import qualified Vector4f as V4;

clip_z_long :: M4x4.T -> V4.T -> Float
clip_z_long m eye =
  let
    m20 = M4x4.row_column m (2, 0)
    m21 = M4x4.row_column m (2, 1)
    m22 = M4x4.row_column m (2, 2)
    m23 = M4x4.row_column m (2, 3)

    k0 = (V4.x eye) * m20
    k1 = (V4.y eye) * m21
    k2 = (V4.z eye) * m22
    k3 = (V4.w eye) * m23
  in
    k0 + k1 + k2 + k3
