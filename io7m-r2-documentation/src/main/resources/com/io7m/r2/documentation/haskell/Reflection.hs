module Reflection where

import qualified Vector3f as V3

reflection :: V3.T -> V3.T -> V3.T
reflection v0 v1 = V3.sub3 v0 (V3.scale v1 (2.0 * (V3.dot3 v1 v0)))
