add_executable(databus_test
        ${CMAKE_CURRENT_LIST_DIR}/TestMain.cpp
        ${CMAKE_CURRENT_LIST_DIR}/resource_manager/FtokArgsGeneratorTest.cpp
)
add_dependencies(databus_test databus_common)
target_link_libraries(databus_test databus_resource gtest_main gmock_main)

add_test(NAME databus_test COMMAND databus_test)
