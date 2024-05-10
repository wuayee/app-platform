/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide query configuration service from registry server.
 * Author       : w00561424
 * Date         : 2023/08/31
 * Notes:       :
 */
#include <configuration_service_for_registry_server.h>
#include <fit/stl/vector.hpp>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <algorithm>
namespace Fit {
namespace Configuration {
constexpr const size_t MILLION_SECONDS_PER_SECOND  = 1000; // 1s等于1000ms
static const uint32_t UPDATE_CONFIG_INTERVAL_SECONDS = 30; // 更新周期为30s

ConfigurationServiceForRegistryServer::ConfigurationServiceForRegistryServer(ConfigurationRepoPtr repo,
    const Fit::string& environment, ConfigurationServiceSpiPtr spi)
    : repo_(std::move(repo)), environment_(environment), spi_(std::move(spi))
{
    timer_ = Fit::timer_instance();
    if (timer_ != nullptr) {
        updateConfigIntervalHandle_ = timer_->set_interval(
            UPDATE_CONFIG_INTERVAL_SECONDS * MILLION_SECONDS_PER_SECOND, [this]() {
            UpdateConfig();
        });
    }
}

ConfigurationServiceForRegistryServer::~ConfigurationServiceForRegistryServer()
{
    if (timer_ != nullptr) {
        timer_->remove(updateConfigIntervalHandle_);
    }
}

Fit::string ConfigurationServiceForRegistryServer::Type()
{
    return "registry_server";
}

int32_t ConfigurationServiceForRegistryServer::GetGenericableConfig(
    const Fit::string &genericId, GenericableConfiguration &genericable)
{
    {
        Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
        if (repo_->Get(genericId, genericable) == FIT_ERR_SUCCESS) {
            return FIT_ERR_SUCCESS;
        }
    }

    if (InsertGenericableId(genericId) == FIT_ERR_EXIST) {
        return FIT_ERR_NOT_FOUND;
    }
    auto ret = UpdateConfig({genericId});
    if (ret != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Update config error, gid,ret : %s:%d.", genericId.c_str(), ret);
        return FIT_ERR_NOT_FOUND;
    }

    Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
    return repo_->Get(genericId, genericable);
}

GenericConfigPtr ConfigurationServiceForRegistryServer::GetGenericableConfigPtr(
    const Fit::string &genericId) const
{
    GenericConfigPtr result;
    {
        Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
        result = repo_->Getter(genericId);
        if (result != nullptr) {
            return result;
        }
    }
    if (InsertGenericableId(genericId) == FIT_ERR_EXIST) {
        return result;
    }
    auto ret = UpdateConfig({genericId});
    if (ret != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Update config error, gid,ret : %s:%d.", genericId.c_str(), ret);
        return nullptr;
    }

    Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
    return repo_->Getter(genericId);
}

int32_t ConfigurationServiceForRegistryServer::UpdateConfig(const Fit::vector<Fit::string> &genericIds) const
{
    Fit::vector<GenericConfigPtr> genericableConfigs;
    auto ret = spi_->GetRunningFitables(genericIds, environment_, genericableConfigs);
    if (ret != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Query running fitables error : %d.", ret);
        return ret;
    }
    if (genericableConfigs.empty()) {
        return FIT_ERR_NOT_FOUND;
    }
    std::unique_lock<Fit::shared_mutex> lock(sharedMutex_);
    for (const auto& genericableConfig : genericableConfigs) {
        repo_->Set(std::move(genericableConfig));
    }
    return FIT_ERR_SUCCESS;
}

void ConfigurationServiceForRegistryServer::UpdateConfig()
{
    Fit::unordered_set<Fit::string> genericableIds;
    {
        std::unique_lock<Fit::shared_mutex> lock(sharedMutex_);
        genericableIds = queriedGenericableId_;
    }

    Fit::vector<Fit::string> genericIds(genericableIds.begin(), genericableIds.end());
    UpdateConfig(genericIds);
}

int32_t ConfigurationServiceForRegistryServer::InsertGenericableId(const Fit::string& genericableId) const
{
    int32_t ret = FIT_ERR_SUCCESS;
    std::unique_lock<Fit::shared_mutex> lock(sharedMutex_);
    auto insertRet = queriedGenericableId_.insert(genericableId);
    if (!insertRet.second) {
        ret = FIT_ERR_EXIST;
    }
    return ret;
}
}
}