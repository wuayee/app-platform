add_library(databus_resource ResourceManager.cpp FtokArgsGenerator.cpp)
add_dependencies(databus_resource databus_common flatbuffers)
target_link_libraries(databus_resource PUBLIC flatbuffers databus_runtime)
target_include_directories(databus_resource PUBLIC
        ${DATABUS_SRC_PATH}/common
        ${CMAKE_CURRENT_LIST_DIR}
)
