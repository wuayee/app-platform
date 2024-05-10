set(COMMON_INCLUDE
    ${FIT_ROOT_DIR}
    ${FIT_ROOT_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/
    ${CMAKE_CURRENT_SOURCE_DIR}/include/proto
    )
file(MAKE_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/include/proto)

include_directories(${COMMON_INCLUDE} $<TARGET_PROPERTY:fit_cmake_base,INTERFACE_INCLUDE_DIRECTORIES>)
add_compile_options($<TARGET_PROPERTY:fit_cmake_base,INTERFACE_COMPILE_OPTIONS>)

if ("${LLT}" STREQUAL "1")
    add_compile_options(-fno-exceptions)
endif ()
macro(add_sub_dirs dir dir_suffix)
    file(GLOB subdir_list RELATIVE ${dir} ${dir}/*)
    foreach(subdir ${subdir_list})
        if (IS_DIRECTORY ${dir}/${subdir} AND EXISTS ${dir}/${subdir}/${dir_suffix}/CMakeLists.txt)
            add_subdirectory(${dir}/${subdir}/${dir_suffix})
        endif()
    endforeach()
endmacro()

set(FIT_PROTO_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/public/proto)
set(FIT_PROTO_GENERATE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/include/proto)

if (FIT_BUILD_GENERATED_CODE)
    add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/component 1.0.0/cplusplus/formatter/json)
    add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/genericable 1.0.0/cplusplus/formatter/json)
    add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/registry/genericable 1.0.0/cplusplus/formatter/json)
    add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/conf/genericable 1.0.0/cplusplus/formatter/json)
    if (FIT_ENABLE_PROTOBUF)
        add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/component 1.0.0/cplusplus/formatter/protobuf)
        add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/genericable 1.0.0/cplusplus/formatter/protobuf)
        add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/registry/genericable 1.0.0/cplusplus/formatter/protobuf)
        add_sub_dirs(${CMAKE_CURRENT_SOURCE_DIR}/conf/genericable 1.0.0/cplusplus/formatter/protobuf)
    endif()
endif()
