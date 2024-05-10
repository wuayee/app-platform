add_library(FitPluginMath SHARED fitable_add.cpp)

target_include_directories(FitPluginMath
    PRIVATE
    ${FIT_ROOT_DIR}/include
    ${FIT_ROOT_DIR}/include/external
    ${FIT_ROOT_DIR}/include/stl
)

target_compile_options(FitPluginMath PRIVATE -fPIC -std=c++11)
target_link_libraries(FitPluginMath PRIVATE $<BUILD_INTERFACE:fit_cmake_base>)
set_target_properties(FitPluginMath PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_TEST_LIB_DIR})
