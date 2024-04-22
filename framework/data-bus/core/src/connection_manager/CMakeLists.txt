add_library(databus_connection ConnectionManager.cpp
        Connection.cpp)
add_dependencies(databus_connection databus_common flatbuffers)
target_link_libraries(databus_connection PUBLIC flatbuffers databus_runtime)
target_include_directories(databus_connection PUBLIC
        ${DATABUS_SRC_PATH}/common
        ${DATABUS_SRC_PATH}/resource_manager
)