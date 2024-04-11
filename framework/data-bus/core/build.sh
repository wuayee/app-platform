#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: entry shell script for build databus core

set -eu

# root directory of the databus core
DIR_ROOT=$(dirname "$(readlink -f "$0")")

# sub directories
DIR_BUILD="${DIR_ROOT}/build"
DIR_DEPLOY="${DIR_ROOT}/deploy"
DIR_SCRIPTS="${DIR_ROOT}/scripts"
DIR_SRC="${DIR_ROOT}/src"
DIR_TEST="${DIR_ROOT}/test"
DIR_THIRD_PARTY="${DIR_ROOT}/third_party"

# Description: build all DataBus files
# Arguments:
#     $1: build directory
#     $2: build mode
# Global Variables:
#     DIR_SCRIPTS; DIR_ROOT
function build_data_bus() {
    source "${DIR_SCRIPTS}/build_all.sh"
    exec_build_all "$1" "${DIR_ROOT}" "$2"
}

# Description: build all DataBus files and pack to docker images
# Arguments: build mode
# Global Variables:
#     DIR_SCRIPTS; DIR_ROOT
function build_data_bus_and_pack_docker() {
    source "${DIR_SCRIPTS}/build_all.sh"
    exec_build_all_and_docker "$1" "${DIR_ROOT}"
}

# Description: build all DataBus test
# Arguments:
#     $1: build directory
#     $2: do run if $2 exits and $2 > 0, otherwise build only
# Global Variables:
#     DIR_SCRIPTS; DIR_ROOT
function build_data_bus_test() {
    source "${DIR_SCRIPTS}/build_test.sh"
    exec_build_and_run_test "$1" "${DIR_ROOT}" "${2:-0}"
}

# no args means build all with debug
if [ "$#" -eq 0 ]; then
    build_data_bus "${DIR_BUILD}" "Debug"
    exit 0
fi

message_help="\nUsage: $0 COMMAND SUBCOMMAND\nSee more at docs/build.md"

# arg parser
case $(echo "$1" | awk '{print tolower($0)}') in
    # match first arg in lower case
    "prepare" )
        source "${DIR_SCRIPTS}/prepare_third_party.sh"
        if [ "$#" -eq 1 ]; then
            prepare_third_party "${DIR_THIRD_PARTY}"
        else
            prepare_third_party "${DIR_THIRD_PARTY}" "${@:2}"
        fi;;
    "build" )
        if [ "$#" -eq 1 ]; then
            build_data_bus "${DIR_BUILD}" "Debug"
            exit 0
        fi

        case $(echo "$2" | awk '{print tolower($0)}') in
            "all" ) build_data_bus "${DIR_BUILD}" "Debug";;
            "test" ) build_data_bus_test "${DIR_BUILD}" 0;;
            * ) echo -e "unknown or unsupported target for build: \"$2\"\nSee \"$0 help\""
        esac;;
    "release" )
        if [ "$#" -eq 1 ]; then
            build_data_bus "${DIR_DEPLOY}" "Release"
            exit 0
        fi

        case $(echo "$2" | awk '{print tolower($0)}') in
            "all" ) build_data_bus "${DIR_DEPLOY}" "Release";;
            "image" ) build_data_bus_and_pack_docker "${DIR_DEPLOY}";;
            * ) echo -e "unknown or unsupported target for release: \"$2\"\nSee \"$0 help\""
        esac;;
    "pack" ) build_data_bus_and_pack_docker "${DIR_DEPLOY}";;
    "test" ) build_data_bus_test "${DIR_BUILD}" 1;;
    "help" ) echo -e "${message_help}";;
    * ) echo -e "unknown command: \"$1\"\nSee \"$0 help\""
esac
