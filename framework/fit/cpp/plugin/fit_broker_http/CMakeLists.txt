set(PLUGIN_NAME FitPlugin_broker_http)
message("****** Build ${CMAKE_CURRENT_SOURCE_DIR} ${PLUGIN_NAME} ******")
file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
add_library(${PLUGIN_NAME} SHARED ${SRC_LIST})
target_compile_features(${PLUGIN_NAME} PRIVATE cxx_std_11)
target_include_directories(${PLUGIN_NAME}
    PRIVATE
    ${FIT_INCLUDE}
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_definitions(${PLUGIN_NAME} PRIVATE
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:CPPHTTPLIB_OPENSSL_SUPPORT>
)

target_compile_options(${PLUGIN_NAME} PRIVATE -fPIC -fno-gnu-unique
)

target_link_libraries(${PLUGIN_NAME} PRIVATE
    $<BUILD_INTERFACE:fit_cmake_base> -fvisibility=hidden
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:ssl>
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:crypto>
    $<$<BOOL:${FIT_HTTP_ENABLE_SSL}>:curl>
)

set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_NAME ${PLUGIN_NAME})
set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})

add_custom_command(TARGET ${PLUGIN_NAME}
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/lib${PLUGIN_NAME}.json ${FIT_LIB_DIR}
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/lib${PLUGIN_NAME}.genericables.json ${FIT_LIB_DIR}
)