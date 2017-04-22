module Position3 where

import qualified Vector3f

-- | The phantom type parameter 's' indicates the coordinate space.
type T a = Vector3f.T

-- | Subtract vectors, @v0 - v1@.
sub3 :: T a -> T a -> T a
sub3 = Vector3f.sub3
