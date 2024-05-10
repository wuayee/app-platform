file(GLOB PLUGIN_SRCS ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
add_library(HeartbeatClientCore OBJECT  ${PLUGIN_SRCS})
