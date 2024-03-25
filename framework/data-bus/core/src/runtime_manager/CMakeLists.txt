add_library(databus_runtime STATIC
        log/LoggerWrapper.cpp
        config/ConfigParser.cpp)
target_include_directories(databus_runtime PUBLIC ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(databus_runtime PUBLIC spdlog::spdlog_header_only)