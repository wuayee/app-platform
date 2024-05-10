/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#include "default_formatter_service.hpp"
#include <fit/external/util/string_utils.hpp>
#include <fit/fit_log.h>
#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include "formatter/json_formatter_entry.hpp"

#ifdef FIT_ENABLE_PROTOBUF
#include "formatter/protobuf_formatter_entry.hpp"
#endif

namespace Fit {
namespace Framework {
namespace Formatter {
DefaultFormatterService::DefaultFormatterService(FormatterRepoPtr repo)
    : repo_(move(repo)) {}

DefaultFormatterService::DefaultFormatterService() = default;

FitCode DefaultFormatterService::SerializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Arguments& args,
    Fit::string& result)
{
    auto formatter = GetFormatter(baseSerialization);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, formats size = %lu, fitableType = %d.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size(), int(baseSerialization.fitableType));
        return FIT_ERR_NOT_FOUND;
    }
    if (formatter->GetArgsInConverter().size() != args.size()) {
        FIT_LOG_ERROR(
            "Not matched formatter, generic id = %s, format size = %lu, args size = %lu, formatter args size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size(),
            args.size(), formatter->GetArgsInConverter().size());
        return FIT_ERR_NOT_MATCH;
    }
    shared_lock<shared_mutex> guard(mt_);
    auto iter = formatterEntry_.find(formatter->GetFormat());
    if (iter == formatterEntry_.end()) {
        FIT_LOG_ERROR("The format type is not supported. (genericableId=%s, type=%d).",
            baseSerialization.genericId.c_str(), formatter->GetFormat());
        return FIT_ERR_NOT_SUPPORT;
    }
    return iter->second->SerializeRequest(ctx, formatter->GetArgsInConverter(), baseSerialization, args, result);
}

FitCode DefaultFormatterService::DeserializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Fit::string& buffer, Arguments& args)
{
    auto formatter = GetFormatter(baseSerialization);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, format size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size());
        return FIT_ERR_NOT_FOUND;
    }
    if (formatter->GetArgsInConverter().empty()) {
        return FIT_OK;
    }

    shared_lock<shared_mutex> guard(mt_);
    auto iter = formatterEntry_.find(formatter->GetFormat());
    if (iter == formatterEntry_.end()) {
        FIT_LOG_ERROR("The format type is not supported. (genericableId=%s, type=%d).",
            baseSerialization.genericId.c_str(), formatter->GetFormat());
        return FIT_ERR_NOT_SUPPORT;
    }
    return iter->second->DeserializeRequest(ctx, formatter->GetArgsInConverter(), baseSerialization, buffer, args);
}

Fit::string DefaultFormatterService::SerializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Response& response)
{
    Fit::string data;
    if (response.code == FIT_OK) {
        auto ret = SerializeArgOut(ctx, baseSerialization, response.args, data);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("SerializeArgOut failed: err = %X", ret);
            return "";
        }
    }

    return data;
}

Response DefaultFormatterService::DeserializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Fit::string& buffer)
{
    Response result {};
    result.code = DeserializeArgOut(ctx, baseSerialization, buffer, result.args);
    if (result.code != FIT_OK) {
        result.msg = "Deserialize fail";
        return result;
    }

    return result;
}

FitCode DefaultFormatterService::SerializeArgOut(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Arguments& args, Fit::string& result)
{
    auto formatter = GetFormatter(baseSerialization);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, format size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size());
        return FIT_ERR_NOT_FOUND;
    }

    if (args.empty()) {
        return FIT_OK;
    }

    if (formatter->GetArgsOutConverter().size() != args.size()) {
        FIT_LOG_ERROR(
            "Not matched formatter, generic id = %s, format size = %lu, args size = %lu, formatter args size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size(),
            args.size(), formatter->GetArgsOutConverter().size());
        return FIT_ERR_NOT_MATCH;
    }

    Fit::string argBuffer;
    auto ret = formatter->GetArgsOutConverter()[0].Serialize(ctx, args[0], result);
    if (ret != FIT_OK && ret != FIT_NULL_PARAM) {
        FIT_LOG_ERROR("Error serialize, generic id = %s, format size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size());
        return ret;
    }

    return FIT_OK;
}

FitCode DefaultFormatterService::DeserializeArgOut(ContextObj ctx, const BaseSerialization& baseSerialization,
    const Fit::string& buffer, Arguments& args)
{
    auto formatter = GetFormatter(baseSerialization);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, format size = %lu.", baseSerialization.genericId.c_str(),
            baseSerialization.formats.size());
        return FIT_ERR_NOT_FOUND;
    }

    if (formatter->GetArgsOutConverter().empty() || !formatter->GetArgsOutConverter().begin()->Deserialize) {
        return FIT_OK;
    }

    if (formatter->GetArgsOutConverter().size() != 1) {
        FIT_LOG_ERROR(
            "Not matched formatter, generic id = %s, format size = %lu, formatter args size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size(),
            formatter->GetArgsOutConverter().size());
        return FIT_ERR_NOT_MATCH;
    }

    Fit::any arg;
    auto ret = formatter->GetArgsOutConverter()[0].Deserialize(ctx, buffer, arg);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Error deserialize, generic id = %s, format size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size());
        return ret;
    }
    args.push_back(arg);

    return FIT_OK;
}

FormatterMetaPtr DefaultFormatterService::GetFormatter(const BaseSerialization& baseSerialization)
{
    if (!repo_) {
        FIT_LOG_ERROR("Null repo");
        return nullptr;
    }

    return repo_->Get(baseSerialization);
}

Fit::vector<int32_t> DefaultFormatterService::GetFormats(const Fit::string &genericId)
{
    return repo_->GetFormats(genericId);
}

Arguments DefaultFormatterService::CreateArgOut(ContextObj ctx, const BaseSerialization& baseSerialization)
{
    auto formatter = GetFormatter(baseSerialization);
    if (!formatter) {
        FIT_LOG_ERROR("Not found, generic id = %s, format size = %lu.",
            baseSerialization.genericId.c_str(), baseSerialization.formats.size());
        return {};
    }

    return formatter->CreateArgOut(ctx);
}
void DefaultFormatterService::ClearAllFormats()
{
    {
        unique_lock<shared_mutex> guard(mt_);
        formatterEntry_.clear();
    }
    if (!repo_) {
        FIT_LOG_ERROR("Null repo");
        return;
    }

    return repo_->Clear();
}

void DefaultFormatterService::AddFormatterEntry(shared_ptr<FormatterEntry> val)
{
    unique_lock<shared_mutex> guard(mt_);
    formatterEntry_[val->GetFormateType()] = move(val);
}

void DefaultFormatterService::RemoveFormatterEntry(const shared_ptr<FormatterEntry>& val)
{
    unique_lock<shared_mutex> guard(mt_);
    formatterEntry_.erase(val->GetFormateType());
}

bool DefaultFormatterService::Start()
{
    repo_ = FormatterRepoPtr(GetRuntime().GetElementIs<FormatterRepo>(), [](FormatterRepo*) {});
    if (!repo_) {
        FIT_LOG_ERROR("Formatter repo is null.");
        return false;
    }
    AddFormatterEntry(make_shared<JsonFormatterEntry>());
#ifdef FIT_ENABLE_PROTOBUF
    AddFormatterEntry(make_shared<ProtobufFormatterEntry>());
#endif

    FIT_LOG_INFO("Genericable formatter service is started.");
    return true;
}

bool DefaultFormatterService::Stop()
{
    ClearAllFormats();
    FIT_LOG_INFO("Genericable formatter service is stopped.");
    return true;
}
}
}
} // LCOV_EXCL_LINE