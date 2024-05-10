add_subdirectory(src)
file(GLOB SRCS ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitPlugin_RegistryObj PRIVATE  ${SRCS})