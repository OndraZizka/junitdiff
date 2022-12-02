#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

rm /usr/bin/junitdiff
ln -s "$SCRIPT_DIR/junitdiff" /usr/bin/junitdiff
