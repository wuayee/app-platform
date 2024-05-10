add_library(FitFramework SHARED)

if ("${LLT}" STREQUAL "1")
    target_compile_options(FitFramework PRIVATE -fno-exceptions)
endif ()

file(GLOB FitFrameworkComponentSrc
    ${CMAKE_CURRENT_SOURCE_DIR}/component/*.cpp
    )
add_library(FitFrameworkComponentObj OBJECT)
target_sources(FitFrameworkComponentObj PRIVATE ${FitFrameworkComponentSrc})

if(FIT_ENABLE_PROTOBUF)
    find_package(protobuf REQUIRED)
    target_link_libraries(FitFramework PUBLIC
        protobuf::libprotobuf-lite)
    target_compile_definitions(FitFramework PRIVATE FIT_ENABLE_PROTOBUF)

    execute_process(COMMAND sh ${CMAKE_CURRENT_SOURCE_DIR}/formatter/generate_serialization.sh
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/formatter/)
    execute_process(COMMAND bash -c "cp -r ${CMAKE_CURRENT_SOURCE_DIR}/formatter/serialization.pb.h ${FIT_EXPORT_INCLUDE_DIR}/framework/formatter/"
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/formatter/)
    target_sources(FitFramework PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/formatter/serialization.pb.cc
        ${CMAKE_CURRENT_SOURCE_DIR}/formatter/protobuf_formatter_entry.cpp
    )
endif()

file(GLOB FitFrameworkSrc ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/formatter/formatter_collector.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/formatter/json_converter.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/formatter/json_formatter_entry.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/annotation/*.cpp
    )
target_sources(FitFramework PRIVATE
    ${FitFrameworkSrc}
    )
target_include_directories(FitFramework PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${COMMON_INCLUDE}
    ${FIT_PLUGINS_DIR}/Component/RegistryCommon
    ${CMAKE_CURRENT_SOURCE_DIR}/include/stl
    )
target_link_libraries(FitFramework PUBLIC
    FitFrameworkComponentObj
    FitBrokerClient)