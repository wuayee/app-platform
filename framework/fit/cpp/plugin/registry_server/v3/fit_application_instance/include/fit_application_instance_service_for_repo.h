/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance service for repo.
 * Author       : w00561424
 * Date         : 2023/09/06
 * Notes:       :
 */
#ifndef FIT_APPLICATION_INSTANCE_FOR_REPO_H
#define FIT_APPLICATION_INSTANCE_FOR_REPO_H
#include <v3/fit_application_instance/include/fit_application_instance_service.h>
#include <registry_server_memory/fitable/fit_memory_worker_operation.h>
#include <registry_server_memory/fitable/fit_memory_address_operation.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitApplicationInstanceServiceForRepo : public FitApplicationInstanceService {
public:
    FitApplicationInstanceServiceForRepo(
        Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo, Fit::shared_ptr<FitMemoryAddressOperation> addressRepo);
    int32_t Save(const Fit::vector<Fit::RegistryInfo::ApplicationInstance>& applicationInstances) override;

    Fit::vector<Fit::RegistryInfo::ApplicationInstance> Query(
        const Fit::vector<Fit::RegistryInfo::Application>& applications) override;
    Fit::vector<Fit::RegistryInfo::ApplicationInstance> Query(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId) override;

    int32_t Remove(const Fit::string& workerId) override;
    int32_t Remove(
        const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId) override;
    int32_t Check(const Fit::string& workerId, const Fit::string& workerVersion) override;
private:
    Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo_;
    Fit::shared_ptr<FitMemoryAddressOperation> addressRepo_;
};
}
}
#endif