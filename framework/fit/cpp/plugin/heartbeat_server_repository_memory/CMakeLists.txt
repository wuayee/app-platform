include_directories(
    ${FIT_ROOT_DIR}/include
)

add_library(HeartbeatServerRepositoryTestObj OBJECT)

file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(HeartbeatServerRepositoryTestObj PRIVATE  ${SRC_LIST})

add_library(FitHeartbeatServerRepositoryMemory SHARED)
target_link_libraries(FitHeartbeatServerRepositoryMemory PRIVATE HeartbeatServerRepositoryTestObj)
target_compile_options(FitHeartbeatServerRepositoryMemory PRIVATE -fPIC)