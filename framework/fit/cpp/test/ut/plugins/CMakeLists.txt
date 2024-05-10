file(GLOB_RECURSE PLUGINS_SRC
    fit_broker_http/*.cpp
    registry_listener/*.cpp
    registry_server/*.cpp
    loadbalance/*.cpp
    stub/*.cpp
)

if(TARGET FitRegistryServerRepositoryOdbc)
    LIST(APPEND PLUGINS_SRC registry_server_repository_odbc/json_converter_util_test.cpp)
endif()

if(TARGET db_info_manager_for_kms)
    LIST(APPEND PLUGINS_SRC db_info_manager_for_kms/db_info_manager_for_kms_test.cpp)
    LIST(APPEND PLUGINS_SRC fit_crypto_kms/get_static_token_test.cpp fit_crypto_kms/kms_client_test.cpp)
endif()

if(TARGET registry_server_repository_pg)
    add_subdirectory(registry_server_repository_pg)
endif()

add_executable(UTFitPlugins
    ${PLUGINS_SRC}
)

target_include_directories(UTFitPlugins PUBLIC ${COMMON_INCLUDE}
    ${FIT_SRC_DIR}/plugin/fit_config_center/include
    ${FIT_SRC_DIR}/plugin
    ${FIT_SRC_DIR}/plugin/registry_listener/include
    ${FIT_SRC_DIR}/plugin/registry_server
    ${FIT_SRC_DIR}/include
    ${FIT_SRC_DIR}/test
)
target_compile_definitions(UTFitPlugins PRIVATE
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:CPPHTTPLIB_OPENSSL_SUPPORT>
)
target_link_libraries(UTFitPlugins PUBLIC $<BUILD_INTERFACE:fit_cmake_base>
    ${LIB_GMOCK_MAIN}
    FitFramework
    FitUtil
    FitBrokerClient
    FitRuntime
    FitPlugin_loadbalance
    FitHeartbeatServerEntity
    FitRegistryServerRepositoryMemory
    FitPlugin_registry_server
    FitPlugin_registry_listener
    FitPlugin_broker_http
    $<$<TARGET_EXISTS:db_info_manager_for_kms>:db_info_manager_for_kms>
    $<$<TARGET_EXISTS:fit_crypto_kms>:fit_crypto_kms>
    $<$<TARGET_EXISTS:registry_server_repository_pg>:registry_server_repository_pg>
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:ssl>
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:crypto>
)
set_target_properties(UTFitPlugins PROPERTIES RUNTIME_OUTPUT_NAME UTFitPlugins)
set_target_properties(UTFitPlugins PROPERTIES LINK_FLAGS "${FIT_LINK_OPTIONS}")
set_target_properties(UTFitPlugins PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${FIT_TEST_BIN_DIR})
add_custom_command(TARGET UTFitPlugins
    COMMAND echo "UTPlugins was compiled"
)

add_test(
    NAME UTFitPlugins
    COMMAND ${FIT_TEST_BIN_DIR}/UTFitPlugins
    WORKING_DIRECTORY ${FIT_TEST_BIN_DIR})

add_dependencies(check UTFitPlugins)