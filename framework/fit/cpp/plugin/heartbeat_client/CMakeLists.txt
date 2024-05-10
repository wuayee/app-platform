include_directories(
    ${FIT_ROOT_DIR}/include
)

add_subdirectory(Core)

add_library(HeartbeatClientObj OBJECT)

file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(HeartbeatClientObj PRIVATE  ${SRC_LIST})

add_library(FitPlugin_heartbeat_client SHARED)
target_link_libraries(FitPlugin_heartbeat_client PRIVATE HeartbeatClientObj HeartbeatClientCore)
target_compile_options(FitPlugin_heartbeat_client PRIVATE -fPIC -fno-gnu-unique)

add_custom_command(TARGET FitPlugin_heartbeat_client
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_heartbeat_client.genericables.json ${FIT_LIB_DIR}
    )