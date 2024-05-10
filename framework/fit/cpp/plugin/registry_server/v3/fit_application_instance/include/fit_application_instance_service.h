/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance interface.
 * Author       : w00561424
 * Date         : 2023/09/06
 * Notes:       :
 */
#ifndef FIT_APPLICATION_INSTANCE_SERVICE_H
#define FIT_APPLICATION_INSTANCE_SERVICE_H
#include <fit/internal/registry/fit_registry_entity.h>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitApplicationInstanceService {
public:
    virtual ~FitApplicationInstanceService() = default;
    virtual int32_t Save(const Fit::vector<Fit::RegistryInfo::ApplicationInstance>& applicationInstances) = 0;

    virtual Fit::vector<Fit::RegistryInfo::ApplicationInstance> Query(
        const Fit::vector<Fit::RegistryInfo::Application>& applications) = 0;
    virtual Fit::vector<Fit::RegistryInfo::ApplicationInstance> Query(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId) = 0;

    virtual int32_t Remove(const Fit::string& workerId) = 0;
    virtual int32_t Remove(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId) = 0;
    virtual int32_t Check(const Fit::string& workerId, const Fit::string& workerVersion) = 0;
};
using FitApplicationInstanceServicePtr = Fit::shared_ptr<FitApplicationInstanceService>;
}
}
#endif