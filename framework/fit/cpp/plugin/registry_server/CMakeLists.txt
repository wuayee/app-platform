message("-- Fit Plugin: fit_plugin_registry ")

include_directories(
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR})
add_library(FitPlugin_RegistryObj OBJECT)
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitPlugin_RegistryObj PRIVATE ${SRC_LIST})
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitPlugin_RegistryObj PRIVATE -fno-exceptions)
endif ()
add_subdirectory(core)
add_subdirectory(registry_server_memory)
add_subdirectory(registry_repo)
add_subdirectory(v3)

add_library(FitPlugin_registry_server SHARED)
target_link_libraries(FitPlugin_registry_server PRIVATE FitPlugin_RegistryObj)
set_target_properties(FitPlugin_registry_server PROPERTIES LIBRARY_OUTPUT_NAME FitPlugin_registry_server)
set_target_properties(FitPlugin_registry_server PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})
target_compile_options(FitPlugin_registry_server PRIVATE -fPIC -fno-gnu-unique)
add_custom_command(
    TARGET FitPlugin_registry_server
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_server.json ${FIT_LIB_DIR}
    COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_server.genericables.json ${FIT_LIB_DIR}
)
