#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

java -jar "$SCRIPT_DIR/JUnitDiff-*-executable.jar" "$@"
