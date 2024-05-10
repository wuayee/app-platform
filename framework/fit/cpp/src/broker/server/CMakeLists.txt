file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)

#objs 定义
add_library(FitBrokerServerObj OBJECT ${SRC_LIST})
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitBrokerServerObj PRIVATE -fno-exceptions)
endif ()

#添加链接
add_library(FitBrokerServerLink INTERFACE)
target_link_libraries(FitBrokerServerLink INTERFACE)

#编译动态库
add_library(FitBrokerServer SHARED )
target_link_libraries(FitBrokerServer PRIVATE $<BUILD_INTERFACE:FitBrokerServerLink> FitBrokerServerObj)
set_target_properties(FitBrokerServer PROPERTIES LIBRARY_OUTPUT_NAME FitBrokerServer)
set_target_properties(FitBrokerServer PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})