add_subdirectory(common)
add_subdirectory(connection_manager)
add_subdirectory(resource_manager)
add_subdirectory(runtime_manager)

add_executable(databus main.cpp)
target_link_libraries(databus PRIVATE databus_connection databus_runtime)
target_compile_options(databus PUBLIC ${COMPILE_OPTIONS})