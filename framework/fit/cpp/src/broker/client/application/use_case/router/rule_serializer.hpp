/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 9:34
 */
#ifndef FIT_RULE_PARAM_SERIALIZER_HPP
#define FIT_RULE_PARAM_SERIALIZER_HPP

#include "../gateway/fit_config.h"

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/fit_code.h>
#include <memory>
#include <sstream>
#include <fit/internal/framework/param_json_formatter_service.hpp>
#include <fit/external/util/context/context_api.hpp>

namespace Fit {
class RuleSerializer {
public:
    virtual ~RuleSerializer() = default;
    virtual FitCode Serialize(Fit::string &serializeResult) = 0;
};

using RuleSerializerPtr = std::shared_ptr<RuleSerializer>;

class BaseRuleSerializer : public RuleSerializer {
public:
    FitCode Serialize(Fit::string &serializeResult) override;
    void AddSerializer(RuleSerializerPtr serializer);
private:
    Fit::vector<RuleSerializerPtr> serializers_;
};

RuleSerializerPtr RuleSerializerBuild(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment);
}

#endif // FIT_RULE_PARAM_SERIALIZER_HPP
