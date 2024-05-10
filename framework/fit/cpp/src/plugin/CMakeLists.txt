add_library(FitPluginManager OBJECT
    library_loader_impl.cpp
    plugin_manager_impl.cpp
    plugin_config_impl.cpp
    plugin_context_impl.cpp
    plugin_library.cpp
    )
if ("${LLT}" STREQUAL "1")
    target_compile_options(FitPluginManager PRIVATE -fno-exceptions)
endif ()

target_include_directories(FitPluginManager PRIVATE ${FIT_INCLUDE_PATH})
target_link_libraries(FitPluginManager PUBLIC dl FitUtil FitFramework)
