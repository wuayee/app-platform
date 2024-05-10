set(PLUGIN_NAME fit_crypto_scc)
message("****** Build ${CMAKE_CURRENT_SOURCE_DIR} ${PLUGIN_NAME} ******")
set(SRCS
    crypto_scc_activator.cpp
    scc_client.cpp
)
add_library(${PLUGIN_NAME} SHARED ${SRCS})
target_compile_features(${PLUGIN_NAME} PRIVATE cxx_std_11)
target_include_directories(${PLUGIN_NAME}
    PRIVATE
    ${FIT_INCLUDE}
)

target_compile_options(${PLUGIN_NAME} PRIVATE -fPIC -fno-gnu-unique
)

target_link_libraries(${PLUGIN_NAME} PRIVATE
    ${SEC_COMPONENT_PATH}/libsc-kmc.so
    ${SEC_COMPONENT_PATH}/libsc-sdp.so
    ${SEC_COMPONENT_PATH}/libsc-common.so
    ${SEC_COMPONENT_PATH}/libsc-secrypto.so
)

set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_NAME ${PLUGIN_NAME})
set_target_properties(${PLUGIN_NAME} PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})

add_custom_command(TARGET ${PLUGIN_NAME}
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/lib${PLUGIN_NAME}.json ${FIT_LIB_DIR}
)