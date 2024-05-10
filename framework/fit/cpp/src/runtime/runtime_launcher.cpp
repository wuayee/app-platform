/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#include <fit/external/runtime/fit_runtime.h>
#include <fit/internal/runtime/runtime_launcher.hpp>
#include <fit/internal/util/command_line_util.hpp>

static Fit::RuntimeLauncher& GetLauncherInstance()
{
    static auto *launcher = new Fit::RuntimeLauncher(Fit::CreateRuntimeFactory());
    return *launcher;
}

namespace Fit {
RuntimeLauncher::RuntimeLauncher(RuntimeFactoryPtr &&factory)
    : factory_(factory) {}

FitCode RuntimeLauncher::Stop()
{
    if (factory_) {
        factory_->Finit();
    }

    return FIT_OK;
}

FitCode RuntimeLauncher::Start(const char *runtimeConfigFile, const char *option)
{
    if (!factory_) {
        return FIT_ERR_FAIL;
    }
    auto options = Fit::CommandLineUtil::GetOpt(option);

    return factory_->Init(runtimeConfigFile, options);
}

FitCode RuntimeLauncher::Start(int32_t argc, char *argv[])
{
    if (!factory_) {
        return FIT_ERR_FAIL;
    }
    auto options = Fit::CommandLineUtil::GetOpt(argc, argv);
    return factory_->Init(options["config_file"].c_str(), options);
}

RuntimeFactoryPtr RuntimeLauncher::GetRuntimeFactory()
{
    return GetLauncherInstance().factory_;
}
}

int32_t FitRuntimeStart(const char* runtimeConfigFile)
{
    return FitRuntimeStartWithOption(runtimeConfigFile, "");
}

int32_t FitRuntimeStartWithCommandLine(int32_t argc, char *argv[])
{
    FitCode launcherRet = GetLauncherInstance().Start(argc, argv);
    if (launcherRet != FIT_OK) {
        std::cout << "Launcher start failed, code = " << std::hex << launcherRet << std::endl;
        return launcherRet;
    }
    std::cout << "Launcher start success." << std::endl;

    return launcherRet;
}

int32_t FitRuntimeStartWithOption(const char *runtimeConfigFile, const char *option)
{
    FitCode launcherRet = GetLauncherInstance().Start(runtimeConfigFile, option);
    if (launcherRet != FIT_OK) {
        std::cout << "Launcher start failed, code = " << std::hex << launcherRet << std::dec << std::endl;
        return launcherRet;
    }

    return FIT_OK;
}

int32_t FitRuntimeStop()
{
    return GetLauncherInstance().Stop();
}
