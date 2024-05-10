/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#ifndef RUNTIME_FACTORY_HPP
#define RUNTIME_FACTORY_HPP

#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>

namespace Fit {
class RuntimeFactory {
public:
    RuntimeFactory() = default;
    virtual ~RuntimeFactory() = default;

    virtual FitCode Init(const char *runtimeConfigFile, const Fit::map<Fit::string, Fit::string> &options) = 0;
    virtual FitCode Finit() = 0;

    // 激活失活服务接口，当fitIds为空时，对genericId下所有fitable有效
    virtual void EnableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) = 0;
    virtual void DisableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) = 0;
};

using RuntimeFactoryPtr = std::shared_ptr<RuntimeFactory>;

RuntimeFactoryPtr CreateRuntimeFactory();
}

#endif // RUNTIMEFACTORY_HPP
