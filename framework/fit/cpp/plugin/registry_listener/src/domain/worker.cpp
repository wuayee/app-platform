/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for worker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#include <domain/worker.hpp>

#include <domain/application.hpp>
#include <registry_listener.hpp>
#include <util/singleton_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

Worker::Worker(const WorkerRepoPtr& repo, Fit::string id, Fit::string environment, map<string, string> extensions)
    : repo_(repo), id_(std::move(id)), environment_(std::move(environment)), extensions_(std::move(extensions))
{
}

RegistryListenerPtr Worker::GetRegistryListener() const
{
    ApplicationPtr application = GetApplication();
    return (application == nullptr) ? nullptr : (application->GetRegistryListener());
}

ApplicationPtr Worker::GetApplication() const
{
    WorkerRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetApplication());
}

const Fit::string& Worker::GetId() const
{
    return id_;
}

const Fit::string& Worker::GetEnvironment() const
{
    return environment_;
}

const map<string, string>& Worker::GetExtensions() const
{
    return extensions_;
}

int32_t Worker::Compare(const WorkerPtr& another) const
{
    return Compare(another->GetId(), another->GetEnvironment(), another->GetExtensions());
}

int32_t Worker::Compare(const string& id, const string& environment, const map<string, string>& extensions) const
{
    int32_t ret = GetId().compare(id);
    if (ret == 0) {
        ret = GetEnvironment().compare(environment);
    }
    if (ret == 0) {
        ret = GetExtensions() == extensions ? 0 : GetExtensions() < extensions;
    }
    return ret;
}

WorkerEndpointRepoPtr Worker::GetEndpoints()
{
    return SingletonUtils::Get<WorkerEndpointRepo>(endpoints_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateWorkerEndpointRepo(shared_from_this());
    });
}

void Worker::Remove()
{
    WorkerRepoPtr repo = repo_.lock();
    if (repo != nullptr) {
        repo->Remove(id_, environment_, extensions_);
    }
}
