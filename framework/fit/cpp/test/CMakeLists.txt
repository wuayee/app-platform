set(FIT_TEST_LIB_DIR ${FIT_LIB_DIR})
set(FIT_TEST_BIN_DIR ${FIT_BIN_DIR})
set(FIT_UT_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(FIT_LINK_OPTIONS "")

set(LIB_GMOCK_MAIN gmock_main)

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${FIT_SRC_DIR}/include
    ${FIT_SRC_DIR}/include/fit
    ${FIT_SRC_DIR}/include/fit/internal
    ${FIT_SRC_DIR}/include/fit/internal/runtime/config/
    ${FIT_SRC_DIR}/include/fit/external
    ${FIT_SRC_DIR}/plugin/fit_dependency
    ${FIT_SRC_DIR}/plugin/fit_dependency/registry
    ${FIT_SRC_DIR}/plugin/fit_dependency/conf
    ${FIT_SRC_DIR}/src/broker/client/application/gateway
    ${FIT_SRC_DIR}/src/broker/client/application/use_case
    ${FIT_SRC_DIR}/src/broker/client/application/use_case/router
    ${FIT_SRC_DIR}/src/broker/client/adapter/south/gateway
    ${FIT_SRC_DIR}/src/broker/client/adapter/north
    ${FIT_SRC_DIR}/src/runtime/
    ${FIT_SRC_DIR}/src/runtime/config/
    ${FIT_SRC_DIR}/plugin/base_plugins/repositoryCcdb/configuration_center/export
    ${FIT_SRC_DIR}/plugin/base_plugins/repositoryCcdb/configuration_center/ccdb_persistence_base
    ${FIT_SRC_DIR}/plugin/base_plugins/repositoryCcdb/configuration_center/lib)
add_library(fit_cmake_exe_base INTERFACE)

add_custom_target(check
    # run tests
    COMMAND GTEST_COLOR=1 ctest --output-on-failure
    WORKING_DIRECTORY ${CMAKE_BINARY_DIR}
    COMMENT "starting fit tester ..."
)

include(ut_cmake/ut_plugin_manager.cmake)
include(ut_cmake/ut_framework.cmake)
include(ut_cmake/ut_runtime.cmake)
include(ut_cmake/ut_securec.cmake)

MACRO(add_fit_test test_name)
    add_test(NAME ${test_name} COMMAND ${FIT_TEST_BIN_DIR}/${test_name} WORKING_DIRECTORY ${FIT_TEST_BIN_DIR}/)
    add_dependencies(check ${test_name})
ENDMACRO()

#####################################plugins test
file(GLOB_RECURSE plugins_tests ${CMAKE_CURRENT_SOURCE_DIR}/plugins/*.cpp)
add_executable(PluginsTest
    FitPluginsTest.cpp
    ${plugins_tests}
)
set_target_properties(PluginsTest PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})
add_fit_test(PluginsTest)
add_custom_command(TARGET PluginsTest
    COMMAND cp ${CMAKE_CURRENT_SOURCE_DIR}/Environment/worker_config_plugins_test.json ${FIT_TEST_BIN_DIR}/
    )

macro(copy_if_exists src dst)
    if(EXISTS ${src})
        add_custom_command(TARGET PluginsTest
            COMMAND ${CMAKE_COMMAND} -E copy_directory ${src} ${dst}
        )
    endif()
endmacro()
if(DEFINED FIT_THIRD_PARTY_OUTPUT_DIR)
    copy_if_exists(${FIT_THIRD_PARTY_OUTPUT_DIR}/lib ${FIT_TEST_LIB_DIR})
    copy_if_exists(${FIT_THIRD_PARTY_OUTPUT_DIR}/lib64 ${FIT_TEST_LIB_DIR})
endif()

target_include_directories(PluginsTest PRIVATE
    ${FIT_PLUGINS_DIR}
    ${FIT_PLUGINS_DIR}/registry_listener
    ${FIT_PLUGINS_DIR}/genericable
    ${FIT_PLUGINS_DIR}/Component/RegistryCommon
    ${FIT_PLUGINS_DIR}/Component/
)

target_link_libraries(PluginsTest PRIVATE
    FitFramework FitUtil FitBrokerClient FitRuntime
    pthread
    gmock
)

add_library(FitRuntimeMocker SHARED mock/runtime_mock.cpp)
target_include_directories(FitRuntimeMocker PRIVATE ${TEST_INCLUDE})
target_compile_options(FitRuntimeMocker PRIVATE "-fPIC")
target_link_libraries(FitRuntimeMocker PRIVATE
    FitRuntime
    FitFramework
    FitBrokerClient
    FitUtil
    )

add_subdirectory(ut/plugins)
add_subdirectory(fit_registry_test)