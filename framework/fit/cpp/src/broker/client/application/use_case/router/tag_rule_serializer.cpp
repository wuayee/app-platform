/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:25
 */

#include "tag_rule_serializer.hpp"
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>

#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <fit/stl/string.hpp>
#include <genericable/com_huawei_fit_matata_tagCenter_tagger_tag/1.0.0/cplusplus/tag.hpp>

namespace Fit {
namespace {
Fit::string MakeArgJsonKey(int32_t idx)
{
    std::ostringstream ss;
    ss << "arg" << idx;
    return Fit::to_fit_string(ss.str());
}
}

TagRuleSerializer::TagRuleSerializer(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment,
    GetTagsFunctor functor)
    : ctx_(ctx),
      config_(config),
      paramJsonFormatterService_(paramJsonFormatterService),
      params_(params),
      environment_(environment),
      functor_(std::move(functor)) {}

FitCode TagRuleSerializer::Serialize(Fit::string &serializeResult)
{
    auto ret = PrepareTags();
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Prepare tags error!");
        return ret;
    }

    return SerializeInner(serializeResult);
}

FitCode TagRuleSerializer::SerializeInner(Fit::string &serializeResult)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);

    writer.StartObject();
    for (const auto &item : argTags_) {
        writer.Key(MakeArgJsonKey(item.first).c_str());
        writer.StartArray();
        for (const auto &id : item.second) {
            writer.String(id.c_str());
        }
        writer.EndArray();
    }
    writer.EndObject();

    std::ostringstream ss;
    ss << "\"T\" : " << sb.GetString();
    serializeResult = Fit::to_fit_string(ss.str());
    return FIT_OK;
}

FitCode TagRuleSerializer::PrepareTags()
{
    for (int32_t idx = 0; idx < static_cast<int32_t>(params_.size()); ++idx) {
        Fit::vector<Fit::string> tags;
        auto ret = GetArgTags(idx, tags);
        if (ret != FIT_OK) {
            return ret;
        }
        argTags_[idx] = std::move(tags);
    }
    return FIT_OK;
}

FitCode TagRuleSerializer::GetArgTags(int32_t idx, Fit::vector<Fit::string> &tags)
{
    auto argTagIds = config_->GetParamTagByIdx(idx);
    if (argTagIds.empty()) {
        return FIT_OK;
    }
    auto argJsonParam = MakeArgParam(idx);
    if (argJsonParam.empty()) {
        return FIT_ERR_SERIALIZE;
    }
    return functor_(environment_, argTagIds, argJsonParam, tags);
}

Fit::string TagRuleSerializer::MakeArgParam(int32_t idx)
{
    Fit::string inputJson;
    auto ret = paramJsonFormatterService_->SerializeIndexParamToJson(
        ctx_, Context::GetGenericableId(ctx_), idx, params_[idx], inputJson);
    if (ret != FIT_OK) {
        return "";
    }
    std::ostringstream ss;
    ss << "{ \"P\" : {\"arg" << idx << "\" : " << inputJson << "}}";
    return Fit::to_fit_string(ss.str());
}

std::unique_ptr<TagRuleSerializer> TagRuleSerializer::Build(
    ContextObj ctx,
    const FitConfigPtr &config,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params,
    const Fit::string &environment)
{
    auto functor = [](const Fit::string &environment,
        const Fit::vector<Fit::string> &tagIds,
        const Fit::string &argJson,
        Fit::vector<Fit::string> &tags) -> FitCode {
        fit::matata::tagCenter::tagger::tag proxy;
        // out param
        Fit::vector<Fit::string> *result_ {nullptr};
        // call
        auto ret = proxy(&environment, &tagIds, &argJson, &result_);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Get tags from tag center error!");
            return ret;
        }
        tags = *result_;
        return FIT_OK;
    };

    return make_unique<TagRuleSerializer>(ctx,
            config,
            paramJsonFormatterService,
            params,
            environment,
            move(functor));
}
} // LCOV_EXCL_LINE