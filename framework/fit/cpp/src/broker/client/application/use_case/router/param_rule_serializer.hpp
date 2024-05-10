/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:10
 */
#ifndef PARAM_RULE_SERIALIZER_HPP
#define PARAM_RULE_SERIALIZER_HPP

#include "rule_serializer.hpp"

namespace Fit {
class ParamRuleSerializer : public RuleSerializer {
public:
    ParamRuleSerializer(
        ContextObj ctx,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params);
    ~ParamRuleSerializer() override = default;

    FitCode Serialize(Fit::string &serializeResult) override;

private:
    ContextObj ctx_;
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService_;
    const Fit::vector<Fit::any> &params_;
};
}

#endif // PARAM_RULE_SERIALIZER_HPP
