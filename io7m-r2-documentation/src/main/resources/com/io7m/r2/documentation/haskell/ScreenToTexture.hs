module ScreenToTexture where

import qualified Vector2f

screen_to_texture :: Vector2f.T -> Float -> Float -> Vector2f.T
screen_to_texture position width height =
  let u = (Vector2f.x position) / width
      v = (Vector2f.y position) / height
  in Vector2f.V2 u v
