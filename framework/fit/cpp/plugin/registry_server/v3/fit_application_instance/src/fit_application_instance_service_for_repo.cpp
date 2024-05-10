/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance service for repo.
 * Author       : w00561424
 * Date         : 2023/09/06
 * Notes:       :
 */
#include <v3/fit_application_instance/include/fit_application_instance_service_for_repo.h>
namespace Fit {
namespace Registry {
using namespace Fit::RegistryInfo;
FitApplicationInstanceServiceForRepo::FitApplicationInstanceServiceForRepo(
    Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo, Fit::shared_ptr<FitMemoryAddressOperation> addressRepo)
    : workerRepo_(std::move(workerRepo)), addressRepo_(std::move(addressRepo))
{
}
int32_t FitApplicationInstanceServiceForRepo::Save(
    const Fit::vector<Fit::RegistryInfo::ApplicationInstance>& applicationInstances)
{
    for (const auto& applicationInstance : applicationInstances) {
        for (const auto& worker : applicationInstance.workers) {
            auto workerMeta = Fit::make_shared<WorkerWithMeta>();
            workerMeta->worker = worker;
            workerRepo_->Save(std::move(workerMeta));
            // 清理原有workerId下的所有address
            addressRepo_->Remove(worker.workerId);
        }

        Fit::vector<Fit::shared_ptr<Fit::RegistryInfo::Address>> addressPtrs;
        addressPtrs.reserve(applicationInstance.addresses.size());
        for (const auto& address : applicationInstance.addresses) {
            addressPtrs.emplace_back(Fit::make_shared<Address>(address));
        }
        addressRepo_->Save(addressPtrs);
    }
    return FIT_OK;
}

Fit::vector<Fit::RegistryInfo::ApplicationInstance> FitApplicationInstanceServiceForRepo::Query(
    const Fit::vector<Fit::RegistryInfo::Application>& applications)
{
    Fit::vector<Fit::RegistryInfo::ApplicationInstance> applicationInstances;
    applicationInstances.reserve(applications.size());
    for (const auto& application : applications) {
        Fit::RegistryInfo::ApplicationInstance applicationInstance;
        auto workerWithMetas = workerRepo_->Query(application);
        applicationInstance.workers.reserve(workerWithMetas.size());
        for (const auto& workerWithMeta : workerWithMetas) {
            applicationInstance.workers.emplace_back(workerWithMeta->worker);
            auto addresses = addressRepo_->Query(workerWithMeta->worker.workerId);
            for (const auto& address : addresses) {
                applicationInstance.addresses.emplace_back(*address);
            }
        }
        applicationInstances.emplace_back(applicationInstance);
    }
    return applicationInstances;
}
Fit::vector<Fit::RegistryInfo::ApplicationInstance> FitApplicationInstanceServiceForRepo::Query(
    const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId)
{
    Fit::vector<Fit::RegistryInfo::ApplicationInstance> applicationInstances;
    for (const auto& application : applications) {
        Fit::RegistryInfo::ApplicationInstance applicationInstance;
        auto workerWithMetas = workerRepo_->Query(application);
        for (const auto& workerWithMeta : workerWithMetas) {
            if (workerWithMeta->worker.workerId != workerId) {
                continue;
            }
            applicationInstance.workers.emplace_back(workerWithMeta->worker);
            auto addresses = addressRepo_->Query(workerWithMeta->worker.workerId);
            for (const auto& address : addresses) {
                applicationInstance.addresses.emplace_back(*address);
            }
        }
        if (applicationInstance.workers.empty()) {
            continue;
        }
        applicationInstances.emplace_back(applicationInstance);
    }
    return applicationInstances;
}

int32_t FitApplicationInstanceServiceForRepo::Remove(const Fit::string& workerId)
{
    workerRepo_->Remove(workerId);
    return addressRepo_->Remove(workerId);
}
int32_t FitApplicationInstanceServiceForRepo::Remove(
    const Fit::vector<Fit::RegistryInfo::Application>& applications, const Fit::string& workerId)
{
    // 目前一个worker只支持一个应用
    for (const auto& application : applications) {
        workerRepo_->Remove(workerId, application);
    }
    return Remove(workerId);
}

int32_t FitApplicationInstanceServiceForRepo::Check(
    const Fit::string& workerId, const Fit::string& workerVersion)
{
    auto workerMeta = workerRepo_->Query(workerId);
    if (workerMeta == nullptr) {
        return FIT_ERR_NOT_EXIST;
    }
    if (workerMeta->worker.version != workerVersion) {
        return FIT_ERR_NOT_EXIST;
    }
    return FIT_OK;
}
}
}