/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide meta interface for repo.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#include <v3/fit_fitable_meta/include/fit_fitable_meta_service_for_repo.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace Fit {
namespace Registry {
FitFitableMetaServiceForRepo::FitFitableMetaServiceForRepo(Fit::shared_ptr<FitMemoryFitableOperation> fitableMetaRepo)
    : fitableMetaRepo_(fitableMetaRepo)
{
}
int32_t FitFitableMetaServiceForRepo::Save(
    const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas)
{
    int32_t ret = FIT_ERR_SUCCESS;
    for (const auto& fitableMeta : fitableMetas) {
        auto temp = fitableMetaRepo_->Save(Fit::make_shared<Fit::RegistryInfo::FitableMeta>(fitableMeta));
        if (temp != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR("Save fitable meta failed, (genenricableId,fitableId):(%s,%s).",
                fitableMeta.fitable.genericableId.c_str(), fitableMeta.fitable.fitableId.c_str());
            ret = temp;
        }
    }
    return ret;
}
Fit::vector<Fit::RegistryInfo::FitableMeta> FitFitableMetaServiceForRepo::Query(
    const Fit::vector<Fit::string>& genericableIds, const Fit::string& environment)
{
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas;
    for (const auto& genericableId : genericableIds) {
        auto fitableMetaPtrs = fitableMetaRepo_->Query(genericableId);
        for (const auto& fitableMetaPtr : fitableMetaPtrs) {
            if (environment != fitableMetaPtr->environment) {
                continue;
            }
            fitableMetas.emplace_back(*fitableMetaPtr);
        }
    }
    return fitableMetas;
}

Fit::vector<Fit::RegistryInfo::FitableMeta> FitFitableMetaServiceForRepo::Query(
    const Fit::RegistryInfo::Fitable& fitable, const Fit::string& environment)
{
    auto fitableMetaPtrs = fitableMetaRepo_->Query(fitable);
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas;
    fitableMetas.reserve(fitableMetaPtrs.size());
    for (const auto& fitableMetaPtr : fitableMetaPtrs) {
        if (environment != fitableMetaPtr->environment) {
            continue;
        }
        fitableMetas.emplace_back(*fitableMetaPtr);
    }
    return fitableMetas;
}
int32_t FitFitableMetaServiceForRepo::Remove(
    const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& environment)
{
    // 目前将环境标计算到application version中，不需要env
    // 后续按照环境标划分，再启用
    int32_t ret = FIT_ERR_SUCCESS;
    for (const auto& application : applications) {
        auto temp = fitableMetaRepo_->Remove(application);
        if (temp != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR("Remove application failed, (name,version):(%s,%s).",
                application.name.c_str(), application.nameVersion.c_str());
            ret = temp;
        }
    }
    return ret;
}

bool FitFitableMetaServiceForRepo::IsApplicationExist(const Fit::RegistryInfo::Application& application)
{
    return fitableMetaRepo_->IsApplicationExist(application);
}
}
}