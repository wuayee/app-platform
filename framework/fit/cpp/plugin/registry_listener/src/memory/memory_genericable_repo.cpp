/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory genericable repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <memory/memory_genericable_repo.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryGenericableRepo::MemoryGenericableRepo(const RegistryListenerPtr& registryListener)
    : registryListener_(registryListener)
{
}

RegistryListenerPtr MemoryGenericableRepo::GetRegistryListener() const
{
    return registryListener_.lock();
}

GenericablePtr MemoryGenericableRepo::Get(const string& id, const string& version, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &version](const GenericablePtr& existing) -> int32_t {
        return existing->Compare(id, version);
    };
    int32_t index = VectorUtils::BinarySearch<GenericablePtr>(genericables_, compare);
    GenericablePtr genericable;
    if (index > -1) {
        genericable = genericables_[index];
    } else if (createNew) {
        genericable = std::make_shared<Genericable>(shared_from_this(), id, version);
        VectorUtils::Insert(genericables_, -1 - index, genericable);
    } else {
        genericable = nullptr;
    }
    return genericable;
}

GenericablePtr MemoryGenericableRepo::Remove(const Fit::string& id, const Fit::string& version)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &version](const GenericablePtr& existing) -> int32_t {
        return existing->Compare(id, version);
    };
    int32_t index = VectorUtils::BinarySearch<GenericablePtr>(genericables_, compare);
    GenericablePtr genericable;
    if (index > -1) {
        genericable = VectorUtils::Remove(genericables_, index);
    } else {
        genericable = nullptr;
    }
    return genericable;
}

Fit::vector<GenericablePtr> MemoryGenericableRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return genericables_;
}
