cmake_minimum_required(VERSION 3.13)

set(PACKAGE_NAME "DataBus")

project(${PACKAGE_NAME} LANGUAGES C CXX)

if (NOT DEFINED CMAKE_BINARY_DIR)
    set(CMAKE_BINARY_DIR ${CMAKE_CURRENT_LIST_DIR}/build)
endif ()
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

message(STATUS "CMAKE_BINARY_DIR: ${CMAKE_BINARY_DIR}")

set(CMAKE_CXX_FLAGS "-Wall -Wextra -pedantic")

set(DATABUS_ROOT_DIR ${CMAKE_CURRENT_LIST_DIR})
set(DATABUS_SRC_PATH ${DATABUS_ROOT_DIR}/src)
set(DATABUS_SCRIPT_PATH ${DATABUS_ROOT_DIR}/scripts)
set(DATABUS_THIRD_PARTY_PATH ${DATABUS_ROOT_DIR}/third_party)

# C++14约束
if (NOT DEFINED CMAKE_CXX_STANDARD)
    set(CMAKE_CXX_STANDARD 14)
    set(CMAKE_CXX_STANDARD_REQUIRED ON)
else ()
    if (CMAKE_CXX_STANDARD LESS 14)
        message(FATAL_ERROR "CMAKE_CXX_STANDARD is less than 14, please specify at least SET(CMAKE_CXX_STANDARD 14)")
    endif ()
endif ()
string(TOUPPER ${PROJECT_NAME} PROJECT_NAME_UPPER)
add_compile_definitions(${PROJECT_NAME_UPPER}_CXX_STANDARD=${CMAKE_CXX_STANDARD})

# Options
option(DATABUS_BUILD_TESTS "build tests" OFF)
option(DATABUS_BUILD_DEPENDENCY "build dependency code" ON)

add_subdirectory(third_party)
add_subdirectory(src)

set(DATABUS_DOCKERFILE_PATH ${DATABUS_ROOT_DIR}/../common/docker)
add_custom_target(databus_core_image
        COMMAND ${DATABUS_SCRIPT_PATH}/pack_core_docker.sh $<TARGET_FILE:databus> ${DATABUS_DOCKERFILE_PATH}/core.dockerfile ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}
)
add_dependencies(databus_core_image databus)

if (DATABUS_BUILD_TESTS)
    enable_testing()
    add_subdirectory(test)
endif ()
