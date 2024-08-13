/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#include <fit/internal/runtime/config/system_config_default.hpp>
#include <fstream>
#include <rapidjson/istreamwrapper.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <fit/external/util/string_utils.hpp>
#include <fit/external/util/number_utils.hpp>

using rapidjson::Document;
using rapidjson::IStreamWrapper;

namespace Fit {
namespace Config {
namespace {
void BuildConfig(WritableValue& config, rapidjson::Value& jsonValue)
{
    switch (jsonValue.GetType()) {
        case rapidjson::kNullType:
            config.SetNull();
            break;
        case rapidjson::kFalseType:
            config.SetValue(false);
            break;
        case rapidjson::kTrueType:
            config.SetValue(true);
            break;
        case rapidjson::kObjectType:
            config.SetObject();
            for (auto& node : jsonValue.GetObject()) {
                BuildConfig(config.AddMember(node.name.GetString()), node.value);
            }
            break;
        case rapidjson::kArrayType:
            config.SetArray();
            for (auto& node : jsonValue.GetArray()) {
                BuildConfig(config.PushBack(), node);
            }
            break;
        case rapidjson::kStringType:
            config.SetValue(jsonValue.GetString());
            break;
        case rapidjson::kNumberType:
            if (jsonValue.IsInt()) {
                config.SetValue(jsonValue.GetInt());
            } else {
                config.SetValue(jsonValue.GetDouble());
            }
            break;
        default:
            FIT_LOG_WARN("Unknown type, type=%u.", jsonValue.GetType());
            break;
    }
}
}
SystemConfigDefault::SystemConfigDefault() : RuntimeElementBase("systemConfig")
{
}

SystemConfigDefault::SystemConfigDefault(const string& configFile, const map<string, string>& options)
    : SystemConfigDefault()
{
    LoadFromFile(configFile.c_str());
    PutItems(options);
}

bool SystemConfigDefault::Start()
{
    return config_ != nullptr;
}

FitCode SystemConfigDefault::LoadFromFile(const char *configFile)
{
    std::ifstream ifs(configFile);
    if (!ifs.is_open()) {
        FIT_LOG_ERROR("Failed to open config file(%s)", configFile);
        return FIT_ERR_NOT_FOUND;
    }

    rapidjson::Document document;
    IStreamWrapper wrapper(ifs);
    document.ParseStream(wrapper);
    if (document.HasParseError()) {
        FIT_LOG_ERROR("Failed to parse config file(%s).", configFile);
        return FIT_ERR_PARSE_JSON_FAIL;
    }
    config_ = WritableValue::New();
    BuildConfig(*config_, document);

    return FIT_OK;
}

FitCode SystemConfigDefault::LoadFromString(const char *jsonString)
{
    rapidjson::Document document;
    document.Parse(jsonString);
    if (document.HasParseError()) {
        FIT_LOG_ERROR("Failed to parse json string(%s).", jsonString);
        return FIT_ERR_PARSE_JSON_FAIL;
    }
    config_ = WritableValue::New();
    BuildConfig(*config_, document);

    return FIT_OK;
}

FitCode SystemConfigDefault::SetValue(const char *key, const char *value)
{
    if (!config_) {
        return FIT_ERR_FAIL;
    }
    auto keyPaths = StringUtils::Split(key, '.');
    const auto &beginPath = keyPaths.front();
    auto *configNode = config_.get();
    for (auto &item : keyPaths) {
        if (!configNode->IsObject()) {
            configNode->SetObject();
        }
        auto nextNode = &configNode->FindMember(item.c_str());
        if (nextNode->IsNull()) {
            nextNode = &configNode->AddMember(item.c_str());
        }
        configNode = nextNode;
    }
    if (!TrySetValue(configNode, value)) {
        FIT_LOG_WARN("Unsupported key (%s) value (%s), will be ignored", key, value);
        return FIT_ERR_PARAM;
    }

    return FIT_OK;
}

Value &SystemConfigDefault::GetValue(const char *key) const
{
    if (key == nullptr || !config_) {
        return WritableValue::Null;
    }

    auto keyPaths = StringUtils::Split(key, '.');
    auto *configNode = config_.get();
    for (auto &item : keyPaths) {
        if (!configNode->IsObject()) {
            return WritableValue::Null;
        }
        auto nextNode = &configNode->FindMember(item.c_str());
        if (nextNode->IsNull()) {
            return WritableValue::Null;
        }
        configNode = nextNode;
    }

    return *configNode;
}

FitCode SystemConfigDefault::PutItems(const Fit::map<Fit::string, Fit::string> &items)
{
    for (auto &item : items) {
        SetValue(item.first.c_str(), item.second.c_str());
    }

    return FIT_OK;
}

bool SystemConfigDefault::TrySetValue(WritableValue *container, const Fit::string &value)
{
    static const Fit::vector<std::function<bool(WritableValue *container, const Fit::string &value)>> setValueFuncs {
        &SystemConfigDefault::TrySetNullValue,
        &SystemConfigDefault::TrySetBoolValue,
        &SystemConfigDefault::TrySetIntValue,
        &SystemConfigDefault::TrySetDoubleValue,
        &SystemConfigDefault::TrySetStringValue,
        &SystemConfigDefault::TrySetDefaultStringValue
    };

    for (const auto &setValue : setValueFuncs) {
        if (setValue(container, value)) {
            return true;
        }
    }

    return false;
}

bool SystemConfigDefault::TrySetBoolValue(WritableValue *container, const Fit::string &value)
{
    if (value == "true") {
        container->SetValue(true);
        return true;
    }

    if (value == "false") {
        container->SetValue(false);
        return true;
    }

    return false;
}

bool SystemConfigDefault::TrySetIntValue(WritableValue *container, const Fit::string &value)
{
    int32_t integerNumber {};
    if (Fit::NumberUtils::TryParse(value, integerNumber)) {
        container->SetValue(integerNumber);
        return true;
    }

    return false;
}

bool SystemConfigDefault::TrySetDoubleValue(WritableValue *container, const Fit::string &value)
{
    double doubleNumber {};
    if (Fit::NumberUtils::TryParse(value, doubleNumber)) {
        container->SetValue(doubleNumber);
        return true;
    }

    return false;
}

bool SystemConfigDefault::TrySetStringValue(WritableValue *container, const Fit::string &value)
{
    if (IsStringValue(value)) {
        constexpr uint32_t stringWrapperSize = 2;
        container->SetValue(value.substr(1, value.size() - stringWrapperSize).c_str());
        return true;
    }

    return false;
}

bool SystemConfigDefault::TrySetNullValue(WritableValue *container, const string &value)
{
    if (value == "null") {
        container->SetNull();
        return true;
    }
    return false;
}

bool SystemConfigDefault::TrySetDefaultStringValue(WritableValue *container, const string &value)
{
    container->SetValue(value.c_str());
    return true;
}

bool SystemConfigDefault::IsStringValue(const Fit::string &value) noexcept
{
    return value.size() >= 2 && value.front() == '\"' && value.back() == '\"';
}

const string& SystemConfigDefault::GetWorkerId() const
{
    if (workerId_.empty()) {
        workerId_ = GetValue("worker_id").AsString();
    }
    return workerId_;
}

const string& SystemConfigDefault::GetEnvName() const
{
    if (envName_.empty()) {
        envName_ = GetValue("environment").AsString("debug");
    }
    return envName_;
}

const string& SystemConfigDefault::GetAppName() const
{
    if (appName_.empty()) {
        appName_ = GetValue("app.name").AsString(GetWorkerId().c_str());
    }
    return appName_;
}
string SystemConfigDefault::GetAppVersion() const
{
    return GetValue("app.version").AsString("");
}
}
} // LCOV_EXCL_LINE