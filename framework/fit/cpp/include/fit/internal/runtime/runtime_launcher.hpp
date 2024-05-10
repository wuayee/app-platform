/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#ifndef RUNTIME_LAUNCHER_HPP
#define RUNTIME_LAUNCHER_HPP
#include <fit/fit_code.h>
#include <getopt.h>
#include "runtime_factory.hpp"

namespace Fit {
class RuntimeLauncher {
public:
    explicit RuntimeLauncher(RuntimeFactoryPtr &&factory);
    virtual ~RuntimeLauncher() = default;

    static RuntimeFactoryPtr GetRuntimeFactory();

    FitCode Start(const char *runtimeConfigFile, const char *option);
    FitCode Start(int32_t argc, char *argv[]);
    FitCode Stop();
private:
    RuntimeFactoryPtr factory_ {};
};
}

#endif // LAUNCHER_HPP
