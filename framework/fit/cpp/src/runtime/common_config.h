/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/9/3
 * Notes:       :
 */

#ifndef FIT_COMMON_CONFIG_H
#define FIT_COMMON_CONFIG_H

#include <cstdint>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
#include <fit/internal/runtime/config/system_config_internal.hpp>
#include <genericable/com_huawei_fit_hakuna_system_shared_get_application_extensions/1.0.0/cplusplus/get_application_extensions.hpp>
#include <genericable/com_huawei_fit_hakuna_system_shared_get_worker_extensions/1.0.0/cplusplus/get_worker_extensions.hpp>
namespace Fit {
constexpr const char* RENEW_KEY = "renew_period";
constexpr const char* RENEW_INTERVAL_KEY = "internal_seconds";
constexpr const char* RENEW_TIMES_KEY = "times";
constexpr const char* RENEW_TIMEOUTTIME_KEY = "timeout_seconds";
constexpr const uint32_t DEFAULT_INTERVAL = 10;
constexpr const int32_t ENDLESS_FLAG = -1;
constexpr const uint32_t DEFAULT_EXPIRE = 90;
constexpr const char* APP_VERSION = "app.version";
struct RenewInfo {
    uint32_t interval{DEFAULT_INTERVAL};
    int32_t times{ENDLESS_FLAG};
    uint32_t timeoutTime{DEFAULT_EXPIRE};
};

class CommonConfig {
public:
    explicit CommonConfig(Fit::Config::SystemConfig* systemConfig)
    {
        systemConfig_ = (Fit::Config::SystemConfigInternal*)(systemConfig);
    }

    ~CommonConfig() = default;

    string GetWorkerEnvironment()
    {
        const char* defaultEnvironment = "debug";
        const char* environmentKey = "environment";
        return systemConfig_->GetValue(environmentKey).AsString(defaultEnvironment);
    }

    string GetWorkerId()
    {
        const char* key = "worker_id";
        return systemConfig_->GetValue(key).AsString();
    }

    string GetEnvironmentChain()
    {
        const char* key = "environment_chain";
        return systemConfig_->GetValue(key).AsString(GetWorkerEnvironment());
    }

    // 返回true退出，返回false不退出
    bool GetRegisteServiceFailedExitFlag()
    {
        const char* key = "register_service_failed_exit_flag";
        return systemConfig_->GetValue(key).AsBool(true);
    }

    Fit::string GetAppName()
    {
        return systemConfig_->GetAppName();
    }

    void SetAppVersion(const Fit::string& appVersion)
    {
        systemConfig_->SetValue(APP_VERSION, appVersion.c_str());
    }

    map<string, string> GetAppExtensions() const
    {
        map<string, string>* result;
        fit::hakuna::system::shared::getApplicationExtensions proxy;
        auto ret = proxy(&result);
        if (ret != FIT_OK || result == nullptr) {
            FIT_LOG_ERROR("Get application extensions failed %d.", ret);
            return map<string, string> {};
        }
        return *result;
    }
    map<string, string> GetWorkerExtensions() const
    {
        map<string, string>* result;
        fit::hakuna::system::shared::getWorkerExtensions proxy;
        auto ret = proxy(&result);
        if (ret != FIT_OK || result == nullptr) {
            FIT_LOG_ERROR("Failed to get worker extensions, ret=%d.", ret);
            return map<string, string> {};
        }
        return *result;
    }

    int32_t GetRenewInfo(Fit::vector<RenewInfo>& renewInfoSet)
    {
        auto &renewInfoArray = systemConfig_->GetValue(RENEW_KEY);
        if (renewInfoArray.IsNull() || !renewInfoArray.IsArray()) {
            renewInfoSet.clear();
            renewInfoSet.emplace_back(RenewInfo());
            return FIT_ERR_FAIL;
        }

        for (int32_t i = 0; i < renewInfoArray.Size(); i++) {
            RenewInfo renewInfo;
            auto& cfgRenewInterval = renewInfoArray[i][RENEW_INTERVAL_KEY];
            if (cfgRenewInterval.IsInt()) {
                renewInfo.interval = static_cast<uint32_t>(cfgRenewInterval.AsInt());
            }
            auto& cfgRenewTimes = renewInfoArray[i][RENEW_TIMES_KEY];
            if (cfgRenewTimes.IsInt()) {
                renewInfo.times = cfgRenewTimes.AsInt();
            }
            auto& cfgRenewTimeout = renewInfoArray[i][RENEW_TIMEOUTTIME_KEY];
            if (cfgRenewTimeout.IsInt()) {
                renewInfo.timeoutTime = static_cast<uint32_t>(cfgRenewTimeout.AsInt());
            }
            renewInfoSet.push_back(renewInfo);
        }
        if (renewInfoSet.empty()) {
            renewInfoSet.emplace_back(RenewInfo());
        }
        return FIT_ERR_SUCCESS;
    }

private:
    Fit::Config::SystemConfigInternal* systemConfig_ {nullptr};
};
}

#endif