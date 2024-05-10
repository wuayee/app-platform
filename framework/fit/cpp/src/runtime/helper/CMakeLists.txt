file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitRuntimeObjs PRIVATE  ${SRC_LIST})

# target_link_libraries(fit_runtime_objs INTERFACE fit_broker_matata_conf_server_configuration_download)
# add_dependencies(fit_runtime_objs fit_broker_matata_conf_server_configuration_download)