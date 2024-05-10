set(GLOBAL_C_FLAGS "-std=c11 -Wall -Werror")
set(GLOBAL_CXX_FLAGS "-std=c++11 -Wall -Werror -fexceptions -fPIC")

set(CMAKE_SKIP_BUILD_RPATH TRUE)

set(GLOBAL_C_EXTRA_FLAGS "-Wextra -Wno-unused-parameter -Wno-missing-field-initializers -Wno-unused-function -Wno-unused-variable")
set(GLOBAL_CXX_EXTRA_FLAGS "-Wextra -Wno-unused-parameter -Wno-missing-field-initializers")

set(SAFETY_LINK "-Wl,-z,relro,-z,now,-z,noexecstack -s -fPIE")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKES_FLAGS} -fPIE -pie")

set(GLOBAL_C_DEBUG_FLAGS "-O0 -g -ggdb3")
set(GLOBAL_C_RELEASE_FLAGS "-O3")

set(GLOBAL_CXX_DEBUG_FLAGS "-O0 -g -ggdb3")
set(GLOBAL_CXX_RELEASE_FLAGS "-O3")

# version macro
add_definitions("-DFIT_VERSION=${VERSION}")

# __FILENAME__ is a macro stand for the file name without path
# set(FILENAME_MACRO "-D__FILENAME__='\"$(lastword $(subst /, ,$(abspath $<)))\"'")

# C final flags
set(CMAKE_C_FLAGS_DEBUG "${GLOBAL_C_DEBUG_FLAGS}")
string(APPEND CMAKE_C_FLAGS_DEBUG " ${SAFETY_LINK}")
set(CMAKE_C_FLAGS_RELEASE "${GLOBAL_C_RELEASE_FLAGS}")
string(APPEND CMAKE_C_FLAGS_RELEASE " ${SAFETY_LINK}")

set(CMAKE_C_FLAGS "${GLOBAL_C_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${GLOBAL_C_EXTRA_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${FILENAME_MACRO}")
string(APPEND CMAKE_C_FLAGS " ${COMPILER_C_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${BITWIDE_C_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${PROJECT_C_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${PLATFORM_C_FLAGS}")
string(APPEND CMAKE_C_FLAGS " ${SAFETY_LINK}")

# CXX final flags
set(CMAKE_CXX_FLAGS_DEBUG "${GLOBAL_CXX_DEBUG_FLAGS}")
set(CMAKE_CXX_FLAGS_RELEASE "${GLOBAL_CXX_RELEASE_FLAGS}")

string(APPEND CMAKE_CXX_FLAGS " ${GLOBAL_CXX_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${GLOBAL_CXX_EXTRA_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${FILENAME_MACRO}")
string(APPEND CMAKE_CXX_FLAGS " ${COMPILER_CXX_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${BITWIDE_CXX_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${PROJECT_CXX_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${PLATFORM_CXX_FLAGS}")
string(APPEND CMAKE_CXX_FLAGS " ${SAFETY_LINK}")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-redundant-decls")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-unused-variable")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-unused-function")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-old-style-cast")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-shadow")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-pedantic")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-strict-aliasing")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-redundant-move")

if(FIT_BUILD_TESTS AND "${CMAKE_BUILD_TYPE}" STREQUAL "Debug")
   set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O0 -Wall --coverage -fno-omit-frame-pointer -fno-optimize-sibling-calls")
endif()
