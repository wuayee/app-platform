project(FitCppBenchmark)
cmake_minimum_required(VERSION 3.12)

if(NOT DEFINED CMAKE_CXX_STANDARD)
    set(CMAKE_CXX_STANDARD 11)
else()
    if(CMAKE_CXX_STANDARD LESS 11)
        message(FATAL_ERROR "CMAKE_CXX_STANDARD is less than 11, please specify at least SET(CMAKE_CXX_STANDARD 11)")
    endif()
endif()

file(GLOB BENCHMARK_SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
add_executable(benchmarkTest
    ${BENCHMARK_SRC_LIST}
    )
set(BENCHMARK_LIBS)
find_package(benchmark QUIET)
if(benchmark_FOUND)
    message("===found benchmark package===")
    set(BENCHMARK_LIBS "benchmark::benchmark_main")
else()
    message("===use benchmark library===")
    set(BENCHMARK_LIBS "-lbenchmark_main")
endif()

target_include_directories(benchmarkTest PRIVATE
    ${FIT_INCLUDE}
    ${FIT_SRC_PATH}
    ${FIT_SRC_PATH}/broker/client
    ${FIT_SRC_PATH}/broker/client/application/gateway
    )
target_link_libraries(benchmarkTest PUBLIC
    ${BENCHMARK_LIBS}
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

add_subdirectory(performance_test)
