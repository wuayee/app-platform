#!/bin/bash
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.

# Description: databus core prepare third-party source

# Description: download third-party source
# Arguments:
#     $1: third_party.mm.xml directory
#     $2: lib path. download all if blank
#     $3: tmp folder for git mm
#     $4: git mm xml filename
# Returns: 0 for success, other nums for fail
function prepare_third_party() {
    local working_dir=${1:-$(readlink -f "$(dirname "$0")/../third_party")}
    local tmp_folder=${3:-".dummy_git_mm"}
    local xml_filename=${4:-"third_party.mm.xml"}
    ensure_git_mm || return 1
    if [ ! -f "${working_dir}/${xml_filename}" ]; then
        echo "no such file: ${working_dir}/${xml_filename}"
        return 1
    fi
    cd "${working_dir}" || return 2
    clean_temp_files
    prepare_dummy_git "${working_dir}"
    cd "${working_dir}" || return 2
    git mm init -u "${tmp_folder}/" -m "${xml_filename}" -b main
    if [ $# -eq 1 ]; then
        git mm sync -d --force-sync || clean_temp_files
    else
        git mm sync -d --force-sync "$2" || clean_temp_files
    fi
    clean_temp_files
}

