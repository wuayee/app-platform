include_directories(
    ${FIT_INCLUDE_PATH}
)

#objs 定义
add_library(FitUtilObj OBJECT)
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitUtilObj PRIVATE -fno-exceptions)
endif ()
#添加源代码
file(GLOB SRC_LIST
    ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/stl/*.cpp)
target_sources(FitUtilObj PRIVATE  ${SRC_LIST})

add_subdirectory(log)
add_subdirectory(context)
add_subdirectory(protocol)
add_subdirectory(thread)

#添加链接
add_library(FitUtilLink INTERFACE)
target_link_libraries(FitUtilLink INTERFACE)

#编译动态库
add_library(FitUtil SHARED )
target_link_libraries(FitUtil PRIVATE $<BUILD_INTERFACE:FitUtilLink> securec FitUtilObj)
set_target_properties(FitUtil PROPERTIES LIBRARY_OUTPUT_NAME FitUtil)
set_target_properties(FitUtil PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})