/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/23 16:29
 */

#include "router_impl.hpp"
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <genericable/com_huawei_fitLab_matata_ruleEngine_rule_json_execute/1.0.0/cplusplus/execute.hpp>

namespace Fit {
DefaultRouter::DefaultRouter(ContextObj ctx, const FitConfigPtr &config, RoutePtr ruleRouter)
    : ctx_ {ctx}, config_(config), ruleRoute_(std::move(ruleRouter)) {}

Fit::string DefaultRouter::Route()
{
    if (ruleRoute_ != nullptr) {
        return ruleRoute_->Route();
    }
    // 对于默认路由，当没有配置默认路由信息时，随机选一个fitable
    auto res = config_->GetDefault();
    if (res.empty()) {
        res = config_->GetRandomFitable();
        FIT_LOG_DEBUG("Gid:Random fid is :(%s:%s).", config_->GetGenericId().c_str(), res.c_str());
    }

    if (res.empty()) {
        FIT_LOG_ERROR("Default router error!, genericableID:%s.", config_->GetGenericId().c_str());
    }
    return res;
}

std::unique_ptr<DefaultRouter> DefaultRouter::Build(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment)
{
    std::unique_ptr<RuleRouter> ruleRouter {nullptr};
    if (!config->GetRuleId().empty()) {
        ruleRouter = RuleRouter::Build(ctx, config, paramJsonFormatterService, params, environment);
    }
    return make_unique<DefaultRouter>(ctx, config, move(ruleRouter));
}

AliasRouter::AliasRouter(ContextObj ctx, const FitConfigPtr &config)
    : ctx_(ctx), config_(config) {}

Fit::string AliasRouter::Route()
{
    auto res = config_->GetFitableIdByAlias(ContextGetAlias(ctx_));
    if (res.empty()) {
        FIT_LOG_ERROR("Alias router error!, genericableID:%s.", config_->GetGenericId().c_str());
    }
    return res;
}

RuleRouter::RuleRouter(
    const Fit::string &ruleID,
    RuleSerializerPtr ruleSerializer,
    const Fit::string &environment,
    RuleExecuteFunctor executor) : ruleID_(ruleID),
                                   ruleSerializer_(std::move(ruleSerializer)), environment_(environment),
                                   executor_(std::move(executor)) {}

Fit::string RuleRouter::Route()
{
    // 参数
    Fit::string paramJson;
    auto ret = ruleSerializer_->Serialize(paramJson);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Serialize rule error!, ret:%x.", ret);
        return "";
    }

    auto res = executor_(environment_, ruleID_, paramJson);
    if (res.empty()) {
        FIT_LOG_ERROR("Rule route error!");
    }
    return res;
}

std::unique_ptr<RuleRouter> RuleRouter::Build(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment)
{
    if (config->GetRuleId().empty()) {
        FIT_LOG_ERROR("Rule id is empty! genericid:%s.",
            Context::GetGenericableId(ctx).c_str());
        return nullptr;
    }

    auto ruleSerializer = RuleSerializerBuild(ctx, config, paramJsonFormatterService, params, environment);
    if (ruleSerializer == nullptr) {
        return nullptr;
    }

    auto ruleExecutor = [](const Fit::string &environment,
        const Fit::string &ruleID, const Fit::string &param) -> Fit::string {
        fitLab::matata::ruleEngine::rule::json::execute proxy;
        // out param
        Fit::string *result {nullptr};
        // call
        auto ret = proxy(&environment, &ruleID, &param, &result);
        if (ret != FIT_OK || result == nullptr || (*result).empty()) {
            FIT_LOG_ERROR("Execute rule error! ret:%u, environment:%s, ruleID:%s, param:%s.",
                ret, environment.c_str(), ruleID.c_str(), param.c_str());
            return "";
        }

        return *result;
    };

    return make_unique<RuleRouter>(
        config->GetRuleId(), move(ruleSerializer), environment, move(ruleExecutor));
}

FitableIdRouter::FitableIdRouter(ContextObj ctx) : ctx_(ctx) {}

string FitableIdRouter::Route()
{
    return ContextGetFitableId(ctx_);
}
} // LCOV_EXCL_LINE