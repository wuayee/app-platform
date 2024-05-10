/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/23 15:46
 */
#ifndef ROUTERFACTORY_HPP
#define ROUTERFACTORY_HPP

#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/framework/param_json_formatter_service.hpp>
#include <configuration_service.h>
#include <fit/stl/memory.hpp>
#include "router_impl.hpp"
#include "fit_config.h"
#include "rule_serializer.hpp"

namespace Fit {
class RouterFactory {
public:
    RouterFactory() = delete;
    static RoutePtr Create(ContextObj ctx,
        const FitConfigPtr &config,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params,
        const Fit::string &environment)
    {
        switch (ContextGetPolicy(ctx)) {
            case POLICY_DEFAULT:
                return DefaultRouter::Build(ctx, config, paramJsonFormatterService, params, environment);
            case POLICY_ALIAS:
                return make_unique<AliasRouter>(ctx, config);
            case POLICY_RULE:
                return RuleRouter::Build(ctx, config, paramJsonFormatterService, params, environment);
            case POLICY_FITABLE_ID:
                return make_unique<FitableIdRouter>(ctx);
            default:
                return nullptr;
        }
    }
};
}

#endif // ROUTERFACTORY_HPP
