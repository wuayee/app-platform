file(GLOB_RECURSE SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp ${CMAKE_CURRENT_SOURCE_DIR}/*.cc)
add_library(FitConverterCJson_fit_hakuna_kernel_shared_Fitable SHARED ${SRC_LIST})

target_include_directories(FitConverterCJson_fit_hakuna_kernel_shared_Fitable
    PRIVATE
    ${CMAKE_SOURCE_DIR}/include
    ${CMAKE_SOURCE_DIR}/include/fit
    ${CMAKE_SOURCE_DIR}/include/component
    ${CMAKE_SOURCE_DIR}/include/genericable
    ${CMAKE_SOURCE_DIR}/include/fit/external
)

target_compile_options(FitConverterCJson_fit_hakuna_kernel_shared_Fitable PRIVATE -fPIC -fno-gnu-unique)
target_link_libraries(FitConverterCJson_fit_hakuna_kernel_shared_Fitable PUBLIC
)