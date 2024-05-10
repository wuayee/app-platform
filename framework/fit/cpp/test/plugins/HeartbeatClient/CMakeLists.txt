add_executable(FitHeartbeatClientTest HeartbeatClientTest.cpp)
target_link_libraries(FitHeartbeatClientTest PRIVATE FitTestMain FitRuntime gmock)
target_include_directories(FitHeartbeatClientTest PRIVATE
    ${FIT_PLUGINS_DIR}/genericable
    ${FIT_PLUGINS_DIR}/Component/
)