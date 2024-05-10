file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp ${CMAKE_CURRENT_SOURCE_DIR}/*.cc)
add_library(FitConverterGJson_fit_hakuna_kernel_registry_server_query_worker_detail SHARED ${SRC_LIST})

target_include_directories(FitConverterGJson_fit_hakuna_kernel_registry_server_query_worker_detail
    PRIVATE
)

target_compile_options(FitConverterGJson_fit_hakuna_kernel_registry_server_query_worker_detail PRIVATE -fPIC -fno-gnu-unique)
target_link_libraries(FitConverterGJson_fit_hakuna_kernel_registry_server_query_worker_detail PUBLIC
        FitConverterCJson_fit_hakuna_kernel_registry_shared_Worker
        FitConverterCJson_fit_hakuna_kernel_registry_shared_Application
        FitConverterCJson_fit_hakuna_kernel_registry_shared_FitableMeta
)