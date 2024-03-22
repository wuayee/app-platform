#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: generate c++ headers from flatbuffers' .fbs files
# Arguments:
#     $1: flatc binary
#     $2: flat buffers' .fbs files directory
#     $3: output directory for generated headers

set -eu

flat_buffers=$1
flat_buffers_dir=$2
output_dir=$3

for file in $(ls "${flat_buffers_dir}" | grep ".fbs$"); do
    absolute_path=$(readlink -f "${flat_buffers_dir}/${file}")
    echo "${absolute_path} -> $(readlink -f "${output_dir}/${file/.fbs/_generated.h}")"
    ${flat_buffers} --cpp --scoped-enums --cpp-std c++11 -o "${output_dir}" "${absolute_path}"
done
echo -e " done...\n flatbuffers' header generated to ${output_dir}"
