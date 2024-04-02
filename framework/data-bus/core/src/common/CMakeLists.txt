# collecting all the fbs files under the FLATBUFFERS_SCHEMA_DIR folder
# convert to aimed headers' path under GENERATED_DIR and store in GENERATED_FILES
set(FLATBUFFERS_SCHEMA_DIR "${DATABUS_ROOT_DIR}/../common/fbs")
set(GENERATED_DIR "${CMAKE_CURRENT_SOURCE_DIR}/fbs")

if(NOT EXISTS "${GENERATED_DIR}")
    file(MAKE_DIRECTORY "${GENERATED_DIR}")
endif()

file(GLOB FLATBUFFERS_SCHEMA_FILES "${FLATBUFFERS_SCHEMA_DIR}/*.fbs")
set(GENERATED_FILES)
foreach (SCHEMA_FILE IN LISTS FLATBUFFERS_SCHEMA_FILES)
    get_filename_component(FILE_NAME "${SCHEMA_FILE}" NAME_WE)
    get_filename_component(ABSOLUTE_PATH "${GENERATED_DIR}/${FILE_NAME}_generated.h" ABSOLUTE)
    list(APPEND GENERATED_FILES "${ABSOLUTE_PATH}")
endforeach ()

set(DATABUS_EXCEPTION ${CMAKE_CURRENT_SOURCE_DIR}/exception/databus_exception.h)

# auto generate headers on make stage. let databus_common depends on that
add_custom_target(databus_common DEPENDS ${GENERATED_FILES} ${DATABUS_STL} ${DATABUS_EXCEPTION})
if (GENERATED_FILES)
    add_custom_command(
            OUTPUT ${GENERATED_FILES}
            PRE_BUILD
            COMMAND ${DATABUS_SCRIPT_PATH}/generate_flatbuffers.sh "$<TARGET_FILE:flatc>" "${FLATBUFFERS_SCHEMA_DIR}" "${GENERATED_DIR}"
            WORKING_DIRECTORY ${GENERATED_DIR}
    )
else()
    message(WARNING "no fbs files to generate under ${FLATBUFFERS_SCHEMA_DIR}")
endif()
