/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/14
 * Notes:       :
 */

#ifndef BM_FITABLE_HELPER_HPP
#define BM_FITABLE_HELPER_HPP

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>

namespace Fit {
namespace Benchmark {
static ::Fit::Framework::Annotation::FitableDetailPtr BuildFitableDetail(
    ::Fit::Framework::Annotation::FitableFunctionProxyType func, const char *genericId, const char *fitableId)
{
    auto detail = std::make_shared<::Fit::Framework::Annotation::FitableDetail>(std::move(func));
    detail->SetGenericId(genericId).SetFitableId(fitableId);

    return detail;
}

static void PrepareRedundantFitables(const ::Fit::Framework::FitableDiscoveryPtr &fitableDiscovery, int32_t count)
{
    for (int32_t i = 0; i < count; ++i) {
        Fit::string genericId = std::to_string(i) + "60d16fbd49d546818b38a3b65b8ce57c";
        Fit::string fitableId = "181593b1f8b4490fa67f297a2abddb7b";

        fitableDiscovery->RegisterLocalFitable(::Fit::Framework::Annotation::FitableDetailPtrList {
            BuildFitableDetail(nullptr, genericId.c_str(), fitableId.c_str())});
    }
}
}
}
#endif // BM_FITABLE_HELPER_HPP
