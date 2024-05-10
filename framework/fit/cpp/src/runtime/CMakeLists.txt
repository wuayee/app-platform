include_directories(
    ${FIT_INCLUDE_PATH}
    ${FIT_INCLUDE_PATH}/fit/internal/runtime/config
    ${FIT_SRC_PATH}/
    ${FIT_SRC_PATH}/runtime
    ${FIT_SRC_PATH}/runtime/config
    ${FIT_PLUGINS_DIR}
    ${FIT_PLUGINS_DIR}/genericable
)
add_library(FitRuntime SHARED)
#objs 定义
add_library(FitRuntimeObjs OBJECT)
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitRuntimeObjs PRIVATE -fno-exceptions)
endif ()
# target_link_libraries(FitRuntime_objs PUBLIC $<BUILD_INTERFACE:fit_cmake_base>)

#添加源代码
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/stl/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/elements/*.cpp
    )
target_sources(FitRuntimeObjs PRIVATE  ${SRC_LIST})
target_include_directories(FitRuntimeObjs PRIVATE ${COMMON_INCLUDE})
#添加子目录
add_subdirectory(memory)
add_subdirectory(config)
add_subdirectory(helper)
add_subdirectory(base_plugins)
#添加源代码
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp ${CMAKE_CURRENT_SOURCE_DIR}/stl/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/crypto/*.cpp
)
target_sources(FitRuntimeObjs PRIVATE  ${SRC_LIST})

#添加链接
add_library(FitRuntimeLink INTERFACE)
target_link_libraries(FitRuntimeLink INTERFACE)

#编译动态库
target_link_libraries(FitRuntime PRIVATE $<BUILD_INTERFACE:FitRuntimeLink>  FitRuntimeObjs)
target_link_libraries(FitRuntime PUBLIC
    FitUtil
    FitBrokerClient
    FitBrokerServer
    FitFramework
    FitPluginManager
    dl
    pthread
)
add_dependencies(FitRuntime FitUtil FitBrokerClient FitBrokerServer FitFramework)

set_target_properties(FitRuntime PROPERTIES LIBRARY_OUTPUT_NAME FitRuntime)
set_target_properties(FitRuntime PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})