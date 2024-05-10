/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide meta interface.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#ifndef FIT_FITABLE_META_SERVICE_H
#define FIT_FITABLE_META_SERVICE_H
#include <fit/internal/registry/fit_registry_entity.h>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitFitableMetaService {
public:
    virtual ~FitFitableMetaService() = default;
    virtual int32_t Save(const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(
        const Fit::vector<Fit::string>& genericableIds, const Fit::string& environment) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(
        const Fit::RegistryInfo::Fitable& fitable, const Fit::string& environment) = 0;
    virtual int32_t Remove(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& environment) = 0;
    virtual bool IsApplicationExist(const Fit::RegistryInfo::Application& application) = 0;
};
using FitFitableMetaServicePtr = Fit::shared_ptr<FitFitableMetaService>;
}
}
#endif