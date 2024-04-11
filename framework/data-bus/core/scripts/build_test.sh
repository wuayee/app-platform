#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core source build test script

# Description: run cmake for all files with test option on
# Arguments:
#     $1: build directory
#     $2: source code directory
# Returns: 0 for success, other nums for fail
function exec_cmake_test() {
    local dir_src=$2
    if [ ! -e "${dir_src}/CMakeLists.txt" ]; then
        echo "no CMakeLists.txt in ${dir_src}"
        return 1
    fi

    local dir_build=$1
    # test build mode is fixed to Debug for now.
    cmake -DCMAKE_BUILD_TYPE="Debug" -DDATABUS_BUILD_TESTS:BOOL=ON -S "${dir_src}" -B "${dir_build}" -G "Unix Makefiles"
}

# Description: run make for test files
# Arguments: build directory
function exec_make_test() {
    local dir_build=$1
    cd "${dir_build}"
    make -j$(nproc) databus_test
}

# Description: build and run test
# Arguments:
#     $1: build directory
#     $2: source code directory
#     $3: do run if $3 > 0, otherwise build only
# Returns: 0 for success, other nums for fail
function exec_build_and_run_test() {
    local dir_build=$1
    local dir_src=$2
    if [ ! -d "${dir_build}" ]; then
        mkdir -p "${dir_build}"
    fi

    if ! exec_cmake_test "${dir_build}" "${dir_src}" ; then
        return $?
    fi
    exec_make_test "$1"
    if [[ "$#" -eq 3 && "$3" -gt 0 ]]; then
        test_executable=$(readlink -f "${dir_build}/bin/databus_test")
        clear
        echo -e "running tests in ${test_executable}"
        ${test_executable}
    fi
}
