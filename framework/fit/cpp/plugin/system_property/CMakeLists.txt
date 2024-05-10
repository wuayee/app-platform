include_directories(
    ${FIT_ROOT_DIR}/include
)
add_library(SystemPropertyObj OBJECT)

file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(SystemPropertyObj PRIVATE  ${SRC_LIST})
if ("${LLT}" STREQUAL "1")
    target_compile_options(SystemPropertyObj PRIVATE -fno-exceptions)
endif ()

add_subdirectory(core)

add_library(FitPlugin_system_property SHARED)
target_link_libraries(FitPlugin_system_property PRIVATE SystemPropertyObj)
target_compile_options(FitPlugin_system_property PRIVATE -fPIC -fno-gnu-unique)

add_custom_command(TARGET FitPlugin_system_property
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_system_property.genericables.json ${FIT_LIB_DIR}
)