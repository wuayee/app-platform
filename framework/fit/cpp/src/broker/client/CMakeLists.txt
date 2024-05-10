include_directories(
    ${FIT_INCLUDE_PATH}
    ${FIT_INCLUDE_PATH}/fit/internal/runtime/config
    ${FIT_SRC_PATH}
    ${FIT_SRC_PATH}/runtime
    ${FIT_SRC_PATH}/broker/client
    ${FIT_SRC_PATH}/broker/client/domain
    ${FIT_SRC_PATH}/broker/client/application/gateway
    ${FIT_SRC_PATH}/broker/client/application/use_case
    ${FIT_SRC_PATH}/broker/client/adapter/south/gateway
)

#objs 定义
add_library(FitBrokerClientObj OBJECT)
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitBrokerClientObj PRIVATE -fno-exceptions)
endif ()

#添加子目录
add_subdirectory(adapter)
add_subdirectory(application)
add_subdirectory(domain)
add_subdirectory(fitable)

#添加链接
add_library(FitBrokerClientLink INTERFACE)
target_link_libraries(FitBrokerClientLink INTERFACE)

#编译动态库
add_library(FitBrokerClient SHARED )
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitBrokerClient PRIVATE -fno-exceptions)
endif ()
target_link_libraries(FitBrokerClient PRIVATE $<BUILD_INTERFACE:FitBrokerClientLink> FitBrokerClientObj)
set_target_properties(FitBrokerClient PROPERTIES LIBRARY_OUTPUT_NAME FitBrokerClient)
set_target_properties(FitBrokerClient PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})