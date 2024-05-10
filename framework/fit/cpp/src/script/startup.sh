#!/bin/bash

WORK_DIR=$(pwd)
CURRENT_DIR=$(cd "$(dirname "$0")" && pwd)
APP_DIR=$(realpath "${CURRENT_DIR}/..")
LIB_DIR=${APP_DIR}/lib
THIRD_PARTY_DIR=${APP_DIR}/third_party

CONFIG_FILE=$1
if [ -z "${CONFIG_FILE}" ]; then
  CONFIG_FILE=worker_config.json
fi

export LD_LIBRARY_PATH=${LIB_DIR}:${THIRD_PARTY_DIR}

function lookup_ip_address() {
  local nic=$(route -n | grep UG | grep -ww 0.0.0.0 | head -n1 | awk '{print $8}')
  local ip=$(ifconfig ${nic} | grep "netmask" | awk '{print $2}')
  echo ${ip}
}

"${CURRENT_DIR}/FitWorker" "config_file=${CONFIG_FILE}"