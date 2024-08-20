message("-- Fit Plugin: fit_plugin_registry ")

include_directories(
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR})
add_library(FitPlugin_SecureAccessObj OBJECT)
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitPlugin_SecureAccessObj PRIVATE ${SRC_LIST})
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitPlugin_SecureAccessObj PRIVATE -fno-exceptions)
endif ()
add_subdirectory(service)
add_subdirectory(src)

add_library(FitPlugin_SecureAccess SHARED)
target_link_libraries(FitPlugin_SecureAccess PRIVATE FitPlugin_SecureAccessObj crypto)
set_target_properties(FitPlugin_SecureAccess PROPERTIES LIBRARY_OUTPUT_NAME FitPlugin_SecureAccess)
set_target_properties(FitPlugin_SecureAccess PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})
target_compile_options(FitPlugin_SecureAccess PRIVATE -fPIC -fno-gnu-unique)
add_custom_command(
    TARGET FitPlugin_SecureAccess
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_SecureAccess.json ${FIT_LIB_DIR}
    COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_SecureAccess.genericables.json ${FIT_LIB_DIR}
)