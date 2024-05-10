/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement
 * Author       : songyongtan
 * Date         : 2022/5/16
 * Notes:       :
 */

#include "tracer_config_element.hpp"

#include <fit/fit_log.h>
#include <fit/internal/runtime/config/system_config.hpp>
#include <key_value_config_service_impl.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include "broker/client/domain/trace/tracer.hpp"

using namespace Fit;
using Config::SystemConfig;

TracerConfigElement::TracerConfigElement() : RuntimeElementBase("tracerConfig") {}

TracerConfigElement::~TracerConfigElement() = default;

string TracerConfigElement::GetAndSubscribeConfig(const string& key,
    const string& defaultValue,
    Configuration::KeyValueConfigService::SubscribeCallBack callback)
{
    if (key.empty()) {
        return defaultValue;
    }
    Configuration::KeyValueConfigService& service = *kvConfigService_;
    string value;
    auto res = service.Get(key, value);
    service.Subscribe(key, std::move(callback));
    if (res != FIT_OK) {
        FIT_LOG_WARN("Trace key %s not in config center, using default: %s.", key.c_str(), defaultValue.c_str());
        service.Set(key, defaultValue);
        value = defaultValue;
    } else {
        FIT_LOG_INFO("Trace key %s in config center, value: %s.", key.c_str(), value.c_str());
    }
    return value;
}

void TracerConfigElement::LoadConfigItem(const string& configKey, const string& subscribedKey,
    bool defaultValue, const std::function<void(bool)>& changedCallback)
{
    SystemConfig& systemConfig = *GetRuntime().GetElementIs<SystemConfig>();
    auto& keyItem = systemConfig.GetValue(configKey.c_str());
    if (keyItem.IsBool()) {
        FIT_LOG_DEBUG("There has a default tracer config. [key=%s, value=%d]", configKey.c_str(), keyItem.AsBool());
        return;
    }

    if (subscribedKey.empty()) {
        FIT_LOG_DEBUG("No subscribed key is setted.");
        return;
    }

    auto tracerEnabled = GetAndSubscribeConfig(subscribedKey, defaultValue ? "true" : "false",
        [changedCallback](const string& key, const string& value) {
            FIT_LOG_INFO("Tracer enabled changed. key:%s, value:%s.", key.c_str(), value.c_str());
            changedCallback(value == "true");
        });
    changedCallback(tracerEnabled == "true");
}

void TracerConfigElement::LoadConf()
{
    auto systemConfig = GetRuntime().GetElementIs<SystemConfig>();
    const char* TRACER_ENABLED_VALUE_KEY = "tracer.enabled.value";
    const char* TRACER_ENABLED_KEY = "tracer.enabled.key";
    const char* TRACER_ENABLED_DEFAULT_KEY = "tracer.enabled.default";
    LoadConfigItem(TRACER_ENABLED_VALUE_KEY,
        systemConfig->GetValue(TRACER_ENABLED_KEY).AsString(""),
        systemConfig->GetValue(TRACER_ENABLED_DEFAULT_KEY).AsBool(false),
        [](bool value) { Tracer::GetInstance()->SetEnabled(value); });

    const char* TRACER_GLOBAL_ENABLED_VALUE_KEY = "tracer.process.enabled.value";
    const char* TRACER_PROCESS_ENABLED_KEY = "tracer.process.enabled.key";
    const char* TRACER_PROCESS_ENABLED_DEFAULT_KEY = "tracer.process.enabled.default";
    LoadConfigItem(TRACER_GLOBAL_ENABLED_VALUE_KEY,
        systemConfig->GetValue(TRACER_PROCESS_ENABLED_KEY).AsString(""),
        systemConfig->GetValue(TRACER_PROCESS_ENABLED_DEFAULT_KEY).AsBool(false),
        [](bool value) { Tracer::GetInstance()->SetGlobalTraceEnabled(value); });

    const char* TRACER_LOCAL_ENABLED_VALUE_KEY = "tracer.process.local.enabled.value";
    const char* TRACER_LOCAL_ENABLED_KEY = "tracer.process.local.enabled.key";
    const char* TRACER_LOCAL_ENABLED_DEFAULT_KEY = "tracer.process.local.enabled.default";
    LoadConfigItem(TRACER_LOCAL_ENABLED_VALUE_KEY,
        systemConfig->GetValue(TRACER_LOCAL_ENABLED_KEY).AsString(""),
        systemConfig->GetValue(TRACER_LOCAL_ENABLED_DEFAULT_KEY).AsBool(false),
        [](bool value) { Tracer::GetInstance()->SetLocalTraceEnabled(value); });

    FIT_LOG_INFO("Tracer config: process:%d, process.local:%d.",
        Tracer::GetInstance()->IsGlobalTraceEnabled(),
        Tracer::GetInstance()->IsLocalTraceEnabled());
}

bool TracerConfigElement::Start()
{
    LoadConf();
    auto confClient = GetRuntime().GetElementIs<Configuration::ConfigurationClient>();
    if (confClient == nullptr) {
        FIT_LOG_ERROR("Need config client.");
        return false;
    }
    auto sharedClient = Configuration::ConfigurationClientPtr(confClient, [](Configuration::ConfigurationClient*) {});
    kvConfigService_ = make_unique<Fit::Configuration::KeyValueConfigServiceImpl>(sharedClient);
    return true;
}

bool TracerConfigElement::Stop()
{
    kvConfigService_.reset();
    return true;
}
