/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/21 17:12
 */
#ifndef RULE_ROUTER_H
#define RULE_ROUTER_H

#include "router.hpp"
#include "fit/external/util/context/context_api.hpp"
#include "fit/stl/string.hpp"
#include "rule_serializer.hpp"
#include "fit_config.h"

#include <fit/internal/framework/param_json_formatter_service.hpp>

namespace Fit {
class DefaultRouter : public Router {
public:
    DefaultRouter(ContextObj ctx,
        const FitConfigPtr &config,
        RoutePtr ruleRouter);

    ~DefaultRouter() override = default;
    Fit::string Route() override;

    static std::unique_ptr<DefaultRouter> Build(
        ContextObj ctx,
        const FitConfigPtr &config,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params,
        const Fit::string &environment);

private:
    ContextObj ctx_ {};
    const FitConfigPtr config_ {};
    RoutePtr ruleRoute_ {nullptr};
};

class AliasRouter : public Router {
public:
    AliasRouter(ContextObj ctx,
        const FitConfigPtr &config);

    ~AliasRouter() override = default;

    Fit::string Route() override;
private:
    ContextObj ctx_ {};
    const FitConfigPtr config_ {};
};

class RuleRouter : public Router {
public:
    using RuleExecuteFunctor =
        std::function<Fit::string(const Fit::string &, const Fit::string &, const Fit::string &)>;

    RuleRouter(const Fit::string &ruleID,
        RuleSerializerPtr ruleSerializer,
        const Fit::string &environment,
        RuleExecuteFunctor executor);
    ~RuleRouter() override = default;

    Fit::string Route() override;

    static std::unique_ptr<RuleRouter> Build(
        ContextObj ctx,
        const FitConfigPtr &config,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params,
        const Fit::string &environment);
private:

    const Fit::string ruleID_;
    RuleSerializerPtr ruleSerializer_ {nullptr};
    const Fit::string &environment_;
    RuleExecuteFunctor executor_;
};
class FitableIdRouter : public Router {
public:
    explicit FitableIdRouter(ContextObj ctx);
    ~FitableIdRouter() override = default;
    Fit::string Route() override;

private:
    ContextObj ctx_ {};
};
}

#endif // RULE_ROUTER_H
