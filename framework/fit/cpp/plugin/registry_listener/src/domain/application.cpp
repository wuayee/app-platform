/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for application.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#include <domain/application.hpp>

#include <domain/fitable.hpp>
#include <domain/genericable.hpp>
#include <registry_listener.hpp>
#include <util/singleton_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

Application::Application(
    const ApplicationRepoPtr& repo, Fit::string name, Fit::string version, map<string, string> extensions)
    : repo_(repo), name_(std::move(name)), version_(std::move(version)), extensions_(move(extensions))
{
}

RegistryListenerPtr Application::GetRegistryListener() const
{
    ApplicationRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetRegistryListener());
}

const Fit::string& Application::GetName() const
{
    return name_;
}

const Fit::string& Application::GetVersion() const
{
    return version_;
}

const map<string, string> Application::GetExtensions() const
{
    return extensions_;
}

int32_t Application::Compare(const ApplicationPtr& another) const
{
    if (another.get() == this) {
        return 0;
    } else {
        return Compare(another->GetName(), another->GetVersion());
    }
}

int32_t Application::Compare(const string& name, const string& version) const
{
    int32_t ret = name_.compare(name);
    if (ret == 0) {
        ret = version_.compare(version);
    }
    return ret;
}

WorkerRepoPtr Application::GetWorkers()
{
    return SingletonUtils::Get<WorkerRepo>(workers_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateWorkerRepo(shared_from_this());
    });
}

ApplicationFitableRepoPtr Application::GetFitables()
{
    return SingletonUtils::Get<ApplicationFitableRepo>(fitables_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateApplicationFitableRepo(shared_from_this());
    });
}

void Application::Remove()
{
    ApplicationRepoPtr repo = repo_.lock();
    if (repo != nullptr) {
        repo->Remove(name_, version_);
    }
}
