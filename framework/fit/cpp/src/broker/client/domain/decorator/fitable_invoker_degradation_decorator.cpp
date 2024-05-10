/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker degradation decorator.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#include "fitable_invoker_degradation_decorator.hpp"

#include <fit/external/util/string_utils.hpp>
#include <fit/fit_log.h>
#include "fitable_invoker_factory.hpp"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

FitableInvokerDegradationDecorator::FitableInvokerDegradationDecorator(std::unique_ptr<FitableInvoker> decorated)
    : FitableInvokerEmptyDecorator(std::move(decorated))
{
}

FitCode FitableInvokerDegradationDecorator::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    auto ret = FitableInvokerDecoratorBase::Invoke(context, in, out);
    if (ret != FIT_OK) {
        auto factory = GetFactory();
        vector<string> chain {};
        chain.push_back(GetCoordinate()->GetFitableId());
        string degradationFitableId = GetConfig()->GetDegradation(GetCoordinate()->GetFitableId());
        while (!degradationFitableId.empty() && ret != FIT_OK) {
            if (exist(chain.begin(), chain.end(), [&degradationFitableId](const string& fitableId) -> bool {
                return fitableId == degradationFitableId;
                })) {
                FIT_LOG_WARN("Cycle degradation occurs. [genericable=%s, chain=%s, error=%x]",
                    GetCoordinate()->GetGenericableId().c_str(), StringUtils::Join("->", chain).c_str(), ret);
                break;
            }
            auto coordinate = FitableCoordinate::Custom()
                .SetGenericableId(GetCoordinate()->GetGenericableId())
                .SetGenericableVersion(GetCoordinate()->GetGenericableVersion())
                .SetFitableId(degradationFitableId)
                .SetFitableVersion(GetCoordinate()->GetFitableVersion())
                .Build();
            chain.push_back(std::move(degradationFitableId));
            auto invoker = factory->GetRawInvoker(coordinate, GetConfig());
            ret = invoker->Invoke(context, in, out);
        }
    }
    return ret;
}
