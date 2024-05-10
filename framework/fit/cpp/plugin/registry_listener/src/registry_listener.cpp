/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for registry listener.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/13
 */

#include <registry_listener.hpp>

#include <util/singleton_utils.hpp>

#include <fit/fit_log.h>

namespace Fit {
namespace Registry {
namespace Listener {
RegistryListener::RegistryListener(
    RegistryListenerSpiPtr spi, RepoFactoryPtr repoFactory, uint32_t isolationExpiration)
    : spi_(std::move(spi)), repoFactory_(std::move(repoFactory)), isolationExpiration_(isolationExpiration)
{
    taskScheduler_ = TaskScheduler::WithSingleThread();
}

RegistryListener::~RegistryListener()
{
    taskScheduler_->Shutdown();
}

RepoFactoryPtr RegistryListener::GetRepoFactory() const
{
    return repoFactory_;
}

const RegistryListenerSpiPtr& RegistryListener::GetSpi() const
{
    return spi_;
}

FitablePtr RegistryListener::GetFitable(const string& genericableId, const string& genericableVersion,
    const string& fitableId, const string& fitableVersion, bool createNew)
{
    GenericablePtr genericable = GetGenericables()->Get(genericableId, genericableVersion, createNew);
    if (genericable == nullptr) {
        return nullptr;
    } else {
        return genericable->GetFitables()->Get(fitableId, fitableVersion, createNew);
    }
}

ApplicationPtr RegistryListener::GetApplication(
    const Fit::string& name, const Fit::string& version, const map<string, string>& extensions, bool createNew)
{
    if (applications_ == nullptr) {
        lock_guard<mutex> guard {mutex_};
        if (applications_ == nullptr) {
            applications_ = repoFactory_->CreateApplicationRepo(shared_from_this());
        }
    }
    return applications_->Get(name, version, extensions, createNew);
}

vector<FitableInfo> RegistryListener::ListFitables()
{
    vector<FitableInfo> fitableInfos {};
    for (auto& genericable: GetGenericables()->List()) {
        for (auto& fitable: genericable->GetFitables()->List()) {
            FitableInfo fitableInfo {};
            fitableInfo.genericableId = fitable->GetGenericable()->GetId();
            fitableInfo.genericableVersion = fitable->GetGenericable()->GetVersion();
            fitableInfo.fitableId = fitable->GetId();
            fitableInfo.fitableVersion = fitable->GetVersion();
            fitableInfos.push_back(fitableInfo);
        }
    }
    return fitableInfos;
}

void RegistryListener::SubscribeFitables(const vector<fit::hakuna::kernel::shared::Fitable>& fitableInfos)
{
    for (auto& fitableInfo : fitableInfos) {
        FIT_LOG_DEBUG("Subscribe fitable addresses. "
                      "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
            fitableInfo.genericableId.c_str(), fitableInfo.genericableVersion.c_str(),
            fitableInfo.fitableId.c_str(), fitableInfo.fitableVersion.c_str());
        GetGenericables()->Get(fitableInfo.genericableId, fitableInfo.genericableVersion, true)
            ->GetFitables()->Get(fitableInfo.fitableId, fitableInfo.fitableVersion, true);
    }
    for (auto& callback : fitablesSubscribedCallbacks_) {
        callback(fitableInfos);
    }
}

void RegistryListener::UnsubscribeFitables(const vector<FitableInfo>& fitableInfos)
{
    for (auto& fitableInfo : fitableInfos) {
        FIT_LOG_DEBUG("Unsubscribe fitable addresses. "
                      "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
            fitableInfo.genericableId.c_str(), fitableInfo.genericableVersion.c_str(),
            fitableInfo.fitableId.c_str(), fitableInfo.fitableVersion.c_str());
        GenericablePtr genericable = GetGenericables()->Get(
            fitableInfo.genericableId, fitableInfo.genericableVersion, false);
        if (genericable == nullptr) {
            FIT_LOG_DEBUG("Skip to remove fitable because genericable not found. "
                          "[genericableId=%s, genericableVersion=%s]",
                fitableInfo.genericableId.c_str(), fitableInfo.genericableVersion.c_str());
            continue;
        }
        FitablePtr fitable = genericable->GetFitables()->Get(
            fitableInfo.fitableId, fitableInfo.fitableVersion, false);
        if (fitable == nullptr) {
            FIT_LOG_DEBUG("Skip to remove fitable because fitable not found. "
                          "[fitableId=%s, fitableVersion=%s]",
                fitableInfo.fitableId.c_str(), fitableInfo.fitableVersion.c_str());
            continue;
        }
        fitable->Remove();
    }
    for (auto& callback : fitablesUnsubscribedCallbacks_) {
        callback(fitableInfos);
    }
}

void RegistryListener::ObserveFitablesSubscribed(std::function<void(const vector<FitableInfo>&)> callback)
{
    fitablesSubscribedCallbacks_.push_back(std::move(callback));
}

void RegistryListener::ObserveFitablesUnsubscribed(std::function<void(const vector<FitableInfo>&)> callback)
{
    fitablesUnsubscribedCallbacks_.push_back(std::move(callback));
}

FitableInstance* RegistryListener::GetAddresses(ContextObj& ctx, const FitableInfo& fitableInfo)
{
    return GetAddresses(ctx, fitableInfo, true);
}

ApplicationPtr RegistryListener::GetApplication(const ApplicationInfo& info, bool createNew)
{
    return GetApplication(info.name, info.nameVersion, info.extensions, createNew);
}

FitablePtr RegistryListener::GetFitable(const FitableInfo& info, bool createNew)
{
    return GetFitable(info.genericableId, info.genericableVersion, info.fitableId, info.fitableVersion, createNew);
}

void RegistryListener::Isolate(const FitableInfo& fitableInfo, const WorkerInfo& workerInfo)
{
    FitablePtr fitable = GetFitable(fitableInfo, false);
    if (fitable == nullptr) {
        FIT_LOG_DEBUG("Skip to isolate fitable address because fitable not found. [genericableId=%s, fitableId=%s]",
            fitableInfo.genericableId.c_str(), fitableInfo.fitableId.c_str());
    } else {
        for (auto& address : workerInfo.addresses) {
            for (auto& endpoint : address.endpoints) {
                fitable->GetUnavailableEndpoints()->Add(address.host, endpoint.port, isolationExpiration_);
            }
        }
    }
}

void RegistryListener::ScheduleTask(TaskPtr task, uint32_t interval)
{
    taskScheduler_->Schedule(std::move(task), interval);
}

void RegistryListener::UnscheduleTask(const TaskPtr& task)
{
    taskScheduler_->Unschedule(task);
}

GenericableRepoPtr RegistryListener::GetGenericables()
{
    return SingletonUtils::Get<GenericableRepo>(genericables_, mutex_, [&]() {
        return repoFactory_->CreateGenericableRepo(shared_from_this());
    });
}

ApplicationInstance ConvertTo(ApplicationFitablePtr applicationFitable, ContextObj& ctx, uint32_t& count,
    const FitablePtr& fitable)
{
    ApplicationInstance outputApplicationInstance;
    outputApplicationInstance.formats = applicationFitable->GetFormats();

    // 设置应用程序信息
    ApplicationPtr application = applicationFitable->GetApplication();
    outputApplicationInstance.application
        = ::Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::Application>(ctx);
    outputApplicationInstance.application->name = application->GetName();
    outputApplicationInstance.application->nameVersion = application->GetVersion();
    outputApplicationInstance.application->extensions = application->GetExtensions();

    // 设置工作进程信息
    vector<WorkerPtr> workers = application->GetWorkers()->List();
    outputApplicationInstance.workers.reserve(workers.size());
    for (auto& worker : workers) {
        ::fit::hakuna::kernel::registry::shared::Worker outputWorker;
        outputWorker.id = worker->GetId();
        outputWorker.environment = worker->GetEnvironment();
        outputWorker.extensions = worker->GetExtensions();

        auto endpoints = worker->GetEndpoints()->List();
        map<string, vector<::fit::hakuna::kernel::registry::shared::Endpoint>> outputEndpoints;
        for (const auto& endpoint : endpoints) {
            if (!fitable->GetUnavailableEndpoints()->Contains(endpoint->GetHost(), endpoint->GetPort())) {
                ::fit::hakuna::kernel::registry::shared::Endpoint outputEndpoint;
                outputEndpoint.port = endpoint->GetPort();
                outputEndpoint.protocol = endpoint->GetProtocol();
                outputEndpoints[endpoint->GetHost()].push_back(outputEndpoint);
            }
        }

        for (auto& pair : outputEndpoints) {
            ::fit::hakuna::kernel::registry::shared::Address outputAddress;
            outputAddress.host = pair.first;
            outputAddress.endpoints = pair.second;
            outputWorker.addresses.push_back(outputAddress);
            count += pair.second.size();
        }

        outputApplicationInstance.workers.push_back(outputWorker);
    }
    return outputApplicationInstance;
}

FitableInstance* RegistryListener::GetAddresses(ContextObj& ctx, const FitableInfo& fitableInfo, bool subscribeNew)
{
    FIT_LOG_DEBUG("Start to lookup addresses for fitable. "
        "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
        fitableInfo.genericableId.c_str(), fitableInfo.genericableVersion.c_str(),
        fitableInfo.fitableId.c_str(), fitableInfo.fitableVersion.c_str());
    FitablePtr fitable = GetFitable(fitableInfo.genericableId, fitableInfo.genericableVersion,
        fitableInfo.fitableId, fitableInfo.fitableVersion, false);
    if (fitable == nullptr) {
        if (subscribeNew) {
            SubscribeFitables(vector<fit::hakuna::kernel::shared::Fitable> {fitableInfo});
            return GetAddresses(ctx, fitableInfo, false);
        } else {
            return nullptr;
        }
    }
    auto instance = ::Fit::Context::NewObj<FitableInstance>(ctx);
    instance->fitable = ::Fit::Context::NewObj<FitableInfo>(ctx);
    instance->fitable->genericableId = fitable->GetGenericable()->GetId();
    instance->fitable->genericableVersion = fitable->GetGenericable()->GetVersion();
    instance->fitable->fitableId = fitable->GetId();
    instance->fitable->fitableVersion = fitable->GetVersion();
    vector<ApplicationFitablePtr> applicationFitables = fitable->GetApplications()->List();
    instance->applicationInstances.reserve(applicationFitables.size());
    uint32_t count = 0;
    for (auto& applicationFitable : applicationFitables) {
        instance->applicationInstances.push_back(ConvertTo(applicationFitable, ctx, count, fitable));
    }
    FIT_LOG_DEBUG("Total %u endpoints found for fitable. [genericable=%s, fitable=%s]",
        count, fitableInfo.genericableId.c_str(), fitableInfo.fitableId.c_str());
    return instance;
}
}
}
}
