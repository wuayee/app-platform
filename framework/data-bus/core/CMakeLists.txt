cmake_minimum_required(VERSION 3.13)

set(PACKAGE_NAME "DataBus")

project(${PACKAGE_NAME} LANGUAGES C CXX)

set(CMAKE_BINARY_DIR ${CMAKE_CURRENT_SOURCE_DIR}/build)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

message(STATUS "CMAKE_BINARY_DIR: ${CMAKE_BINARY_DIR}")

set(CMAKE_CXX_FLAGS "-Wall -Wextra -pedantic")

set(DATABUS_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/)
set(DATABUS_SRC_PATH ${DATABUS_ROOT_DIR}/src)
set(DATABUS_THIRD_PARTY_PATH ${DATABUS_ROOT_DIR}/third_party)

# C++11约束
if (NOT DEFINED CMAKE_CXX_STANDARD)
    set(CMAKE_CXX_STANDARD 11)
else ()
    if (CMAKE_CXX_STANDARD LESS 11)
        message(FATAL_ERROR "CMAKE_CXX_STANDARD is less than 11, please specify at least SET(CMAKE_CXX_STANDARD 11)")
    endif ()
endif ()

# Options
option(DATABUS_BUILD_TESTS "build tests" OFF)
option(DATABUS_BUILD_DEPENDENCY "build dependency code" ON)

set(COMMON_INCLUDE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${DATABUS_THIRD_PARTY_PATH}/
)

set(COMPILE_OPTIONS
        -Wall -Werror -fexceptions -fPIC
        --param
        large-function-insns=3478
        -Wno-unused
        -fbranch-target-load-optimize
        -fcx-fortran-rules
        -ffloat-store
        -fgcse-after-reload
        -fgcse-las
        -finline-functions
        -finline-limit=100000000
        -fipa-cp-clone
        -fira-loop-pressure
        -fno-branch-count-reg
        -fno-delete-null-pointer-checks
        -fno-forward-propagate
        -fno-gcse
        -fno-if-conversion
        -fno-ipa-cp
        -fno-omit-frame-pointer
        -fno-sched-interblock
        -fno-sched-spec-insn-heuristic
        -fno-schedule-insns
        -fno-schedule-insns2
        -fno-tree-dominator-opts
        -fno-tree-sink
        -fno-var-tracking
        -fno-web
        -fno-strict-aliasing
        -fpredictive-commoning
        -freschedule-modulo-scheduled-loops
        -fsched2-use-superblocks
        -fsel-sched-reschedule-pipelined
        -ftree-loop-distribute-patterns
        -funswitch-loops
        -fvariable-expansion-in-unroller
        -fstack-protector-all
)

add_subdirectory(src)

# todo: clean this
if (DATABUS_BUILD_TESTS)
    enable_testing()

    set(TEST_INCLUDE
            ${CMAKE_CURRENT_SOURCE_DIR}/test
    )

    set(gtest_libs gtest gmock)
    if (CMAKE_BUILD_TYPE STREQUAL "Debug")
        set(gtest_libs gtestd gmockd)
    endif ()

    add_custom_target(
            cov
            COMMAND lcov --rc lcov_branch_coverage=1 --directory . --zerocounters # clear previous test caches
            COMMAND ${CMAKE_MAKE_PROGRAM} check # run tests
            COMMAND lcov --capture --rc lcov_branch_coverage=1 --directory . --output-file cover.info --test-name DATABUS_test
            --quiet
            COMMAND lcov --rc lcov_branch_coverage=1 --remove cover.info "/usr/*" "*/include/*" "*/test/*" "*/third_party/*"
            "*/rapidjson/*" "*/mq/*" "*/configuration_client_impl.*" -output-file cover.info
            COMMAND genhtml cover.info --rc genhtml_branch_coverage=1 --rc lcov_branch_coverage=1 --output-directory
            cover_report --legend --title "DATABUS-TEST" --show-details
            VERBATIM
            WORKING_DIRECTORY ${CMAKE_BINARY_DIR}
            COMMENT "starting code coverage checker...")

    add_subdirectory(test)
endif ()