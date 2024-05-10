file(GLOB_RECURSE runtime_tests ${FIT_UT_SRC_DIR}/runtime/*.cpp)

file(GLOB_RECURSE broker_tests ${FIT_UT_SRC_DIR}/broker/client/*.cpp)
file(GLOB_RECURSE context_src ${FIT_UT_SRC_DIR}/util/context/*.cpp)
file(GLOB_RECURSE protocol_test_src ${FIT_UT_SRC_DIR}/util/protocol/*.cpp)
file(GLOB util_src ${FIT_UT_SRC_DIR}/util/*.cpp
    ${FIT_UT_SRC_DIR}/util/log/*.cpp
    ${FIT_UT_SRC_DIR}/util/thread/*.cpp
    )

add_executable(UTFitRuntimeTest
    ${runtime_tests}
    ${broker_tests}
    ${context_src}
    ${protocol_test_src}
    ${util_src}
)
target_link_libraries(UTFitRuntimeTest PUBLIC $<BUILD_INTERFACE:fit_cmake_exe_base>
    ${LIB_GMOCK_MAIN}
    FitRuntimeMocker
    FitFramework FitUtil FitBrokerClient FitRuntime securec
    )
set_target_properties(UTFitRuntimeTest PROPERTIES RUNTIME_OUTPUT_NAME UTFitRuntimeTest)
set_target_properties(UTFitRuntimeTest PROPERTIES LINK_FLAGS "${FIT_LINK_OPTIONS}")
set_target_properties(UTFitRuntimeTest PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})
add_custom_command(TARGET UTFitRuntimeTest
    COMMAND cp ${FIT_UT_SRC_DIR}/runtime/config/configuration_file_test.json ${FIT_TEST_BIN_DIR}/
)

add_test(
    NAME UTFitRuntimeTest
    COMMAND ${FIT_TEST_BIN_DIR}/UTFitRuntimeTest
    WORKING_DIRECTORY ${FIT_TEST_BIN_DIR})

add_dependencies(check UTFitRuntimeTest)