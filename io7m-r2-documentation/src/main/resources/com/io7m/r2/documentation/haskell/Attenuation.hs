module Attenuation where

attenuation_from_inverses :: Float -> Float -> Float -> Float
attenuation_from_inverses inverse_maximum_range inverse_falloff distance =
  max 0.0 (1.0 - (distance * inverse_maximum_range) ** inverse_falloff)

attenuation :: Float -> Float -> Float -> Float
attenuation maximum_range falloff distance =
  attenuation_from_inverses (1.0 / maximum_range) (1.0 / falloff) distance
