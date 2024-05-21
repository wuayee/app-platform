add_executable(databus_test
        ${CMAKE_CURRENT_LIST_DIR}/TestMain.cpp
        ${CMAKE_CURRENT_LIST_DIR}/resource_manager/FtokArgsGeneratorTest.cpp
        ${CMAKE_CURRENT_LIST_DIR}/resource_manager/ResourceManagerTest.cpp
        ${CMAKE_CURRENT_LIST_DIR}/common/stl/BlockingQueueTest.cpp
        ${CMAKE_CURRENT_LIST_DIR}/common/utils/DataBusUtilsTest.cpp
        ${CMAKE_CURRENT_LIST_DIR}/common/utils/FileUtilsTest.cpp
)
add_dependencies(databus_test databus_common)
target_link_libraries(databus_test databus_resource gtest_main gmock_main)
target_include_directories(databus_test PRIVATE ${DATABUS_SRC_PATH}/common/stl/)
add_test(NAME databus_test COMMAND databus_test)
