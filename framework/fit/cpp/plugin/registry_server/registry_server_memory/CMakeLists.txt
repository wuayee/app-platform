
add_subdirectory(common)
add_subdirectory(fitable)
add_subdirectory(subscriber)
file(GLOB SRCS ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitPlugin_RegistryObj PRIVATE  ${SRCS})