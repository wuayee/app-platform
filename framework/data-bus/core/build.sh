#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: entry shell script for build databus core

set -eu

# root directory of the databus core
DIR_ROOT=$(dirname "$(readlink -f "$0")")

# sub directories
DIR_BUILD="${DIR_ROOT}/build"
DIR_SCRIPTS="${DIR_ROOT}/scripts"
DIR_SRC="${DIR_ROOT}/src"
DIR_TEST="${DIR_ROOT}/test"
DIR_THIRD_PARTY="${DIR_ROOT}/third_party"

# Description: build all DataBus files
# Global Variables:
#     DIR_BUILD; DIR_SRC
function build_data_bus() {
    source "${DIR_SCRIPTS}/build_all.sh"
    exec_build_all "${DIR_BUILD}" "${DIR_ROOT}"
}

# Description: build all DataBus test
# Arguments: do run if $1 exits and $1 > 0, otherwise build only
# Global Variables:
#     DIR_BUILD; DIR_SRC
function build_data_bus_test() {
    source "${DIR_SCRIPTS}/build_test.sh"
    exec_build_and_run_test "${DIR_BUILD}" "${DIR_ROOT}" ${1:-0}
}

# no args means build all
if [ "$#" -eq 0 ]; then
    build_data_bus
    exit 0
fi

message_help="\nUsage: $0 COMMAND SUBCOMMAND"

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
            build_data_bus
            exit 0
        fi

        case $(echo "$2" | awk '{print tolower($0)}') in
            "all" ) build_data_bus;;
            "test" ) build_data_bus_test;;
            * ) echo -e "unknown subcommand for make: \"$2\"\nSee \"$0 help\""
        esac;;
    "test" ) build_data_bus_test 1;;
    "help" ) echo -e "${message_help}";;
    * ) echo -e "unknown command: \"$1\"\nSee \"$0 help\""
esac