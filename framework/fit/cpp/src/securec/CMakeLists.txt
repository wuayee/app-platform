include_directories(
    ${FIT_INCLUDE_PATH}
)

#objs 定义
add_library(securec_obj OBJECT)
if ("${LLT}" STREQUAL "1")
    target_compile_options(securec_obj PRIVATE -fno-exceptions)
endif ()
#添加源代码
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(securec_obj PRIVATE ${SRC_LIST})

#添加链接
add_library(securec_link INTERFACE)
target_link_libraries(securec_link INTERFACE)

#编译动态库
add_library(securec SHARED )
target_link_libraries(securec PRIVATE $<BUILD_INTERFACE:securec_link> securec_obj)
set_target_properties(securec PROPERTIES LIBRARY_OUTPUT_NAME securec)
set_target_properties(securec PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})