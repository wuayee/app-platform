#####################################UTSecurec test
add_executable(UTSecurecTest
    ${FIT_UT_SRC_DIR}/ut/src/securec/securec_test.cpp
)
target_include_directories(UTSecurecTest PRIVATE
    ${FIT_SRC_DIR}/include
)
target_link_libraries(UTSecurecTest PUBLIC $<BUILD_INTERFACE:fit_cmake_exe_base>
    ${LIB_GMOCK_MAIN}
    FitRuntimeMocker
    FitFramework FitUtil FitBrokerClient FitRuntime securec gmock gtest_main
    )

set_target_properties(UTSecurecTest PROPERTIES RUNTIME_OUTPUT_NAME UTSecurecTest)
set_target_properties(UTSecurecTest PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})
add_test(
    NAME UTSecurecTest
    COMMAND ${FIT_TEST_BIN_DIR}/UTSecurecTest
    WORKING_DIRECTORY ${FIT_TEST_BIN_DIR})