add_subdirectory(common)
add_subdirectory(connection_manager)
add_subdirectory(resource_manager)
add_subdirectory(runtime_manager)
add_subdirectory(task_handler)

set(COMPILE_OPTIONS
        -Wall -Werror -fexceptions -fPIC
        --param
        large-function-insns=3478
        -Wno-unused
        -D_FORTIFY_SOURCE=2 -O2
        -fbranch-target-load-optimize
        -fcx-fortran-rules
        -ffloat-store
        -fgcse-after-reload
        -fgcse-las
        -finline-functions
        -finline-limit=100000000
        -fipa-cp-clone
        -fira-loop-pressure
        -fno-branch-count-reg
        -fno-delete-null-pointer-checks
        -fno-forward-propagate
        -fno-gcse
        -fno-if-conversion
        -fno-ipa-cp
        -fno-omit-frame-pointer
        -fno-sched-interblock
        -fno-sched-spec-insn-heuristic
        -fno-schedule-insns
        -fno-schedule-insns2
        -fno-tree-dominator-opts
        -fno-tree-sink
        -fno-var-tracking
        -fno-web
        -fno-strict-aliasing
        -fpredictive-commoning
        -freschedule-modulo-scheduled-loops
        -fsched2-use-superblocks
        -fsel-sched-reschedule-pipelined
        -ftrapv
        -ftree-loop-distribute-patterns
        -funswitch-loops
        -fvariable-expansion-in-unroller
        -fstack-protector-all
)

set(LINK_OPTIONS
        "-s"
        "-Wl,-z,relro,-z,now"
)

add_executable(databus main.cpp)
target_link_libraries(databus PRIVATE databus_connection databus_resource databus_runtime databus_task)
target_compile_options(databus PUBLIC ${COMPILE_OPTIONS})
target_link_options(databus PRIVATE ${LINK_OPTIONS})
