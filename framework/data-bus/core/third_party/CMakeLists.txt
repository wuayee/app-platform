# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: this CMakeLists.txt is for including third_party

# include spdlog
if (EXISTS ${CMAKE_CURRENT_LIST_DIR}/spdlog/CMakeLists.txt)
    message(STATUS "found spdlog under third_party, include it in cmake")
    set(SPDLOG_MASTER_PROJECT OFF)
    set(SPDLOG_USE_STD_FORMAT OFF)
    add_subdirectory(spdlog EXCLUDE_FROM_ALL)
else ()
    message(WARNING "spdlog not found. probably you forget to prepare third_party?")
endif ()

# include json
if (EXISTS ${CMAKE_CURRENT_LIST_DIR}/json/CMakeLists.txt)
    message(STATUS "found json under third_party, include it in cmake")
    add_subdirectory(json EXCLUDE_FROM_ALL)
else ()
    message(WARNING "json not found. probably you forget to prepare third_party?")
endif ()

# include flatbuffers
set(flatbuffers_DIR "${CMAKE_CURRENT_LIST_DIR}/flatbuffers/CMake/")
find_package(flatbuffers REQUIRED)
set(FLATBUFFERS_BUILD_FLATC ON)
set(FLATBUFFERS_BUILD_TESTS OFF)
set(FLATBUFFERS_CPP_STD 14)
add_subdirectory(flatbuffers "${CMAKE_CURRENT_BINARY_DIR}/third_party/flatbuffers" EXCLUDE_FROM_ALL)

# include googletest
if (DATABUS_BUILD_TESTS)
    if (EXISTS ${CMAKE_CURRENT_LIST_DIR}/googletest/CMakeLists.txt)
        message(STATUS "found googletest under third_party, include it in cmake")
        add_subdirectory(googletest EXCLUDE_FROM_ALL)
        set_property(TARGET gtest_main PROPERTY CXX_STANDARD 14)
        set_property(TARGET gmock_main PROPERTY CXX_STANDARD 14)
    else ()
        message(FATAL_ERROR "googletest not found. probably you forget to prepare third_party?")
    endif ()
endif()