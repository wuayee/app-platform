/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:14
 */

#include "rule_serializer.hpp"
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include "param_rule_serializer.hpp"
#include "tag_rule_serializer.hpp"
#include "context_rule_serializer.hpp"

namespace Fit {
FitCode BaseRuleSerializer::Serialize(Fit::string &serializeResult)
{
    std::ostringstream oss;
    bool flag {false};

    oss << "{";
    for (const auto &serializer : serializers_) {
        Fit::string tmpResult;
        auto ret = serializer->Serialize(tmpResult);
        if (ret != FIT_OK) {
            return ret;
        }
        if (tmpResult.empty()) {
            continue;
        }
        if (flag) {
            oss << ",";
        } else {
            flag = true;
        }
        oss << tmpResult;
    }
    oss << "}";
    serializeResult = Fit::to_fit_string(oss.str());
    return FIT_OK;
}

void BaseRuleSerializer::AddSerializer(RuleSerializerPtr serializer)
{
    if (serializer != nullptr) {
        serializers_.push_back(std::move(serializer));
    }
}

RuleSerializerPtr RuleSerializerBuild(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment)
{
    auto baseSerializer = make_unique<BaseRuleSerializer>();
    if (!params.empty()) {
        auto paramSerializer = make_unique<ParamRuleSerializer>(ctx, paramJsonFormatterService, params);
        baseSerializer->AddSerializer(std::move(paramSerializer));

        auto tagSerializer = TagRuleSerializer::Build(ctx, config,
            paramJsonFormatterService, params, environment);
        baseSerializer->AddSerializer(std::move(tagSerializer));
    }

    auto contextSerializer = make_unique<ContextRuleSerializer>(ctx);
    baseSerializer->AddSerializer(std::move(contextSerializer));

    return std::move(baseSerializer);
}
} // LCOV_EXCL_LINE