/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 插件入口
 * Author       : songyongtan
 * Create       : 2022-07-08
 */

#include <chrono>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/vector.hpp>
#include <gtest/gtest.h>
#include <thread>

#include "config.h"
#include "module_entry.h"
#include "fit_code.h"

using namespace Fit;
using namespace RegistryTest;
namespace RegistryTest {
std::thread task;
FitCode Start(::Fit::Framework::PluginContext* context)
{
    task = std::thread([context]() {
        std::this_thread::sleep_for(std::chrono::seconds(3)); // 3 延迟启动等待引擎启动完成
        ModuleEntry::Instance().Start(context->GetConfig().get());
        auto& config = ModuleEntry::Instance().GetConfig();
        vector<char*> gtestArgs;
        for (auto& arg : config.GetGtestArgs()) {
            gtestArgs.push_back((char*)arg.c_str());
        }
        int argc = gtestArgs.size();
        testing::InitGoogleTest(&argc, &gtestArgs[0]);

        auto ret = RUN_ALL_TESTS();
        FIT_LOG_INFO("Finish to run all tests. (ret=%d).", ret);
        task.detach();
        std::exit(ret);
    });

    return FIT_OK;
}
FitCode Stop()
{
    if (task.joinable()) {
        task.join();
    }
    return FIT_OK;
}
}  // namespace

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar().SetStart(Start).SetStop(Stop);
}