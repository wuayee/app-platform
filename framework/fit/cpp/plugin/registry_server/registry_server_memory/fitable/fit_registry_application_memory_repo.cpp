/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application memory repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include "fit_registry_application_memory_repo.h"
#include <fit/fit_log.h>
#include <fit/stl/memory.hpp>

namespace Fit {
const char* RegistryApplicationMemoryRepo::GetName() const
{
    return "memoryRepo";
}
FitCode RegistryApplicationMemoryRepo::Save(ApplicationMeta application)
{
    auto strId = application.id.GetStrId();
    lock_guard<mutex> guard(mt_);
    auto iter = appIndexOfId_.find(strId);
    if (iter == appIndexOfId_.end()) {
        auto value = make_unique<ApplicationMeta>(move(application));
        appIndexOfName_[value->id.name].insert(value.get());
        iter = appIndexOfId_.insert(make_pair(strId, move(value))).first;
        FIT_LOG_INFO("Application is added. (id=%s).", strId.c_str());
        return FIT_OK;
    }
    *iter->second = move(application);
    return FIT_OK;
}

FitCode RegistryApplicationMemoryRepo::Delete(const Application& id)
{
    auto strId = id.GetStrId();
    lock_guard<mutex> guard(mt_);
    auto iter = appIndexOfId_.find(strId);
    if (iter == appIndexOfId_.end()) {
        return FIT_ERR_NOT_FOUND;
    }
    auto& nameRecords = appIndexOfName_[id.name];
    nameRecords.erase(iter->second.get());
    if (nameRecords.empty()) {
        appIndexOfName_.erase(id.name);
        FIT_LOG_INFO("Application is empty. (name=%s).", id.name.c_str());
    }
    appIndexOfId_.erase(iter);
    FIT_LOG_INFO("Application is removed. (id=%s).", strId.c_str());
    return FIT_OK;
}

vector<RegistryApplicationRepo::ApplicationMeta> RegistryApplicationMemoryRepo::Query(const string& appName) const
{
    vector<ApplicationMeta> result;
    lock_guard<mutex> guard(mt_);
    auto iter = appIndexOfName_.find(appName);
    if (iter == appIndexOfName_.end()) {
        return {};
    }
    result.reserve(iter->second.size());
    for (const auto& item : iter->second) {
        result.emplace_back(*item);
    }
    return result;
}

FitCode RegistryApplicationMemoryRepo::Query(const Application& id, ApplicationMeta& result) const
{
    lock_guard<mutex> guard(mt_);
    auto iter = appIndexOfId_.find(id.GetStrId());
    if (iter == appIndexOfId_.end()) {
        return FIT_ERR_NOT_FOUND;
    }
    result = *iter->second;
    return FIT_OK;
}

vector<RegistryApplicationRepo::ApplicationMeta> RegistryApplicationMemoryRepo::QueryAll() const
{
    vector<ApplicationMeta> result;
    lock_guard<mutex> guard(mt_);
    result.reserve(appIndexOfId_.size());
    for (const auto& item : appIndexOfId_) {
        result.emplace_back(*item.second);
    }
    return result;
}
}