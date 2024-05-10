/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application cached repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include "fit_registry_application_cached_repo.h"
#include <fit/fit_log.h>
#include <fit/stl/memory.hpp>

namespace Fit {
RegistryApplicationCachedRepo::RegistryApplicationCachedRepo(vector<unique_ptr<RegistryApplicationRepo>> repos)
    : repos_(move(repos))
{
}

const char* RegistryApplicationCachedRepo::GetName() const
{
    return "cachedRepo";
}

    FitCode RegistryApplicationCachedRepo::Save(ApplicationMeta application)
{
    for (auto& repo : repos_) {
        auto ret = repo->Save(application);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to save application to %s. (ret=%x, id=%s).", repo->GetName(), ret,
                application.id.GetStrId().c_str());
            return ret;
        }
    }
    return FIT_OK;
}

FitCode RegistryApplicationCachedRepo::Delete(const Application& id)
{
    for (auto& repo : repos_) {
        auto ret = repo->Delete(id);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR(
                "Failed to save application to %s. (ret=%x, id=%s).", repo->GetName(), ret, id.GetStrId().c_str());
            return ret;
        }
    }
    return FIT_OK;
}

vector<RegistryApplicationRepo::ApplicationMeta> RegistryApplicationCachedRepo::Query(const string& appName) const
{
    for (uint32_t i = 0; i < repos_.size(); ++i) {
        auto result = repos_[i]->Query(appName);
        if (result.empty()) {
            continue;
        }
        for (int32_t j = static_cast<int32_t>(i) - 1; j >= 0; --j) {
            for (const auto& item : result) {
                repos_[j]->Save(item);
            }
        }
        return result;
    }

    return {};
}

FitCode RegistryApplicationCachedRepo::Query(const Application& id, ApplicationMeta& result) const
{
    for (uint32_t i = 0; i < repos_.size(); ++i) {
        auto ret = repos_[i]->Query(id, result);
        if (ret != FIT_OK) {
            continue;
        }
        for (int32_t j = static_cast<int32_t>(i) - 1; j >= 0; --j) {
            repos_[j]->Save(result);
        }
        return FIT_OK;
    }

    return FIT_ERR_NOT_FOUND;
}

vector<RegistryApplicationRepo::ApplicationMeta> RegistryApplicationCachedRepo::QueryAll() const
{
    for (uint32_t i = 0; i < repos_.size(); ++i) {
        auto result = repos_[i]->QueryAll();
        if (result.empty()) {
            continue;
        }
        for (int32_t j = static_cast<int32_t>(i) - 1; j >= 0; --j) {
            for (const auto& item : result) {
                repos_[j]->Save(item);
            }
        }
        return result;
    }

    return {};
}
}