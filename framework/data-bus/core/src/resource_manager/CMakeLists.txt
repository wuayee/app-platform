add_library(databus_resource ResourceManager.cpp FtokArgsGenerator.cpp)
target_include_directories(databus_resource PUBLIC
        ${DATABUS_SRC_PATH}/common
        ${CMAKE_CURRENT_LIST_DIR}
)
