set(FrameworkTestSrc
    ${FIT_UT_SRC_DIR}/framework/formatter_repo_test.cpp
    ${FIT_UT_SRC_DIR}/framework/formatter_service_test.cpp
    ${FIT_UT_SRC_DIR}/framework/json_param_formatter_service_test.cpp
    ${FIT_UT_SRC_DIR}/framework/fitable_discovery_default_impl_test.cpp
    ${FIT_UT_SRC_DIR}/framework/plugin_activator_test.cpp
    ${FIT_UT_SRC_DIR}/framework/annotation/fitable_registrar_test.cpp
    ${FIT_UT_SRC_DIR}/framework/annotation/fitable_collector_test.cpp
    ${FIT_UT_SRC_DIR}/framework/annotation/fitable_detail_test.cpp
    ${FIT_UT_SRC_DIR}/framework/component/value_test.cpp
    ${FIT_UT_SRC_DIR}/framework/component/array_value_test.cpp
    ${FIT_UT_SRC_DIR}/framework/component/object_value_test.cpp
    ${FIT_UT_SRC_DIR}/framework/formatter/json_converter_test.cpp
    ${FIT_UT_SRC_DIR}/framework/formatter/protobuf_converter_test.cpp
    ${FIT_UT_SRC_DIR}/framework/formatter/formatter_collector_test.cpp
    )
add_executable(UTFrameworkTest ${FrameworkTestSrc})
target_link_libraries(UTFrameworkTest PRIVATE $<BUILD_INTERFACE:fit_cmake_exe_base>
    ${LIB_GMOCK_MAIN}
    -lprotobuf-lite FitFramework FitUtil FitBrokerClient FitRuntime securec
    )
set_target_properties(UTFrameworkTest PROPERTIES RUNTIME_OUTPUT_NAME UTFrameworkTest)
# set_target_properties(UTFrameworkTest PROPERTIES LINK_FLAGS ${FIT_LINK_OPTIONS})
set_target_properties(UTFrameworkTest PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})

add_test(
    NAME UTFrameworkTest
    COMMAND ${FIT_TEST_BIN_DIR}/UTFrameworkTest
    WORKING_DIRECTORY ${FIT_TEST_BIN_DIR})

message("${FIT_TEST_BIN_DIR}/UTFrameworkTest")

add_dependencies(check UTFrameworkTest)