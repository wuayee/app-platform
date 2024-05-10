include_directories(
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/core
)

add_library(HeartbeatServerObj OBJECT)

file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(HeartbeatServerObj PRIVATE  ${SRC_LIST})

add_library(FitPlugin_heartbeat_server SHARED)
target_link_libraries(FitPlugin_heartbeat_server PRIVATE HeartbeatServerObj)
target_compile_options(FitPlugin_heartbeat_server PRIVATE -fPIC)

add_custom_command(TARGET FitPlugin_heartbeat_server
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_heartbeat_server.genericables.json ${FIT_LIB_DIR}
    )