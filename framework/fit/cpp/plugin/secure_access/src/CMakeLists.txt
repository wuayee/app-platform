file(GLOB SRCS ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp)
target_sources(FitPlugin_SecureAccessObj PRIVATE  ${SRCS})

add_subdirectory(repo)
add_subdirectory(signature)
add_subdirectory(client)