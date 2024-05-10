/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for address synchronizer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/17
 */

#include <sync/address_synchronizer.hpp>

#include <fit/fit_log.h>
#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;
AddressSynchronizerBase::AddressSynchronizerBase(RegistryListenerPtr registryListener)
    : registryListener_(std::move(registryListener))
{
}

RegistryListenerPtr AddressSynchronizerBase::GetRegistryListener() const
{
    return registryListener_;
}

void AddressSynchronizerBase::AcceptChanges(
    const vector<::fit::hakuna::kernel::registry::shared::FitableInstance>& fitableInstances)
{
    for (const auto& fitableInstance : fitableInstances) {
        AcceptFitableChanges(fitableInstance);
    }
}

void AddressSynchronizerBase::AcceptFitableChanges(
    const ::fit::hakuna::kernel::registry::shared::FitableInstance& fitableInstance)
{
    FIT_LOG_DEBUG("Synchronize addresses for fitable. "
        "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
        fitableInstance.fitable->genericableId.c_str(),
        fitableInstance.fitable->genericableVersion.c_str(),
        fitableInstance.fitable->fitableId.c_str(),
        fitableInstance.fitable->fitableVersion.c_str());
    FitablePtr fitable = registryListener_->GetFitable(*(fitableInstance.fitable), true);

    vector<ApplicationPtr> usedApplications {};

    for (auto& applicationInstance : fitableInstance.applicationInstances) {
        AcceptApplicationInstanceChange(fitable, usedApplications, applicationInstance);
    }

    vector<ApplicationFitablePtr> allApplications = fitable->GetApplications()->List();
    for (auto& application : allApplications) {
        auto compare = [&application](const ApplicationPtr& existing) -> int32_t {
            return existing->Compare(application->GetApplication());
        };
        int32_t index = VectorUtils::BinarySearch<ApplicationPtr>(usedApplications, compare);
        if (index < 0) {
            fitable->GetApplications()->Remove(application->GetApplication());
        }
    }
}

void AddressSynchronizerBase::AcceptApplicationInstanceChange(
    FitablePtr& fitable,
    vector<ApplicationPtr>& usedApplications,
    const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstance)
{
    if (applicationInstance.workers.empty()) {
        ApplicationPtr application = registryListener_->GetApplication(*(applicationInstance.application), false);
        if (application != nullptr) {
            fitable->GetApplications()->Remove(application);
        }
    } else {
        if (applicationInstance.application == nullptr || applicationInstance.application->name.empty()) {
            for (auto& worker : applicationInstance.workers) {
                ApplicationPtr application = registryListener_->GetApplication(worker.id, "", {}, true);
                AcceptApplicationChanges(fitable, application, applicationInstance, {worker}, usedApplications);
            }
        } else {
            ApplicationPtr application = registryListener_->GetApplication(*(applicationInstance.application),
                true);
            AcceptApplicationChanges(fitable, application, applicationInstance,
                applicationInstance.workers, usedApplications);
        }
    }
}

void AddressSynchronizerBase::AcceptApplicationChanges(FitablePtr& fitable, ApplicationPtr& application,
    const ApplicationInstance& applicationInstance, const vector<WorkerInfo>& workers,
    vector<ApplicationPtr>& usedApplications)
{
    ApplicationFitablePtr applicationFitable = fitable->GetApplications()->Get(application, true);
    applicationFitable->SetFormats(applicationInstance.formats);
    AcceptWorkerChanges(application->GetWorkers(), workers);
    if (application->GetWorkers()->Count() > 0) {
        auto compare = [&applicationFitable](const ApplicationPtr& existing) -> int32_t {
            return existing->Compare(applicationFitable->GetApplication());
        };
        int32_t index = VectorUtils::BinarySearch<ApplicationPtr>(usedApplications, compare);
        if (index < 0) {
            VectorUtils::Insert(usedApplications, -1 - index, applicationFitable->GetApplication());
        }
    }
}

void AddressSynchronizerBase::AcceptWorkerChanges(const WorkerRepoPtr& repo,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Worker>& infos)
{
    vector<WorkerPtr> usedWorkers {};
    for (const auto& info : infos) {
        WorkerPtr worker = repo->Get(info.id, info.environment, info.extensions, true);
        AcceptEndpoints(worker, info);
        if (worker->GetEndpoints()->Count() > 0) {
            auto compare = [&worker](const WorkerPtr& existing) -> int32_t {
                return existing->Compare(worker);
            };
            int32_t index = VectorUtils::BinarySearch<WorkerPtr>(usedWorkers, compare);
            if (index < 0) {
                VectorUtils::Insert(usedWorkers, -1 - index, worker);
            }
        }
    }
    vector<WorkerPtr> allWorkers = repo->List();
    for (auto& worker : allWorkers) {
        auto compare = [&worker](const WorkerPtr& existing) -> int32_t {
            return existing->Compare(worker);
        };
        int32_t index = VectorUtils::BinarySearch<WorkerPtr>(usedWorkers, compare);
        if (index < 0) {
            worker->Remove();
        }
    }
}

void AddressSynchronizerBase::AcceptEndpoints(const WorkerPtr& worker,
    const ::fit::hakuna::kernel::registry::shared::Worker& info)
{
    vector<WorkerEndpointPtr> usedEndpoints {};
    for (const auto& host : info.addresses) {
        for (const auto& endpointInfo : host.endpoints) {
            WorkerEndpointPtr endpoint = worker->GetEndpoints()->Get(
                host.host, endpointInfo.port, endpointInfo.protocol, true);
            auto compare = [&endpoint](const WorkerEndpointPtr& existing) -> int32_t {
                return existing->Compare(endpoint);
            };
            int32_t index = VectorUtils::BinarySearch<WorkerEndpointPtr>(usedEndpoints, compare);
            if (index < 0) {
                VectorUtils::Insert(usedEndpoints, -1 - index, endpoint);
            }
        }
    }
    vector<WorkerEndpointPtr> allEndpoints = worker->GetEndpoints()->List();
    for (auto& endpoint : allEndpoints) {
        auto compare = [&endpoint](const WorkerEndpointPtr& existing) -> int32_t {
            return existing->Compare(endpoint);
        };
        int32_t index = VectorUtils::BinarySearch<WorkerEndpointPtr>(usedEndpoints, compare);
        if (index < 0) {
            endpoint->Remove();
        }
    }
}
