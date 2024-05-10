#!/bin/bash

set -euo pipefail

current_directory=$(cd "$(dirname "$0")" && pwd)

if ! command -v python3 &>/dev/null; then
  echo "python3 not found; install it now..."
  sudo apt install -y python3
  sudo apt install -y python3-pip
  pip3 install requests
fi

python3 "${current_directory}"/download_dependency.py -d "${current_directory}"/../fit_dependency.json \
  -o "${current_directory}"/../../build/lib