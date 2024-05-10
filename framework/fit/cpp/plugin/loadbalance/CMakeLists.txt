include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${FIT_ROOT_DIR}/include
)
add_library(LoadBalanceObj OBJECT)

file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/facade/*.cpp ${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp)
#file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(LoadBalanceObj PRIVATE  ${SRC_LIST})
if ("${LLT}" STREQUAL "1")
    target_compile_options(LoadBalanceObj PRIVATE -fno-exceptions)
endif ()

add_library(FitPlugin_loadbalance SHARED)
target_link_libraries(FitPlugin_loadbalance PRIVATE LoadBalanceObj)
target_compile_options(FitPlugin_loadbalance PRIVATE -fPIC -fno-gnu-unique)

add_custom_command(TARGET FitPlugin_loadbalance
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_loadbalance.genericables.json ${FIT_LIB_DIR}
    )