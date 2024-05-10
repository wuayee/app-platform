/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#include <csignal>
#include <execinfo.h>
#include <iostream>
#include <mutex>
#include <condition_variable>
#include <fit/external/runtime/fit_runtime.h>
#include <fit/fit_log.h>

#define SIG_EXIT() abort()

static void SigHandler(int signum)
{
    void* buffer[1024];
    int size = backtrace(buffer, 1024);
    char** strings = backtrace_symbols(buffer, size);

    printf("Received signal %d\n", signum);
    for (int i = 0; i < size; i++) {
        printf("%s\n", strings[i]);
    }
    FIT_LOG_ERROR("Received signal %d.", signum);
    FIT_LOG_ERROR("======================== start print ======================");
    for (int i = 0; i < size; i++) {
        FIT_LOG_ERROR("[%d] %s", i, strings[i]);
    }
    FIT_LOG_ERROR("======================== end print ======================");
    free(strings);
    FitLogFlush();
    SIG_EXIT();
}

int main(int argc, char* argv[])
{
    signal(SIGSEGV, SigHandler);
    FitCode launcherRet = FitRuntimeStartWithCommandLine(argc, argv);
    if (launcherRet != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Start runtime error! ret:%x.", launcherRet);
        return launcherRet;
    }

    bool exitFlag = false;
    std::mutex mtx;
    std::condition_variable cv;
    std::unique_lock<std::mutex> ul(mtx);
    cv.wait(ul, [&] { return exitFlag; });

    FitRuntimeStop();

    return 0;
}
