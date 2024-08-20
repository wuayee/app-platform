set(PLUGIN_NAME registry_server_repository_pg)
message("****** Build ${CMAKE_CURRENT_SOURCE_DIR} ${PLUGIN_NAME} ******")
set(SRCS
    "${PLUGIN_NAME}_activator.cpp"
    src/connection_pool.cpp
    src/sql_wrapper/sql_exec_result.cpp
    src/sql_wrapper/sql_connection.cpp
    src/table_service/heartbeat_server/table_heartbeat.cpp
    src/table_service/heartbeat_server/table_scene_subscribe.cpp
    src/table_service/heartbeat_server/table_factory.cpp
    src/table_service/registry_server/table_address.cpp
    src/table_service/registry_server/table_application.cpp
    src/table_service/registry_server/table_factory.cpp
    src/table_service/registry_server/table_fitable.cpp
    src/table_service/registry_server/table_fitable_subscribe.cpp
    src/table_service/registry_server/table_worker.cpp
    src/table_service/secure_access/token_role_repo_by_pg.cpp
    src/table_service/utils/util_by_pg.cpp
)
add_library(${PLUGIN_NAME} SHARED ${SRCS})
target_compile_features(${PLUGIN_NAME} PRIVATE cxx_std_11)
target_include_directories(${PLUGIN_NAME}
    PRIVATE
        ${FIT_INCLUDE}
    PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/src
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${FIT_THIRD_PARTY_PATH}/pgsql/install/include
)

target_compile_options(${PLUGIN_NAME} PRIVATE -fPIC -fno-gnu-unique
)

target_link_directories(${PLUGIN_NAME} PRIVATE ${FIT_THIRD_PARTY_PATH}/pgsql/install/lib)

target_link_libraries(${PLUGIN_NAME}
    PRIVATE
        $<BUILD_INTERFACE:fit_cmake_base> -fvisibility=hidden
        pq
        FitHeartbeatServerEntity
)
set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_NAME ${PLUGIN_NAME})
set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})

file(MAKE_DIRECTORY ${FIT_SQL_DIR})

add_custom_command(TARGET ${PLUGIN_NAME}
    POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_if_different ${CMAKE_CURRENT_SOURCE_DIR}/lib${PLUGIN_NAME}.json ${FIT_LIB_DIR}/lib${PLUGIN_NAME}.json
        COMMAND ${CMAKE_COMMAND} -E copy_if_different ${CMAKE_CURRENT_SOURCE_DIR}/sql/* ${FIT_SQL_DIR}
)