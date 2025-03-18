add_library(databus_task TaskHandler.cpp TaskLoop.cpp)
add_dependencies(databus_task databus_common flatbuffers databus_connection)
target_link_libraries(databus_task PUBLIC flatbuffers databus_runtime databus_connection)
target_include_directories(databus_task PUBLIC
        ${DATABUS_SRC_PATH}/common
        ${DATABUS_SRC_PATH}/resource_manager
        ${DATABUS_SRC_PATH}/connection_manager
        ${DATABUS_SRC_PATH}/runtime_manager
        ${CMAKE_CURRENT_LIST_DIR}
)
