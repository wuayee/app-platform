/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/1
 * Notes:       :
 */

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/internal/framework/plugin_activator_collector_inner.hpp>
#include <fit/fit_log.h>
#include <fit/stl/memory.hpp>
#include <mutex>
#include <map>
#include <list>
#include <utility>
#include <algorithm>

namespace {
std::mutex& GetLock()
{
    static auto* instance = new std::mutex;
    return *instance;
}

std::list<Fit::Framework::PluginActivatorPtr>& GetCacheInstance()
{
    static auto* instance = new std::list<Fit::Framework::PluginActivatorPtr> {};
    return *instance;
}

Fit::Framework::PluginActivatorReceiver*& GetReceiver()
{
    static Fit::Framework::PluginActivatorReceiver* instance {};
    return instance;
}
}
namespace Fit {
namespace Framework {
void PluginActivatorCollector::Register(const PluginActivatorPtrList& val)
{
    for (auto& item : val) {
        FIT_LOG_DEBUG("Register activator. [hash=%zx]", std::hash<PluginActivator*>()(item.get()));
    }
    std::lock_guard<std::mutex> guard(GetLock());
    if (GetReceiver()) {
        return GetReceiver()->Register(val);
    }

    for (auto& item : val) {
        GetCacheInstance().push_back(item);
    }
}

void PluginActivatorCollector::UnRegister(const PluginActivatorPtrList& val)
{
    for (auto& item : val) {
        FIT_LOG_DEBUG("Unregister activator. [hash=%zx]", std::hash<PluginActivator*>()(item.get()));
    }
    std::lock_guard<std::mutex> guard(GetLock());
    if (GetReceiver()) {
        return GetReceiver()->UnRegister(val);
    }

    for (auto &item : val) {
        GetCacheInstance().erase(std::find_if(GetCacheInstance().begin(), GetCacheInstance().end(),
            [&item](const PluginActivatorPtr &activator) { return activator.get() == item.get(); }),
            GetCacheInstance().end());
    }
}

PluginActivatorPtrList __attribute__ ((visibility ("default"))) PopPluginActivatorCache()
{
    std::lock_guard<std::mutex> guard(GetLock());
    PluginActivatorPtrList result;
    result.reserve(GetCacheInstance().size());
    for (auto& item : GetCacheInstance()) {
        result.push_back(item);
    }
    GetCacheInstance().clear();

    return result;
}

__attribute__ ((visibility ("default"))) PluginActivatorReceiver* PluginActivatorFlowTo(PluginActivatorReceiver* target)
{
    std::lock_guard<std::mutex> guard(GetLock());
    PluginActivatorReceiver* old = GetReceiver();
    GetReceiver() = target;

    return old;
}

class PluginActivator::Impl {
public:
    FitCode SetStart(PluginStartFunc start)
    {
        start_ = std::move(start);
        return FIT_OK;
    }
    FitCode SetStop(PluginStopFunc stop)
    {
        stop_ = std::move(stop);
        return FIT_OK;
    }

    PluginStartFunc GetStart() const noexcept
    {
        return start_;
    }
    PluginStopFunc GetStop() const noexcept
    {
        return stop_;
    }

private:
    PluginStartFunc start_;
    PluginStopFunc stop_;
};

PluginActivator::PluginActivator()
    : impl_(make_unique<Impl>()) {}

PluginActivator::~PluginActivator() = default;

FitCode PluginActivator::SetStart(PluginStartFunc start)
{
    return impl_->SetStart(std::move(start));
}

FitCode PluginActivator::SetStop(PluginStopFunc stop)
{
    return impl_->SetStop(std::move(stop));
}

PluginStartFunc PluginActivator::GetStart() const noexcept
{
    return impl_->GetStart();
}

PluginStopFunc PluginActivator::GetStop() const noexcept
{
    return impl_->GetStop();
}
}
}
