#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core source build all script

# Description: run cmake for all files
# Arguments:
#     $1: build directory
#     $2: source code directory
# Returns: 0 for success, other nums for fail
function exec_cmake_all() {
    dir_src=$2
    if [ ! -e "${dir_src}/CMakeLists.txt" ]; then
        echo "no CMakeLists.txt in ${dir_src}"
        return 1
    fi

    dir_build=$1
    cmake -S "${dir_src}" -B "${dir_build}" -G "Unix Makefiles"
}

# Description: run make for core files
# Arguments: build directory
function exec_make_all() {
    dir_build=$1
    cd "${dir_build}"
    make -j$(nproc)
}

# Description: build core
# Arguments:
#     $1: build directory
#     $2: source code directory
# Returns: 0 for success, other nums for fail
function exec_build_all() {
    dir_build=$1
    dir_src=$2
    if [ ! -d "${dir_build}" ]; then
        mkdir -p "${dir_build}"
    fi

    if ! exec_cmake_all "$1" "$2" ; then
        return $?
    fi
    exec_make_all "$1"
}
