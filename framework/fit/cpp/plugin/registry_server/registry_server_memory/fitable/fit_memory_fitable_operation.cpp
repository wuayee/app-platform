/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#include "fit_memory_fitable_operation.h"
#include <fit/fit_log.h>

namespace {
void PrintFitableMeta(const Fit::RegistryInfo::FitableMeta& fitableMeta,
    Fit::string type = "")
{
    Fit::string formats;
    for (const auto& format : fitableMeta.formats) {
        formats = Fit::to_string(format) + ":";
    }
    FIT_LOG_DEBUG("Fitable type is %s, appname:version(%s:%s), "
    "gid:fid:gvsion:fvesion(%s:%s:%s:%s), formats %s.",
        type.c_str(),
        fitableMeta.application.name.c_str(), fitableMeta.application.nameVersion.c_str(),
        fitableMeta.fitable.genericableId.c_str(), fitableMeta.fitable.fitableId.c_str(),
        fitableMeta.fitable.genericableVersion.c_str(), fitableMeta.fitable.fitableVersion.c_str(),
        formats.c_str());
}
bool CheckFitable(const Fit::RegistryInfo::Fitable& fitable)
{
    auto ret = fitable.genericableId.empty() || fitable.fitableId.empty() || fitable.genericableVersion.empty();
    if (ret) {
        FIT_LOG_ERROR("Fitable is error, gid:fid:gversion:fversion [%s:%s:%s:%s].",
            fitable.genericableId.c_str(), fitable.fitableId.c_str(),
            fitable.genericableVersion.c_str(), fitable.fitableVersion.c_str());
    }
    return !ret;
}
bool CheckApplication(const Fit::RegistryInfo::Application& app)
{
    auto ret = app.name.empty() || app.nameVersion.empty();
    if (ret) {
        FIT_LOG_ERROR("Application is error, name:version (%s:%s).",
            app.name.c_str(), app.nameVersion.c_str());
    }
    return !ret;
}
void PrintFitable(const Fit::RegistryInfo::Fitable& fitable, const Fit::RegistryInfo::Application& app,
    Fit::string type = "")
{
    FIT_LOG_DEBUG("Fitable type is %s, appname:version(%s:%s), gid:fid:gversion:fversion[%s:%s:%s:%s].", type.c_str(),
        app.name.c_str(), app.nameVersion.c_str(),
        fitable.genericableId.c_str(), fitable.fitableId.c_str(),
        fitable.genericableVersion.c_str(), fitable.fitableVersion.c_str());
}
}

namespace Fit {
namespace Registry {
int32_t FitMemoryFitableOperation::Save(std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr)
{
    if (fitableMetaPtr == nullptr ||
        !CheckApplication(fitableMetaPtr->application) ||
        !CheckFitable(fitableMetaPtr->fitable)) {
        FIT_LOG_ERROR("FitableMetaPtr is nullptr.");
        return FIT_ERR_FAIL;
    }
    Fit::unique_lock<Fit::mutex> lockApplication(fitableMetaSetIndexByApplicationMutex_);
    Fit::unique_lock<Fit::mutex> lockFitable(fitableMetaSetIndexByFitableMutex_);
    Fit::unique_lock<Fit::mutex> lockGenericable(fitableMetaSetIndexByGenericableMutex_);

    auto& currentRef = fitableMetaSetIndexByFitable_[fitableMetaPtr->fitable][fitableMetaPtr->application];
    // 获取原有存在的数据用于清理索引使用
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> originalFitableMeta = move(currentRef);
    currentRef = move(fitableMetaPtr);
    if (fitableMetaSetIndexByApplication_.count(currentRef->application) == 0) {
        FIT_LOG_INFO("App is online, app=%s.", currentRef->application.GetStrId().c_str());
    }
    fitableMetaSetIndexByApplication_[currentRef->application][currentRef->fitable] = currentRef;
    UpdateGenericableIndex(originalFitableMeta, currentRef);

    PrintFitableMeta(*currentRef, "Save");

    return FIT_OK;
}

void FitMemoryFitableOperation::UpdateGenericableIndex(const FitableMetaPtr& original, const FitableMetaPtr& current)
{
    // 更新genericable索引，首先删除原有存在的数据
    auto& genericableIndexRef = fitableMetaSetIndexByGenericable_[current->fitable.genericableId];
    if (original) {
        genericableIndexRef.erase(original);
    }
    genericableIndexRef.emplace(current);
}

void FitMemoryFitableOperation::RemoveGenericableIndex(const FitableMetaPtr& target)
{
    auto iter = fitableMetaSetIndexByGenericable_.find(target->fitable.genericableId);
    if (iter == fitableMetaSetIndexByGenericable_.end()) {
        return;
    }
    iter->second.erase(target);
    if (iter->second.empty()) {
        fitableMetaSetIndexByGenericable_.erase(iter);
        FIT_LOG_INFO("All genericable's fitables are removed. (genericable=%s).",
            target->fitable.genericableId.c_str());
    }
}

void FitMemoryFitableOperation::RemoveFitableIndex(const FitableMetaPtr& target)
{
    auto fitableMetaIndexByApplication = fitableMetaSetIndexByFitable_.find(target->fitable);
    if (fitableMetaIndexByApplication != fitableMetaSetIndexByFitable_.end()) {
        PrintFitable(target->fitable, target->application, "Remove");
        auto fitableMetaIt = fitableMetaIndexByApplication->second.find(target->application);
        if (fitableMetaIt != fitableMetaIndexByApplication->second.end()) {
            fitableMetaIndexByApplication->second.erase(fitableMetaIt);
        }
    }
    if (fitableMetaIndexByApplication->second.empty()) {
        fitableMetaSetIndexByFitable_.erase(fitableMetaIndexByApplication);
        FIT_LOG_INFO("All fitable's apps are removed. (genericable=%s:%s).",
            target->fitable.genericableId.c_str(), target->fitable.fitableId.c_str());
    }
}

FitMemoryFitableOperation::FitableMetaPtrSet FitMemoryFitableOperation::Query(
    const Fit::RegistryInfo::Fitable& fitable)
{
    Fit::unique_lock<Fit::mutex> lockFitable(fitableMetaSetIndexByFitableMutex_);
    PrintFitable(fitable, Fit::RegistryInfo::Application(), "Query");
    FitMemoryFitableOperation::FitableMetaPtrSet result;
    auto fitableMetaSetIt = fitableMetaSetIndexByFitable_.find(fitable);
    if (fitableMetaSetIt == fitableMetaSetIndexByFitable_.end()) {
        return FitMemoryFitableOperation::FitableMetaPtrSet();
    }
    for (const auto& it : fitableMetaSetIt->second) {
        if (it.second == nullptr) {
            FIT_LOG_ERROR("Fitable pointer is nullptr.");
            continue;
        }
        result.emplace_back(it.second);
        PrintFitableMeta(*it.second, "Query after");
    }
    return result;
}

std::shared_ptr<Fit::RegistryInfo::FitableMeta> FitMemoryFitableOperation::Query(
    const Fit::RegistryInfo::FitableMeta& fitableMeta)
{
    if (!CheckApplication(fitableMeta.application) ||
        !CheckFitable(fitableMeta.fitable)) {
        FIT_LOG_ERROR("Fitable param error.");
        return nullptr;
    }
    Fit::unique_lock<Fit::mutex> lockFitable(fitableMetaSetIndexByFitableMutex_);
    auto fitableMetaSetIt = fitableMetaSetIndexByFitable_.find(fitableMeta.fitable);
    if (fitableMetaSetIt == fitableMetaSetIndexByFitable_.end()) {
        return nullptr;
    }
    auto fitableMetaIt = fitableMetaSetIt->second.find(fitableMeta.application);
    if (fitableMetaIt == fitableMetaSetIt->second.end()) {
        return nullptr;
    }
    return fitableMetaIt->second;
}

FitMemoryFitableOperation::FitableMetaPtrSet FitMemoryFitableOperation::Query(
    const Fit::RegistryInfo::Application& application)
{
    FitMemoryFitableOperation::FitableMetaPtrSet result;
    if (!CheckApplication(application)) {
        FIT_LOG_ERROR("Fitable param error.");
        return result;
    }
    Fit::unique_lock<Fit::mutex> lockFitable(fitableMetaSetIndexByApplicationMutex_);
    auto fitableMetaSetIt = fitableMetaSetIndexByApplication_.find(application);
    if (fitableMetaSetIt == fitableMetaSetIndexByApplication_.end()) {
        return FitMemoryFitableOperation::FitableMetaPtrSet();
    }
    auto it = fitableMetaSetIt->second.begin();
    for (; it != fitableMetaSetIt->second.end();) {
        auto temp = it->second.lock();
        if (temp == nullptr) {
            it = fitableMetaSetIt->second.erase(it);
        } else {
            result.emplace_back(temp);
            ++it;
        }
    }
    return result;
}

int32_t FitMemoryFitableOperation::Remove(const Fit::RegistryInfo::Application& application)
{
    Fit::unique_lock<Fit::mutex> lockApplication(fitableMetaSetIndexByApplicationMutex_);
    Fit::unique_lock<Fit::mutex> lockFitable(fitableMetaSetIndexByFitableMutex_);
    Fit::unique_lock<Fit::mutex> lockGenericable(fitableMetaSetIndexByGenericableMutex_);
    auto fitableMetaIndexByFitableIt = fitableMetaSetIndexByApplication_.find(application);
    if (fitableMetaIndexByFitableIt == fitableMetaSetIndexByApplication_.end()) {
        return FIT_ERR_SUCCESS;
    }
    auto it = fitableMetaIndexByFitableIt->second.begin();
    for (; it != fitableMetaIndexByFitableIt->second.end();) {
        auto temp = it->second.lock();
        if (temp == nullptr) {
            it = fitableMetaIndexByFitableIt->second.erase(it);
            continue;
        }
        RemoveFitableIndex(temp);
        RemoveGenericableIndex(temp);
        ++it;
    }
    FIT_LOG_INFO("App is offline, app=%s.", application.GetStrId().c_str());
    fitableMetaSetIndexByApplication_.erase(fitableMetaIndexByFitableIt);
    return FIT_ERR_SUCCESS;
}
FitMemoryFitableOperation::FitableMetaPtrSet FitMemoryFitableOperation::Query(const string& genericId) const
{
    FitableMetaPtrSet result;
    Fit::unique_lock<Fit::mutex> guard(fitableMetaSetIndexByGenericableMutex_);
    auto iter = fitableMetaSetIndexByGenericable_.find(genericId);
    if (iter == fitableMetaSetIndexByGenericable_.end()) {
        return result;
    }
    auto& fitableMetaSet = iter->second;
    result.reserve(fitableMetaSet.size());
    result.assign(fitableMetaSet.begin(), fitableMetaSet.end());

    return result;
}

bool FitMemoryFitableOperation::IsApplicationExist(const Fit::RegistryInfo::Application& application)
{
    Fit::unique_lock<Fit::mutex> lockApplication(fitableMetaSetIndexByApplicationMutex_);
    return fitableMetaSetIndexByApplication_.count(application) != 0;
}

Fit::shared_ptr<FitMemoryFitableOperation> FitMemoryFitableOperation::Create()
{
    return Fit::make_shared<FitMemoryFitableOperation>();
}
}
} // LCOV_EXCL_BR_LINE
