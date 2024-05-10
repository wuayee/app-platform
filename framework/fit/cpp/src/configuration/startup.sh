#!/bin/bash

CURRENT_DIR=$(cd "$(dirname "$0")" && pwd)
APP_DIR=$(realpath "${CURRENT_DIR}/..")
LIB_DIR=${APP_DIR}/lib
THIRD_PARTY_DIR=${APP_DIR}/third_party

CONFIG_FILE=$1
if [ -z "${CONFIG_FILE}" ]; then
  echo "Use default config file: worker_config.json"
  CONFIG_FILE=worker_config.json
fi
CONFIG_FILE=$(realpath "${CONFIG_FILE}")

export LD_LIBRARY_PATH=${LIB_DIR}:${THIRD_PARTY_DIR}

function lookup_ip_address() {
  local nic=$(route -n | grep UG | grep -ww 0.0.0.0 | head -n1 | awk '{print $8}')
  local ip=$(ifconfig ${nic} | grep "netmask" | awk '{print $2}')
  echo ${ip}
}

# gdb --args "${CURRENT_DIR}/FitWorker" "--config_file=${CONFIG_FILE}"
cd "${CURRENT_DIR}" && "${CURRENT_DIR}/FitWorker" "--config_file=${CONFIG_FILE}"
