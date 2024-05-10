/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide meta interface for repo.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#ifndef FIT_FITABLE_META_SERVICE_FOR_REPO_H
#define FIT_FITABLE_META_SERVICE_FOR_REPO_H
#include <v3/fit_fitable_meta/include/fit_fitable_meta_service.h>
#include <registry_server_memory/fitable/fit_memory_fitable_operation.h>
namespace Fit {
namespace Registry {
class FitFitableMetaServiceForRepo : public FitFitableMetaService {
public:
    FitFitableMetaServiceForRepo(Fit::shared_ptr<FitMemoryFitableOperation> fitableMetaRepo);
    int32_t Save(const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(
        const Fit::vector<Fit::string>& genericableIds, const Fit::string& environment) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(
        const Fit::RegistryInfo::Fitable& fitable, const Fit::string& environment) override;
    int32_t Remove(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& environment) override;
    bool IsApplicationExist(const Fit::RegistryInfo::Application& application) override;
private:
    Fit::shared_ptr<FitMemoryFitableOperation> fitableMetaRepo_ {};
};
}
}
#endif