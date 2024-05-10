#添加源代码
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitUtilObj PRIVATE  ${SRC_LIST})