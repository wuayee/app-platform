/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for base of genericable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#include <domain/genericable.hpp>

#include <registry_listener.hpp>
#include <repo_factory.hpp>
#include <util/singleton_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

Genericable::Genericable(const GenericableRepoPtr& repo, Fit::string id, Fit::string version)
    : repo_(repo), id_(std::move(id)), version_(std::move(version))
{
}

RegistryListenerPtr Genericable::GetRegistryListener() const
{
    GenericableRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetRegistryListener());
}

const Fit::string& Genericable::GetId() const
{
    return id_;
}

const Fit::string& Genericable::GetVersion() const
{
    return version_;
}

FitableRepoPtr Genericable::GetFitables()
{
    return SingletonUtils::Get<FitableRepo>(fitables_, mutex_, [&]() {
        return GetRegistryListener()->GetRepoFactory()->CreateFitableRepo(shared_from_this());
    });
}

int32_t Genericable::Compare(const GenericablePtr& another) const
{
    return Compare(another->GetId(), another->GetVersion());
}

int32_t Genericable::Compare(const string& id, const string& version) const
{
    int32_t ret = GetId().compare(id);
    if (ret == 0) {
        ret = GetVersion().compare(version);
    }
    return ret;
}
