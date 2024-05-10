/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#include <domain/fitable.hpp>

#include <domain/application.hpp>
#include <domain/genericable.hpp>
#include <registry_listener.hpp>
#include <util/singleton_utils.hpp>
#include <fit/external/util/string_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

Fitable::Fitable(const FitableRepoPtr& repo, Fit::string id, Fit::string version)
    : repo_(repo), id_(std::move(id)), version_(std::move(version))
{
}

RegistryListenerPtr Fitable::GetRegistryListener() const
{
    GenericablePtr genericable = GetGenericable();
    return (genericable == nullptr) ? nullptr : (genericable->GetRegistryListener());
}

FitableRepoPtr Fitable::GetRepo() const
{
    return repo_.lock();
}

GenericablePtr Fitable::GetGenericable() const
{
    FitableRepoPtr repo = GetRepo();
    return (repo == nullptr) ? nullptr : (repo->GetGenericable());
}

const Fit::string& Fitable::GetId() const
{
    return id_;
}

const Fit::string& Fitable::GetVersion() const
{
    return version_;
}

int32_t Fitable::Compare(const FitablePtr& another) const
{
    if (another.get() == this) {
        return 0;
    } else {
        return Compare(another->GetId(), another->GetVersion());
    }
}

int32_t Fitable::Compare(const string& id, const string&) const
{
    // only compare id, the version is not used.
    return id_.compare(id);
}

void Fitable::Remove()
{
    FitableRepoPtr repo = repo_.lock();
    if (repo != nullptr) {
        repo->Remove(id_, version_);
    }
}

FitableApplicationRepoPtr Fitable::GetApplications()
{
    return SingletonUtils::Get<FitableApplicationRepo>(applications_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateFitableApplicationRepo(shared_from_this());
    });
}

FitableUnavailableEndpointRepoPtr Fitable::GetUnavailableEndpoints()
{
    return SingletonUtils::Get<FitableUnavailableEndpointRepo>(unavailableEndpoints_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateFitableUnavailableEndpointRepo(shared_from_this());
    });
}

string Fitable::ToString() const
{
    return StringUtils::Format("[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
        GetGenericable()->GetId().c_str(), GetGenericable()->GetVersion().c_str(),
        GetId().c_str(), GetVersion().c_str());
}
