cmake_minimum_required(VERSION 3.13)

set(PACKAGE_NAME "FitCpp")
set(PACKAGE_VERSION "0.0.1")

find_program(CCACHE_PROGRAM ccache)
if(CCACHE_PROGRAM)
    set_property(GLOBAL PROPERTY RULE_LAUNCH_COMPILE "${CCACHE_PROGRAM}")
endif()

project(${PACKAGE_NAME} LANGUAGES C CXX)

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

message(==== "${FIT_THIRD_PARTY_OUTPUT_DIR}" ====)
if(DEFINED FIT_THIRD_PARTY_OUTPUT_DIR)
    list(APPEND CMAKE_PREFIX_PATH "${FIT_THIRD_PARTY_OUTPUT_DIR}")
endif()

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(FIT_EXPORT_INCLUDE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/include/fit/external)
set(FIT_LIB_DIR ${CMAKE_BINARY_DIR}/lib)
set(FIT_BIN_DIR ${CMAKE_BINARY_DIR}/bin)
set(FIT_CONF_DIR ${CMAKE_BINARY_DIR}/conf)
set(FIT_SQL_DIR ${CMAKE_BINARY_DIR}/sql)
set(FIT_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(FIT_PLUGINS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/plugin)

set(CMAKE_CXX_FLAGS "-Wall -Wextra -pedantic")

set(FIT_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/)
set(FIT_SRC_PATH ${FIT_ROOT_DIR}/src)
set(FIT_INCLUDE_PATH ${FIT_ROOT_DIR}/include)
set(CMAKE_PATH ${FIT_ROOT_DIR}/cmake)
set(FIT_THIRD_PARTY_PATH ${FIT_ROOT_DIR}/third_party)
set(FIT_PROTOC_OPTION "--experimental_allow_proto3_optional")

if(NOT DEFINED CMAKE_CXX_STANDARD)
    set(CMAKE_CXX_STANDARD 11)
else()
    if(CMAKE_CXX_STANDARD LESS 11)
        message(FATAL_ERROR "CMAKE_CXX_STANDARD is less than 11, please specify at least SET(CMAKE_CXX_STANDARD 11)")
    endif()
endif()

# Options
option(FIT_BUILD_SAMPLES "build samples" OFF)
option(FIT_BUILD_TESTS "build tests" ON)
option(FIT_ENABLE_BENCHMARK "build benchmark" OFF)
option(FIT_BUILD_GENERATED_CODE "build generated code" ON)
option(FIT_OPEN_ASAN "open asan" OFF)
option(FIT_BUILD_DEPENDENCY "build dependency code" ON)
option(FIT_HTTP_ENABLE_SSL "http ssl option" ON)
option(FIT_ENABLE_GRPC "grpc option" OFF)
option(FIT_ENABLE_PROTOBUF "protobuf option" OFF)
option(FIT_ENABLE_ODBC "odbc option" OFF)
option(FIT_ENABLE_PGSQL "pgsql option" OFF)
option(FIT_ENABLE_LIBRDKAFKA "librdkafka option" OFF)
option(FIT_ENABLE_KMS "kms option" OFF)
option(FIT_ENABLE_SCC "scc option" ON)

include(${CMAKE_PATH}/toolchain.cmake)

if(FIT_BUILD_TESTS)
    set(FIT_ENABLE_GRPC ON)
    set(FIT_ENABLE_PROTOBUF ON)
endif()

set(COMMON_INCLUDE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include/external
    ${CMAKE_CURRENT_SOURCE_DIR}/include/fit
    ${CMAKE_CURRENT_SOURCE_DIR}/include/fit/internal
    ${FIT_THIRD_PARTY_PATH}/
    ${FIT_THIRD_PARTY_PATH}/rapidjson/include
)
if(DEFINED FIT_THIRD_PARTY_OUTPUT_DIR)
    list(APPEND COMMON_INCLUDE ${FIT_THIRD_PARTY_OUTPUT_DIR}/include)
endif()

set(FIT_INCLUDE
    ${CMAKE_CURRENT_SOURCE_DIR}/include)

add_library(fit_cmake_base INTERFACE)
target_include_directories(fit_cmake_base INTERFACE
    ${COMMON_INCLUDE}
    ${CMAKE_CURRENT_SOURCE_DIR}/src
    ${FIT_PLUGINS_DIR}/fit_dependency
    ${FIT_PLUGINS_DIR}/fit_dependency/conf
    ${FIT_PLUGINS_DIR}/fit_dependency/registry
    )
target_compile_options(fit_cmake_base INTERFACE
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

if(DEFINED FIT_THIRD_PARTY_OUTPUT_DIR)
    target_link_libraries(fit_cmake_base INTERFACE
        -L${FIT_THIRD_PARTY_OUTPUT_DIR}/lib
        -L${FIT_THIRD_PARTY_OUTPUT_DIR}/lib64
    )
endif()
if (FIT_OPEN_ASAN)
    target_compile_options(fit_cmake_base INTERFACE
        -fsanitize=address
        )
    target_link_libraries(fit_cmake_base INTERFACE
        -lasan
        )
endif ()

include_directories(${COMMON_INCLUDE} $<TARGET_PROPERTY:fit_cmake_base,INTERFACE_INCLUDE_DIRECTORIES>)
add_compile_options($<TARGET_PROPERTY:fit_cmake_base,INTERFACE_COMPILE_OPTIONS>)
link_libraries($<TARGET_PROPERTY:fit_cmake_base,INTERFACE_LINK_LIBRARIES>)

add_subdirectory(src)
add_subdirectory(plugin)

if (FIT_BUILD_SAMPLES)
    message("Build samples")
    add_subdirectory(samples)
endif()

if(FIT_BUILD_TESTS)
    enable_testing()
    add_subdirectory(third_party/googletest)

    set(TEST_INCLUDE
        ${CMAKE_CURRENT_SOURCE_DIR}/test
        ${CMAKE_CURRENT_SOURCE_DIR}/third_party/googletest/googletest/include
        ${CMAKE_CURRENT_SOURCE_DIR}/third_party/googletest/googlemock/include
        ${CMAKE_CURRENT_SOURCE_DIR}/plugin/fit_config_center/include
        )

    set(gtest_libs gtest gmock)
    if (CMAKE_BUILD_TYPE STREQUAL "Debug")
        set(gtest_libs gtestd gmockd)
    endif()

    add_custom_target(
        cov
        COMMAND lcov --rc lcov_branch_coverage=1 --directory . --zerocounters # clear previous test caches
        COMMAND ${CMAKE_MAKE_PROGRAM} check # run tests
        COMMAND lcov --capture --rc lcov_branch_coverage=1 --directory . --output-file cover.info --test-name fit_test
        --quiet
        COMMAND lcov --rc lcov_branch_coverage=1 --remove cover.info "/usr/*" "*/include/*" "*/test/*" "*/third_party/*"
        "*.pb.h" "*.pb.cc" "*/rapidjson/*" "*/mq/*" "*/fit_dependency/*" "*/configuration_client_impl.*"
        -output-file cover.info
        COMMAND genhtml cover.info --rc genhtml_branch_coverage=1 --rc lcov_branch_coverage=1 --output-directory
        cover_report --legend --title "FIT-TEST" --show-details
        VERBATIM
        WORKING_DIRECTORY ${CMAKE_BINARY_DIR}
        COMMENT "starting code coverage checker...")

    add_subdirectory(test)
endif()

if(FIT_ENABLE_BENCHMARK)
    add_subdirectory(benchmark)
endif()