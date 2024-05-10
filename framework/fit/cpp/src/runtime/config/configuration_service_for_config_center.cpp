/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide config info manager from configuration center.
 * Author       : w00561424
 * Date         : 2023/08/26
 * Notes:       :
 */
#include <configuration_service_for_config_center.h>
#include <fit/internal/runtime/runtime.hpp>
#include <mutex>
namespace Fit {
namespace Configuration {
namespace {
template<typename Routine>
std::pair<Fit::string, RoutineFunc> MakeRoutineHelper()
{
    return std::make_pair(Routine::Key(), Routine::GetRoutine());
}

constexpr const char *GENERICABLES_CONFIG_HEADER = "fit.public.genericables.";

Fit::string BuildGenericableConfigKey(const Fit::string &genericId)
{
    return GENERICABLES_CONFIG_HEADER + genericId;
}

Fit::string ExtractGenericIdFromConfigKey(const Fit::string &key)
{
    auto genericId = key;
    genericId.replace(0, strlen(GENERICABLES_CONFIG_HEADER), "");

    return genericId;
}

bool IsGenericablesConfigKey(const Fit::string &key) noexcept
{
    return key.find(GENERICABLES_CONFIG_HEADER) == 0;
}
}

ConfigurationServiceForConfigCenter::ConfigurationServiceForConfigCenter(
    ConfigurationClientPtr client, ConfigurationRepoPtr repo)
    : client_(move(client)), repo_(move(repo))
{
    configItemRoutine_ = {
            MakeRoutineHelper<RouterRoutine>(),
            MakeRoutineHelper<LoadbalanceRoutine>(),
            MakeRoutineHelper<TagsRoutine>(),
            MakeRoutineHelper<TrustRoutine>(),
            MakeRoutineHelper<FitablesRoutine>(),
            MakeRoutineHelper<ParamsRoutine>(),
        };
}

Fit::string ConfigurationServiceForConfigCenter::Type()
{
    return "config_center";
}

int32_t ConfigurationServiceForConfigCenter::GetGenericableConfig(const Fit::string &genericId,
    GenericableConfiguration &genericable)
{
    {
        Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
        if (repo_->Get(genericId, genericable) == FIT_ERR_SUCCESS) {
            return FIT_ERR_SUCCESS;
        }
    }
    
    auto downloadRet = Download(genericId);
    if (downloadRet != FIT_ERR_SUCCESS) {
        return FIT_ERR_NOT_FOUND;
    }

    Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
    return repo_->Get(genericId, genericable);
}

GenericConfigPtr ConfigurationServiceForConfigCenter::GetGenericableConfigPtr(
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
    auto downloadRet = Download(genericId);
    if (downloadRet != FIT_ERR_SUCCESS) {
        return nullptr;
    }

    Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
    return repo_->Getter(genericId);
}

int32_t ConfigurationServiceForConfigCenter::Download(const Fit::string &genericId) const
{
    if (!client_) {
        return FIT_ERR_NOT_FOUND;
    }

    auto genericableKey = BuildGenericableConfigKey(genericId);
    if (!client_->IsSubscribed(genericableKey)) {
        client_->Subscribe(genericableKey,
            [this](const Fit::string &key, ItemValueSet &items) {
                if (!IsGenericablesConfigKey(key)) {
                    FIT_LOG_WARN("Not expected key. [key=%s]", key.c_str());
                    return;
                }
                UpdateGenericableConfig(ExtractGenericIdFromConfigKey(key), items);
            });
    }

    ItemValueSet items;
    auto downloadRet = client_->Download(genericableKey, items);
    if (downloadRet != FIT_ERR_SUCCESS) {
        return FIT_ERR_NOT_FOUND;
    }
    UpdateGenericableConfig(genericId, items);
    return downloadRet;
}

void ConfigurationServiceForConfigCenter::UpdateGenericableConfig(const Fit::string &genericId,
    const ItemValueSet &items) const
{
    auto genericableConfig = std::make_shared<GenericableConfiguration>();
    genericableConfig->SetGenericId(genericId);
    for (const auto &item : items) {
        FIT_LOG_INFO("Parse config, key:%s, value:%s.",
            item.key.c_str(), item.value.c_str());

        auto keys = StringUtils::Split(item.key, KEY_SPLIT_DELI);
        if (keys.empty()) {
            FIT_LOG_ERROR("Invalid key, generic_id = %s, key = %s.", genericId.c_str(), item.key.c_str());
            continue;
        }

        auto iter = configItemRoutine_.find(keys[0]);
        if (iter != configItemRoutine_.end()) {
            iter->second(genericableConfig, range_skip<vector<Fit::string>>(keys, 1), item.value);
        } else {
            FIT_LOG_DEBUG("Ignored config. (key=%s, value=%s).", item.key.c_str(), item.value.c_str());
        }
    }
    std::unique_lock<Fit::shared_mutex> lock(sharedMutex_);
    repo_->Set(std::move(genericableConfig));
}
}
}