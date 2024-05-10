add_executable(FitWorker
    worker.cpp)
target_include_directories(FitWorker PUBLIC ${COMMON_INCLUDE})
target_link_libraries(FitWorker PRIVATE
    FitRuntime
    dl
    pthread
)

add_custom_command(TARGET FitWorker
    COMMAND ${CMAKE_COMMAND} -E copy ${FIT_SRC_PATH}/configuration/*  ${FIT_BIN_DIR}/
)

add_executable(FitWorkerClient
    worker_client.cpp)
target_include_directories(FitWorkerClient PUBLIC ${COMMON_INCLUDE})
target_link_libraries(FitWorkerClient PRIVATE
    FitRuntime
    dl
    pthread
)
