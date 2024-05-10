add_library(ActivatorTestPlugin SHARED ${CMAKE_CURRENT_SOURCE_DIR}/test_activator.cpp)
target_compile_options(ActivatorTestPlugin PRIVATE -fPIC -std=c++11)
target_link_libraries(ActivatorTestPlugin PRIVATE $<BUILD_INTERFACE:fit_cmake_base>)

add_custom_command(
    TARGET ActivatorTestPlugin
    POST_BUILD
    COMMAND cmake -E copy ${CMAKE_CURRENT_SOURCE_DIR}/libActivatorTestPlugin.json ${FIT_TEST_LIB_DIR})

set_target_properties(ActivatorTestPlugin PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_TEST_LIB_DIR})
