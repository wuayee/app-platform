/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/25
 * Notes:       :
 */

#include "configuration_client_v2.hpp"
#include <fit/fit_log.h>
#include <fit/external/util/string_utils.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>

#include <genericable/com_huawei_matata_conf_client_get/1.0.0/cplusplus/get.hpp>
#include <genericable/com_huawei_matata_conf_client_download/1.0.0/cplusplus/download.hpp>
#include <genericable/com_huawei_matata_notification_client_subscribe/1.0.0/cplusplus/subscribe.hpp>
#include <genericable/com_huawei_matata_conf_subscription_client_append/1.0.0/cplusplus/append.hpp>
#include <genericable/com_huawei_matata_notification_client_consume/1.0.0/cplusplus/consume.hpp>

using Fit::Config::SystemConfig;
namespace {
// 目前一个进程的订阅回调只有这里需要处理，目前只能创建一个ConfigurationClientV2对象关联回调
std::function<void(const Fit::bytes &)> g_mqGroupCallback;
}

namespace Fit {
namespace Configuration {
ConfigurationClientV2::ConfigurationClientV2() = default;
ConfigurationClientV2::ConfigurationClientV2(Fit::string environment, Fit::string groupName, Fit::string consumerName)
    : environment_(std::move(environment)),
      groupName_(std::move(groupName)),
      consumerName_(std::move(consumerName)) {}

ConfigurationClientV2::~ConfigurationClientV2()
{
    g_mqGroupCallback = nullptr;
}

int32_t ConfigurationClientV2::Download(const string &key, ItemValueSet &out)
{
    return DownloadWithKey(key, out);
}

bool ConfigurationClientV2::IsSubscribed(const string &key) const
{
    std::lock_guard<std::recursive_mutex> guard(mt_);
    return genericablesNotifyGroup_.find(key) != genericablesNotifyGroup_.end()
        || valueNotifyGroup_.find(key) != valueNotifyGroup_.end();
}

FitCode ConfigurationClientV2::InvokeSubscribe(const string &key)
{
    auto ret = SubscribeGroup();
    if (ret != FIT_OK) {
        return ret;
    }

    FIT_LOG_INFO("Start append generic(%s) to (%s).", key.c_str(), groupName_.c_str());
    matata::conf::subscription::client::append proxy;
    Fit::vector<Fit::string> paths {key};
    ret = proxy(&groupName_, &paths);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Append generic %s to %s failed", key.c_str(), groupName_.c_str());
    }
    return ret;
}

int32_t ConfigurationClientV2::Subscribe(const string &key,
    ConfigurationClient::ConfigSubscribePathCallback callback)
{
    auto ret = InvokeSubscribe(key);
    if (ret != FIT_OK) {
        return ret;
    }

    std::lock_guard<std::recursive_mutex> guard(mt_);
    genericablesNotifyGroup_[key] = callback;

    return FIT_OK;
}

int32_t ConfigurationClientV2::Subscribe(const string &key,
    ConfigurationClient::ConfigSubscribeNodeCallback callback)
{
    auto ret = InvokeSubscribe(key);
    if (ret != FIT_OK) {
        return ret;
    }

    std::lock_guard<std::recursive_mutex> guard(mt_);
    valueNotifyGroup_[key] = callback;

    return FIT_OK;
}

FitCode ConfigurationClientV2::SubscribeGroup()
{
    /**
     * 消息格式，以“\n”分隔的多个配置的路径
     * 例如：
     * fit.public.genericables.g1
     * fit.public.genericables.g2
     * 1. 获取通知后需要主动解析后再拉取genericable配置
     */
    if (isSubscribeSuccess_) {
        return FIT_OK;
    }
    std::lock_guard<std::recursive_mutex> guard(mt_);
    if (isSubscribeSuccess_) {
        return FIT_OK;
    }

    g_mqGroupCallback = BuildChangeCallback();

    FIT_LOG_INFO("Start subscribe group(%s), consumer(%s).",
        groupName_.c_str(), consumerName_.c_str());

    matata::notification::client::subscribe proxy;
    Fit::string fitableId {"consume_cpp"};
    // call
    auto ret = proxy(&consumerName_, &groupName_, &fitableId);
    isSubscribeSuccess_ = (ret == FIT_OK);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to subscribe, group name = %s, consumer = %s",
            groupName_.c_str(), consumerName_.c_str());
    }

    return ret;
}

int32_t ConfigurationClientV2::Get(const Fit::string &key, Fit::string &value)
{
    matata::conf::client::get getProxy;
    // out param
    Fit::string *getResult {nullptr};
    // call
    auto getRet = getProxy(&key, &getResult);
    if (getRet != FIT_OK || getResult == nullptr) {
        return FIT_ERR_NOT_FOUND;
    }
    value = *getResult;
    return FIT_OK;
}

FitCode ConfigurationClientV2::DownloadWithKey(const string &key, ItemValueSet &out) const
{
    matata::conf::client::download proxy;
    Fit::map<Fit::string, Fit::string> *configValues = nullptr;
    auto ret = proxy(&key, &configValues);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to download config. [key=%s, ret=%x]", key.c_str(), ret);
        return ret;
    }
    if (configValues == nullptr) {
        return FIT_OK;
    }

    FIT_LOG_INFO("Get the config. [key=%s, size=%lu]", key.c_str(), configValues->size());
    for (const auto &item : *configValues) {
        FIT_LOG_INFO("Config item. [name=%s, value=%s]", item.first.c_str(), item.second.c_str());
        out.push_back(ItemValue {item.first, item.second});
    }

    return FIT_OK;
}

void ConfigurationClientV2::Notify(const Fit::string &key, ItemValueSet &out) const
{
    std::lock_guard<std::recursive_mutex> guard(mt_);
    auto iter = genericablesNotifyGroup_.find(key);
    if (iter == genericablesNotifyGroup_.end()) {
        return;
    }

    iter->second(key, out);
}

void ConfigurationClientV2::Notify(const Fit::string &key, const Fit::string &value) const
{
    std::lock_guard<std::recursive_mutex> guard(mt_);
    auto iter = valueNotifyGroup_.find(key);
    if (iter == valueNotifyGroup_.end()) {
        return;
    }

    iter->second(key, value);
}

bool ConfigurationClientV2::IsSubscribedPath(const Fit::string &key) const
{
    std::lock_guard<std::recursive_mutex> guard(mt_);
    return genericablesNotifyGroup_.find(key) != genericablesNotifyGroup_.end();
}

bool ConfigurationClientV2::IsSubscribedValue(const Fit::string &key) const
{
    std::lock_guard<std::recursive_mutex> guard(mt_);
    return valueNotifyGroup_.find(key) != valueNotifyGroup_.end();
}

std::function<void(const Fit::bytes &)> ConfigurationClientV2::BuildChangeCallback()
{
    return [this](const Fit::bytes &msg) {
        auto changedGenericables = StringUtils::Split(msg.data(), '\n');
        for (const auto &item : changedGenericables) {
            if (IsSubscribedPath(item)) {
                ItemValueSet configValues;
                if (DownloadWithKey(item, configValues) == FIT_OK) {
                    Notify(item, configValues);
                }
            }

            if (IsSubscribedValue(item)) {
                Fit::string value;
                if (Get(item, value) == FIT_OK) {
                    Notify(item, value);
                }
            }
        }
    };
}

bool ConfigurationClientV2::Start()
{
    auto systemConfig = GetRuntime().GetElementIs<SystemConfig>();
    if (systemConfig == nullptr) {
        FIT_LOG_ERROR("System config is needed.");
        return false;
    }
    environment_ = systemConfig->GetEnvName();
    groupName_ = systemConfig->GetWorkerId();
    consumerName_ = systemConfig->GetAppName();
    return true;
}

bool ConfigurationClientV2::Stop()
{
    return true;
}

FitCode Consume(ContextObj _ctx, const Fit::string *topic, const Fit::bytes *data)
{
    if (!data || !topic) {
        FIT_LOG_ERROR("Invalid request param, topic = %s, data = %s.", topic->c_str(), data->data());
        return FIT_ERR_PARAM;
    }

    FIT_LOG_INFO("Changed event, topic = %s, data = %s.", topic->c_str(), data->data());

    if (g_mqGroupCallback) {
        g_mqGroupCallback(*data);
    }

    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(Consume)
        .SetGenericId(matata::notification::client::consume::GENERIC_ID)
        .SetFitableId("consume_cpp");
}
}
} // LCOV_EXCL_LINE