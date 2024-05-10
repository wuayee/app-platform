project(fit_registry_test)

cmake_minimum_required(VERSION 3.10)

file(GLOB SRC_LIST
    module_entry.cpp
    config.cpp
    case/register_test.cpp
    case/unregister_test.cpp
    case/subscribe_test.cpp
    facade/fitable_suite.cpp
)

add_library(fit_registry_test SHARED
    ${SRC_LIST})

target_include_directories(fit_registry_test PRIVATE $<TARGET_PROPERTY:fit_cmake_base,INTERFACE_INCLUDE_DIRECTORIES>
    ${FIT_ROOT_DIR}/third_party/googletest/googletest/include
    ${FIT_ROOT_DIR}/third_party/googletest/googlemock/include
    ${CMAKE_CURRENT_SOURCE_DIR}
)
target_link_libraries(fit_registry_test PRIVATE $<BUILD_INTERFACE:fit_cmake_base>
    gtest gmock)

set_target_properties(fit_registry_test PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})