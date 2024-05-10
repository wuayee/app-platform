/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/24 14:29
 */

#include "default_param_json_formatter_service.hpp"

#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <fit/fit_log.h>
#include <fit/internal/runtime/runtime.hpp>

namespace Fit {
namespace Framework {
namespace ParamJsonFormatter {
DefaultParamJsonFormatterService::DefaultParamJsonFormatterService() = default;

DefaultParamJsonFormatterService::DefaultParamJsonFormatterService(Formatter::FormatterRepoPtr repo)
    : repo_(move(repo)) {}

namespace {
bool Append(rapidjson::Writer<rapidjson::StringBuffer> &writer, const Fit::string &jsonString)
{
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    if (doc.HasParseError()) {
        FIT_LOG_ERROR("Failed to parse json. [json=%s]", jsonString.c_str());
        return false;
    }
    doc.Accept(writer);
    return true;
}

FitCode SerializeParamToJsonInner(ContextObj ctx,
    const Fit::string& genericID,
    const Arguments& args,
    const Formatter::ArgConverterList &converterList,
    Fit::string& result)
{
    const Fit::string argPrefix {"arg"};
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    writer.StartObject();
    for (uint32_t i = 0; i < args.size(); ++i) {
        Fit::string argBuffer;
        auto ret = converterList[i].Serialize(ctx, args[i], argBuffer);
        if (ret != FIT_OK && ret != FIT_NULL_PARAM) {
            FIT_LOG_ERROR("Error serialize, generic id = %s, arg index = %d.",
                genericID.c_str(), i);
            return ret;
        }
        writer.Key((argPrefix + Fit::to_string(i)).c_str());
        if (ret == FIT_NULL_PARAM) {
            writer.Null();
        } else if (!Append(writer, argBuffer)) {
            return FIT_ERR_SERIALIZE_JSON;
        }
    }
    writer.EndObject();
    result = sb.GetString();
    return FIT_OK;
}

struct SerializeArgInfo {
    int32_t idx;
    const Argument& arg;
};

FitCode SerializeIndexParamToJsonInner(ContextObj ctx,
    const Fit::string& genericID,
    const SerializeArgInfo &argInfo,
    const Formatter::ArgConverterList &converterList,
    Fit::string& result)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    Fit::string argBuffer;
    auto ret = converterList[argInfo.idx].Serialize(ctx, argInfo.arg, argBuffer);
    if (ret != FIT_OK && ret != FIT_NULL_PARAM) {
        FIT_LOG_ERROR("Error serialize, generic id = %s, arg index = %d.",
            genericID.c_str(), argInfo.idx);
        return ret;
    }
    if (ret == FIT_NULL_PARAM) {
        writer.Null();
    } else if (!Append(writer, argBuffer)) {
        return FIT_ERR_SERIALIZE_JSON;
    }
    result = sb.GetString();
    return FIT_OK;
}
}

FitCode DefaultParamJsonFormatterService::GetConvertList(
    const Fit::string& genericID,
    Formatter::ArgConverterList &converterList)
{
    Formatter::BaseSerialization base {
        genericID,
        {1},
        Annotation::FitableType::MAIN
    };

    auto formatter = repo_->Get(base);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, formats size = %lu, fitableType = %d.",
            base.genericId.c_str(), base.formats.size(), int(base.fitableType));
        return FIT_ERR_NOT_FOUND;
    }

    converterList = formatter->GetArgsInConverter();
    return FIT_OK;
}

FitCode DefaultParamJsonFormatterService::SerializeParamToJson(ContextObj ctx,
    const Fit::string& genericID,
    const Arguments& args,
    Fit::string& result)
{
    Formatter::ArgConverterList converterList;
    auto ret = GetConvertList(genericID, converterList);
    if (ret != FIT_OK) {
        return ret;
    }
    if (converterList.size() != args.size()) {
        FIT_LOG_ERROR("Not matched formatter, generic id = %s, args size = %lu, formatter args size = %lu.",
            genericID.c_str(), args.size(), converterList.size());
        return FIT_ERR_NOT_MATCH;
    }

    return SerializeParamToJsonInner(ctx, genericID, args, converterList, result);
}

FitCode DefaultParamJsonFormatterService::SerializeIndexParamToJson(ContextObj ctx,
    const Fit::string& genericID,
    int32_t idx,
    const Argument& arg,
    Fit::string& result)
{
    Formatter::ArgConverterList converterList;
    auto ret = GetConvertList(genericID, converterList);
    if (ret != FIT_OK) {
        return ret;
    }
    if (converterList.size() <= static_cast<size_t>(idx)) {
        FIT_LOG_ERROR(
            "Not matched formatter, generic id = %s, args idx = %d, formatter args size = %lu.",
            genericID.c_str(), idx, converterList.size());
        return FIT_ERR_NOT_MATCH;
    }

    SerializeArgInfo oneArg {
        idx,
        arg
    };
    return SerializeIndexParamToJsonInner(ctx, genericID, oneArg, converterList, result);
}

bool DefaultParamJsonFormatterService::Start()
{
    auto repo = Formatter::FormatterRepoPtr(
        GetRuntime().GetElementIs<Formatter::FormatterRepo>(),
        [](Formatter::FormatterRepo *) {});
    if (!repo) {
        FIT_LOG_ERROR("Formatter repo is null.");
        return false;
    }
    repo_ = move(repo);
    FIT_LOG_INFO("Route with param service is started.");
    return true;
}

bool DefaultParamJsonFormatterService::Stop()
{
    FIT_LOG_INFO("Route with param service is stopped.");
    return true;
}
}
}
} // LCOV_EXCL_LINE