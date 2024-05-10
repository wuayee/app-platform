add_subdirectory(${FIT_UT_SRC_DIR}/plugin/test_plugins)

add_library(valid_library SHARED ${FIT_UT_SRC_DIR}/plugin/test_plugins/valid_library/valid_library.cpp)
add_library(valid_library_1 SHARED ${FIT_UT_SRC_DIR}/plugin/test_plugins/valid_library/valid_library_1.cpp)
set_target_properties(valid_library PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_TEST_LIB_DIR})
set_target_properties(valid_library_1 PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_TEST_LIB_DIR})

add_executable(UTPluginManagerTest
    ${FIT_UT_SRC_DIR}/plugin/library_loader_test.cpp
    ${FIT_UT_SRC_DIR}/plugin/plugin_config_test.cpp
    ${FIT_UT_SRC_DIR}/plugin/plugin_library_test.cpp
    ${FIT_UT_SRC_DIR}/plugin/plugin_manager_impl_test.cpp
    )
add_dependencies(UTPluginManagerTest valid_library valid_library_1
    ActivatorTestPlugin
    FitPluginMath
    )
target_link_libraries(UTPluginManagerTest PUBLIC $<BUILD_INTERFACE:fit_cmake_exe_base>
    ${LIB_GMOCK_MAIN}
    FitFramework FitUtil FitBrokerClient FitRuntime
    )
set_target_properties(UTPluginManagerTest PROPERTIES RUNTIME_OUTPUT_NAME UTPluginManagerTest)
set_target_properties(UTPluginManagerTest PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})
add_custom_command(
    TARGET UTPluginManagerTest
    POST_BUILD
    COMMAND cmake -E copy ${FIT_UT_SRC_DIR}/plugin/plugin_config_test.json ${FIT_TEST_BIN_DIR}
    COMMAND cmake -E copy ${FIT_UT_SRC_DIR}/plugin/plugin_config_test_invalid.json ${FIT_TEST_BIN_DIR}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})

add_test(
    NAME UTPluginManagerTest
    COMMAND ${FIT_TEST_BIN_DIR}/UTPluginManagerTest
    WORKING_DIRECTORY ${FIT_TEST_BIN_DIR})

add_dependencies(check UTPluginManagerTest)