include_directories(
    ${FIT_ROOT_DIR}/include
)

add_library(HeartbeatServerEntityObj OBJECT)

file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(HeartbeatServerEntityObj PRIVATE  ${SRC_LIST})

add_library(FitHeartbeatServerEntity SHARED)
target_link_libraries(FitHeartbeatServerEntity PRIVATE HeartbeatServerEntityObj)
target_compile_options(FitHeartbeatServerEntity PRIVATE -fPIC)