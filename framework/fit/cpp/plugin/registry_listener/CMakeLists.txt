include_directories(
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

add_library(registry_listener_src OBJECT)
file(GLOB_RECURSE SRC_FILES ${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp)
target_sources(registry_listener_src PRIVATE ${SRC_FILES})
target_compile_options(registry_listener_src PRIVATE -fPIC)

add_library(registry_listener_facade OBJECT)
file(GLOB_RECURSE FACADE_FILES ${CMAKE_CURRENT_SOURCE_DIR}/facade/*.cpp)
target_sources(registry_listener_facade PRIVATE ${FACADE_FILES})
target_compile_options(registry_listener_facade PRIVATE -fPIC)

if ("${LLT}" STREQUAL "1")
    target_compile_options(registry_listener_src PRIVATE -fno-exceptions)
    target_compile_options(registry_listener_facade PRIVATE -fno-exceptions)
endif ()

add_library(FitPlugin_registry_listener SHARED)
target_link_libraries(FitPlugin_registry_listener PRIVATE
    registry_listener_src
    registry_listener_facade)
target_compile_options(FitPlugin_registry_listener PRIVATE -fPIC -fno-gnu-unique)

add_custom_command(TARGET FitPlugin_registry_listener
    POST_BUILD
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_listener.json ${FIT_LIB_DIR}
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/libFitPlugin_registry_listener.genericables.json ${FIT_LIB_DIR}
    )