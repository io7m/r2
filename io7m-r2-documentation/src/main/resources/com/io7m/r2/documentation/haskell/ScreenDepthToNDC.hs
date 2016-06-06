module ScreenDepthToNDC where

screen_depth_to_ndc :: Float -> Float
screen_depth_to_ndc screen_depth =
  (screen_depth * 2.0) - 1.0