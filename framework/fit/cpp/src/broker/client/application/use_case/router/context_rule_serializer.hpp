/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:14
 */
#ifndef CONTEXT_RULE_SERIALIZER_HPP
#define CONTEXT_RULE_SERIALIZER_HPP

#include "rule_serializer.hpp"

namespace Fit {
class ContextRuleSerializer : public RuleSerializer {
public:
    explicit ContextRuleSerializer(ContextObj ctx);
    ~ContextRuleSerializer() override = default;
    FitCode Serialize(Fit::string &serializeResult) override;
private:
    static FitCode SerializeInner(const Fit::map<Fit::string, Fit::string> &routeContext,
        Fit::string &serializeResult);
    ContextObj ctx_ {nullptr};
};
}

#endif // CONTEXT_RULE_SERIALIZER_HPP
