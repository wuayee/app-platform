# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: this CMakeLists.txt is for including third_party

# include spdlog
if (EXISTS ${CMAKE_CURRENT_LIST_DIR}/spdlog/CMakeLists.txt)
    message(STATUS "found spdlog under third_party, include it in cmake")
    set(SPDLOG_MASTER_PROJECT OFF)
    set(SPDLOG_USE_STD_FORMAT OFF)
    add_subdirectory(spdlog)
else ()
    message(WARNING "spdlog not found. probably you forget to prepare third_party?")
endif ()

# include flatbuffers
set(flatbuffers_DIR "${CMAKE_CURRENT_LIST_DIR}/flatbuffers/CMake/")
find_package(flatbuffers REQUIRED)
set(FLATBUFFERS_BUILD_FLATC ON)
set(FLATBUFFERS_BUILD_TESTS OFF)
set(FLATBUFFERS_CPP_STD 14)
add_subdirectory(flatbuffers "${CMAKE_CURRENT_BINARY_DIR}/third_party/flatbuffers" EXCLUDE_FROM_ALL)