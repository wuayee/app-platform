set(PLUGINS_SRC ${PLUGINS_SRC}
    ${CMAKE_CURRENT_SOURCE_DIR}/sql_builder_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/connection_pool_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_address_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_application_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_fitable_subscribe_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_fitable_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_heartbeat_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_scene_subscribe_test.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/table_service/table_worker_test.cpp
    PARENT_SCOPE
)