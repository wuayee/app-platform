add_executable(main main.cpp)
target_compile_options(main PUBLIC ${COMPILE_OPTIONS})

add_subdirectory(common)
add_subdirectory(connection_manager)
add_subdirectory(resource_manager)
add_subdirectory(runtime_manager)