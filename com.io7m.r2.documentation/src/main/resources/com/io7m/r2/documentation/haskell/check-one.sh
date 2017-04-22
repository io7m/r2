#!/bin/sh -ex

exec ghc -Wall -fno-warn-name-shadowing -Werror "$1" -o 'return ()'
