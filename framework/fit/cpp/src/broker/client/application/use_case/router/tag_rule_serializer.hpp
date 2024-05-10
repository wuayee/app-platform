/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:11
 */
#ifndef TAG_RULE_SERIALIZER_HPP
#define TAG_RULE_SERIALIZER_HPP

#include <functional>
#include "rule_serializer.hpp"

namespace Fit {
class TagRuleSerializer : public RuleSerializer {
public:
    using GetTagsFunctor = std::function<FitCode(const Fit::string &,
        const Fit::vector<Fit::string> &,
        const Fit::string &,
        Fit::vector<Fit::string> &)>;

    TagRuleSerializer(
        ContextObj ctx,
        const FitConfigPtr &config,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params,
        const Fit::string &environment,
        GetTagsFunctor functor);

    ~TagRuleSerializer() override = default;

    FitCode Serialize(Fit::string &serializeResult) override;
    static std::unique_ptr<TagRuleSerializer> Build(
        ContextObj ctx,
        const FitConfigPtr &config,
        const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
        const Fit::vector<Fit::any> &params,
        const Fit::string &environment);
private:
    FitCode PrepareTags();
    FitCode SerializeInner(Fit::string &serializeResult);
    Fit::string MakeArgParam(int32_t idx);
    FitCode GetArgTags(int32_t idx, Fit::vector<Fit::string> &tags);

    ContextObj ctx_ {};
    const FitConfigPtr config_ {};
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService_ {};
    const Fit::vector<Fit::any> &params_;
    const Fit::string &environment_;
    GetTagsFunctor functor_;
    Fit::map<int32_t, Fit::vector<Fit::string>> argTags_;
};
}

#endif // TAG_RULE_SERIALIZER_HPP
