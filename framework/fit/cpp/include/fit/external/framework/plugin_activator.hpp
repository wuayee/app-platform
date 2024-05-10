/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/1
 * Notes:       :
 */

#ifndef PLUGIN_ACTIVATOR_HPP
#define PLUGIN_ACTIVATOR_HPP

#include <fit/fit_code.h>
#include <fit/external/plugin/plugin_config.hpp>
#include <fit/external/plugin/plugin_context.hpp>
#include <fit/external/util/registration.hpp>
#include <functional>
#include <memory>
#include <fit/stl/vector.hpp>
#include <utility>

namespace Fit {
namespace Framework {
using Fit::Plugin::PluginContext;
using PluginStartFunc = std::function<FitCode(PluginContext *)>;
using PluginStopFunc = std::function<FitCode()>;

class __attribute__((visibility("default"))) PluginActivator {
public:
    PluginActivator();
    ~PluginActivator();
    PluginActivator(const PluginActivator &) = delete;
    PluginActivator &operator=(const PluginActivator &) = delete;
    PluginActivator(PluginActivator &&) = delete;

    FitCode SetStart(PluginStartFunc start);
    FitCode SetStop(PluginStopFunc stop);

    PluginStartFunc GetStart() const noexcept;
    PluginStopFunc GetStop() const noexcept;

private:
    class Impl;

    std::unique_ptr<Impl> impl_;
};

using PluginActivatorPtr = std::shared_ptr<PluginActivator>;
using PluginActivatorPtrList = Fit::vector<PluginActivatorPtr>;

class __attribute__((visibility("default"))) PluginActivatorCollector {
public:
    static void Register(const PluginActivatorPtrList &val);

    static void UnRegister(const PluginActivatorPtrList &val);
};

class __attribute__((visibility("hidden"))) PluginActivatorPluginCollector {
public:
    PluginActivatorPluginCollector() = default;

    ~PluginActivatorPluginCollector()
    {
        PluginActivatorCollector::UnRegister(activators_);
    }

    static void Register(const PluginActivatorPtrList &activators)
    {
        Instance().AddItems(activators);
        PluginActivatorCollector::Register(activators);
    }

    static PluginActivatorPluginCollector &Instance()
    {
        static PluginActivatorPluginCollector instance;
        return instance;
    }

    void AddItems(const PluginActivatorPtrList &activators)
    {
        activators_.insert(activators_.end(), activators.begin(), activators.end());
    }

private:
    PluginActivatorPtrList activators_{};
};

class __attribute__((visibility("hidden"))) PluginActivatorRegistrar {
public:
    PluginActivatorRegistrar()
    {
        activator_ = std::make_shared<PluginActivator>();
    }

    ~PluginActivatorRegistrar()
    {
        PluginActivatorPluginCollector::Register({std::move(activator_)});
    }

    PluginActivatorRegistrar &SetStart(const PluginStartFunc &start)
    {
        activator_->SetStart([start](PluginContext *ctx) -> FitCode {
            if (start) {
                return start(ctx);
            }
            return FIT_OK;
        });
        return *this;
    }
    PluginActivatorRegistrar &SetStop(const PluginStopFunc &stop)
    {
        activator_->SetStop([stop]() -> FitCode {
            if (stop) {
                return stop();
            }
            return FIT_OK;
        });
        return *this;
    }

private:
    PluginActivatorPtr activator_;
};
}  // namespace Framework
}  // namespace Fit
#endif  // PLUGIN_ACTIVATOR_HPP
