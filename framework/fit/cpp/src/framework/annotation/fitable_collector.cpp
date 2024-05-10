/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */
#include <fit/external/framework/annotation/fitable_collector.hpp>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <fit/stl/map.hpp>
#include <fit/fit_log.h>

namespace Fit {
namespace Framework {
namespace Annotation {
static Fit::map<void *, FitableDetailPtr> &GetCacheInstance()
{
    static auto *instance = new Fit::map<void *, FitableDetailPtr> {};
    return *instance;
}

static FitableDetailReceiver *&GetReceiver()
{
    static FitableDetailReceiver *instance {};
    return instance;
}

void FitableCollector::Register(const FitableDetailPtrList &annotations)
{
    for (auto &item : annotations) {
        FIT_LOG_DEBUG("Register (%s:%s).", item->GetGenericId().c_str(), item->GetFitableId().c_str());
    }

    if (GetReceiver()) {
        return GetReceiver()->Register(annotations);
    }

    for (auto &item : annotations) {
        GetCacheInstance()[item.get()] = item;
    }
}

void FitableCollector::UnRegister(const FitableDetailPtrList &annotations)
{
    for (auto &item : annotations) {
        FIT_LOG_DEBUG("Unregister (%s:%s).", item->GetGenericId().c_str(), item->GetFitableId().c_str());
    }

    if (GetReceiver()) {
        return GetReceiver()->UnRegister(annotations);
    }

    for (auto &item : annotations) {
        GetCacheInstance().erase(item.get());
    }
}

FitableDetailPtrList __attribute__ ((visibility ("default"))) PopFitableDetailCache()
{
    FitableDetailPtrList result;
    result.reserve(GetCacheInstance().size());
    for (auto &item : GetCacheInstance()) {
        result.push_back(item.second);
    }
    GetCacheInstance().clear();

    return result;
}

__attribute__ ((visibility ("default"))) FitableDetailReceiver *FitableDetailFlowTo(FitableDetailReceiver *target)
{
    FitableDetailReceiver *old = GetReceiver();
    GetReceiver() = target;

    return old;
}
}
}
} // LCOV_EXCL_LINE