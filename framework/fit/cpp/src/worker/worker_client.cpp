/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#include <fit/external/runtime/fit_runtime.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <mutex>
#include <condition_variable>
int main(int argc, char* argv[])
{
    FitCode launcherRet = FitRuntimeStart("worker_config.json");
    if (launcherRet != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Start runtime error! ret:%x.", launcherRet);
    }

    bool exitFlag = false;
    std::mutex mtx;
    std::condition_variable cv;
    std::unique_lock<std::mutex> ul(mtx);
    cv.wait(ul, [&] { return exitFlag; });

    FitRuntimeStop();
    return 0;
}
