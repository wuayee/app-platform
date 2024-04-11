#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: pack databus core executable into docker images and save as tarball
# Arguments:
#     $1: databus executable
#     $2: dockerfile for packing
#     $3: tarball output directory
#     $3: databus version

set -eu

databus_executable=$1
dockerfile=$2
output_dir=$(readlink -f $3)
databus_version=${4:-"latest"}

output_build_dir="${output_dir}/databus_core"

if [ ! -d "${output_build_dir}" ]; then
    mkdir -p "${output_build_dir}"
fi

# copy all the files into the output directory
if [ "${databus_executable}" != "${output_build_dir}/$(basename "${databus_executable}")" ]; then
    cp -f "${databus_executable}" "${output_build_dir}"
fi
cp -f "${dockerfile}" "${output_build_dir}"

echo "[info] prepare to build databus-core image from dir: ${output_build_dir}"
docker build -f "${dockerfile}" -t "databus-core:${databus_version}" --quiet "${output_build_dir}"

echo "[info] image built. saving it to tarball"
docker save "databus-core:${databus_version}" -o "${output_dir}/databus-core_${databus_version}.tar"

rm -rf "${output_build_dir}"
echo "[info] pack and save databus-core docker image finished."

set +eu
