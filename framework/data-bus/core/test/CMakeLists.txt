add_executable(databus_test
        TestMain.cpp
        resource_manager/FtokArgsGeneratorTest.cpp
        resource_manager/ResourceManagerTest.cpp
        common/stl/BlockingQueueTest.cpp
        common/utils/DataBusUtilsTest.cpp
        common/utils/FileUtilsTest.cpp
        runtime_manager/config/ConfigParserTest.cpp
)
add_dependencies(databus_test databus_common)
target_link_libraries(databus_test databus_resource databus_runtime gtest_main gmock_main)
target_include_directories(databus_test PRIVATE ${DATABUS_SRC_PATH}/common/stl/ mock)
add_test(NAME databus_test COMMAND databus_test)
