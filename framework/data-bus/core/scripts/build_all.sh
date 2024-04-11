#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core source build all script

# Description: run cmake for all files
# Arguments:
#     $1: build directory
#     $2: source code directory
#     $3: cmake build mode
# Returns: 0 for success, other nums for fail
function exec_cmake_all() {
    local dir_src=$2
    if [ ! -e "${dir_src}/CMakeLists.txt" ]; then
        echo "no CMakeLists.txt in ${dir_src}"
        return 1
    fi

    local dir_build=$1
    local build_mode=$3
    cmake -DCMAKE_BUILD_TYPE="${build_mode}" \
          -DCMAKE_BINARY_DIR="${dir_build}" \
          -S "${dir_src}" -B "${dir_build}" -G "Unix Makefiles"
}

# Description: run make for core files
# Arguments: build directory
function exec_make_all() {
    local dir_build=$1
    cd "${dir_build}"
    cmake --build . -j$(nproc)
}

# Description: build core
# Arguments:
#     $1: build directory
#     $2: source code directory
#     $3: cmake build mode, optional. default to debug
# Returns: 0 for success, other nums for fail
function exec_build_all() {
    local dir_build=$1
    local dir_src=$2
    local build_mode=${3:-"Debug"}
    if [ ! -d "${dir_build}" ]; then
        mkdir -p "${dir_build}"
    fi

    if ! exec_cmake_all "${dir_build}" "${dir_src}" "${build_mode}" ; then
        return $?
    fi
    exec_make_all "${dir_build}"
}

# Description: build core and pack to docker image tarball
# Arguments:
#     $1: build directory
#     $2: source code directory
# Returns: 0 for success, other nums for fail
function exec_build_all_and_docker() {
    local dir_build=$1
    exec_build_all "${dir_build}" "$2" "Release"
    cd "${dir_build}"
    make databus_core_image
}
