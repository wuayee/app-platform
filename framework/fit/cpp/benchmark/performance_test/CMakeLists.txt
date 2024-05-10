file(GLOB_RECURSE PERFORMANCE_SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)

add_executable(performance_test ${PERFORMANCE_SRC_LIST})

target_include_directories(performance_test PRIVATE
    ${FIT_INCLUDE}
    ${FIT_SRC_PATH}
    )
target_link_libraries(performance_test PUBLIC
    benchmark::benchmark
    -L${FIT_LIB_DIR}
    -L/usr/local/lib
    -lprotobuf-lite
    FitUtil
    FitBrokerClient
    FitFramework
    FitRuntime
    -lpthread
    -fuse-ld=gold
    )
