# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: this CMakeLists.txt is for including third_party

# include flatbuffers
set(flatbuffers_DIR "${CMAKE_CURRENT_LIST_DIR}/flatbuffers/CMake/")
find_package(flatbuffers REQUIRED)
set(FLATBUFFERS_BUILD_FLATC ON)
set(FLATBUFFERS_BUILD_TESTS OFF)
set(FLATBUFFERS_CPP_STD 11)
add_subdirectory(flatbuffers "${CMAKE_CURRENT_BINARY_DIR}/third_party/flatbuffers" EXCLUDE_FROM_ALL)