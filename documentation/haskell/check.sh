#!/bin/sh -ex

for f in *.hs
do
  ./check-one.sh "$f" || exit 1
done

rm *.o
rm *.hi
