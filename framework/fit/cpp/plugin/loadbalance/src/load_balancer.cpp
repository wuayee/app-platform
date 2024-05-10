/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for load balancer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/27
 */

#include <load_balancer.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/mutex.hpp>
#include <fitable_endpoint.hpp>

#include <fit/fit_log.h>
#include <fit/external/util/string_utils.hpp>
#include <fit/internal/util/vector_utils.hpp>
#include <fit/internal/util/fit_random.h>

using namespace Fit;
using namespace Fit::LoadBalance;
using namespace Fit::Util;

namespace {
/**
 * 获取一个随机数。
 *
 * @param bounds 表示随机数的上限，不包含在有效值域。
 * @return 表示随机数的值。
 */
size_t GetRandom(size_t bounds)
{
    if (bounds == 0) {
        return 0;
    }

    return static_cast<size_t>(FitRandom()) % bounds;
}

class DefaultLoadBalancer : public virtual LoadBalancer {
public:
    DefaultLoadBalancer(ContextObj context, const Fitable& fitable,
        const vector<ApplicationInstance>& targets);
    ~DefaultLoadBalancer() override = default;
    FitCode LoadBalance() override;
    ApplicationInstance* GetResult() override;
private:
    ContextObj context_;
    const Fitable& fitable_;
    ApplicationInstance* result_ {nullptr};
    vector<FitableEndpoint> targetEndpoints_ {};

    static map<size_t, size_t> fitableIndexes_;
    static mutex mutex_;
    static size_t NextIndex(const Fitable& fitable, size_t count);
};
}

map<size_t, size_t> DefaultLoadBalancer::fitableIndexes_ {};
mutex DefaultLoadBalancer::mutex_ {};

std::unique_ptr<LoadBalancer> LoadBalancer::Create(ContextObj context, const Fitable& fitable,
    const vector<ApplicationInstance>& targets)
{
    return make_unique<DefaultLoadBalancer>(context, fitable, targets);
}

DefaultLoadBalancer::DefaultLoadBalancer(ContextObj context, const Fitable& fitable,
    const vector<ApplicationInstance>& targets) : context_(context), fitable_(fitable)
{
    targetEndpoints_ = FitableEndpoint::Flat(targets);
}

FitCode DefaultLoadBalancer::LoadBalance()
{
    if (targetEndpoints_.empty()) {
        FIT_LOG_ERROR("No endpoints for fitable to select.");
        return FIT_ERR_NOT_FOUND;
    }
    auto endpoint = &targetEndpoints_[NextIndex(fitable_, targetEndpoints_.size())];
    FIT_LOG_DEBUG("Use remote endpoint. [genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s, "
                "worker=%s, environment=%s, host=%s, port=%d, protocol=%d]",
        fitable_.genericableId.c_str(), fitable_.genericableVersion.c_str(), fitable_.fitableId.c_str(),
        fitable_.fitableVersion.c_str(), endpoint->GetWorker()->id.c_str(),
        endpoint->GetWorker()->environment.c_str(), endpoint->GetAddress()->host.c_str(),
        endpoint->GetEndpoint()->port, endpoint->GetEndpoint()->protocol);
    result_ = endpoint->CreateApplicationInstance(context_);
    return FIT_OK;
}

ApplicationInstance* DefaultLoadBalancer::GetResult()
{
    return result_;
}

size_t DefaultLoadBalancer::NextIndex(const Fitable& fitable, size_t count)
{
    size_t hash = StringUtils::ComputeHash(StringUtils::Join(':', {
        fitable.genericableId, fitable.genericableVersion, fitable.fitableId, fitable.fitableVersion
    }));
    lock_guard<mutex> guard {mutex_};
    auto iter = fitableIndexes_.find(hash);
    if (iter == fitableIndexes_.end()) {
        size_t value = GetRandom(count);
        fitableIndexes_[hash] = value;
        return value;
    } else {
        iter->second = (iter->second + 1) % count;
        return iter->second;
    }
}
