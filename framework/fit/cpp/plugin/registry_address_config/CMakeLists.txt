include_directories(
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}
)
add_library(RegistryAddressObj OBJECT)

file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(RegistryAddressObj PRIVATE  ${SRC_LIST})

add_library(FitPlugin_registry_address_config SHARED)
target_link_libraries(FitPlugin_registry_address_config PRIVATE RegistryAddressObj)
target_compile_options(FitPlugin_registry_address_config PRIVATE -fPIC  -fno-gnu-unique)

add_custom_command(TARGET FitPlugin_registry_address_config
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_address_config.json ${FIT_LIB_DIR}
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_address_config.genericables.json ${FIT_LIB_DIR}
    )
