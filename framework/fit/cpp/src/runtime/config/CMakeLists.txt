add_library(FitRuntimeConfigValueObj OBJECT)
target_sources(FitRuntimeConfigValueObj PRIVATE
    config_value_rapidjson.cpp
    config_value.cpp
    config_encryptedable_value.cpp
    config_writable_value.cpp
    )
target_sources(FitRuntimeObjs PRIVATE
    configuration_client_v2.cpp
    configuration_repo_impl.cpp
    configuration_service.cpp
    configuration_service_for_config_file.cpp
    configuration_service_for_config_center.cpp
    configuration_service_for_registry_server.cpp
    configuration_service_composite.cpp
    configuration_service_proxy.cpp
    configuration_service_factory.cpp
    system_config_default.cpp
    key_value_config_service_impl.cpp
    genericable_configuration_json_parser.cpp
    genericable_configuration_parser.cpp
    )

 target_link_libraries(FitRuntime PRIVATE FitRuntimeConfigValueObj)