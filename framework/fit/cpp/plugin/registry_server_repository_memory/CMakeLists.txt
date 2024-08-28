file(GLOB SRCS
    ${CMAKE_CURRENT_SOURCE_DIR}/fitablev2/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/subscriber/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/secure_access/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/util/*.cpp
    )

add_library(FitRegistryServerRepositoryMemory SHARED ${SRCS})

target_compile_options(FitRegistryServerRepositoryMemory PRIVATE -fPIC -fno-strict-aliasing)
set_target_properties(FitRegistryServerRepositoryMemory PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${FIT_LIB_DIR})