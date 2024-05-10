file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp ${CMAKE_CURRENT_SOURCE_DIR}/*.cc)
add_library(FitConverterGJson_fit_heartbeat_subscribeHeartbeat SHARED ${SRC_LIST})

target_include_directories(FitConverterGJson_fit_heartbeat_subscribeHeartbeat
    PRIVATE
    ${CMAKE_SOURCE_DIR}/include
    ${CMAKE_SOURCE_DIR}/include/fit
    ${CMAKE_SOURCE_DIR}/include/component
    ${CMAKE_SOURCE_DIR}/include/genericable
    ${CMAKE_SOURCE_DIR}/include/fit/external
)

target_compile_options(FitConverterGJson_fit_heartbeat_subscribeHeartbeat PRIVATE -fPIC -fno-gnu-unique)
target_link_libraries(FitConverterGJson_fit_heartbeat_subscribeHeartbeat PUBLIC
        FitConverterCJson_fit_registry_registryCommon
        FitConverterCJson_fit_heartbeat_heartbeatCommon
)