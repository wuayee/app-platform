/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory fitable repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <repo/fitable_repo.hpp>

#include <domain/genericable.hpp>
#include <memory/memory_fitable_repo.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

Registry::Listener::MemoryFitableRepo::MemoryFitableRepo(const GenericablePtr& genericable)
    : genericable_(genericable)
{
}

RegistryListenerPtr MemoryFitableRepo::GetRegistryListener() const
{
    GenericablePtr genericable = GetGenericable();
    return (genericable == nullptr) ? nullptr : (genericable->GetRegistryListener());
}

GenericablePtr MemoryFitableRepo::GetGenericable() const
{
    return genericable_.lock();
}

FitablePtr MemoryFitableRepo::Get(const Fit::string& id, const Fit::string& version, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &version](const FitablePtr& existing) -> int32_t {
        return existing->Compare(id, version);
    };
    int32_t index = VectorUtils::BinarySearch<FitablePtr>(fitables_, compare);
    FitablePtr fitable;
    if (index > -1) {
        fitable = fitables_[index];
    } else if (createNew) {
        fitable = std::make_shared<Fitable>(shared_from_this(), id, version);
        VectorUtils::Insert(fitables_, -1 - index, fitable);
    } else {
        fitable = nullptr;
    }
    return fitable;
}

FitablePtr MemoryFitableRepo::Remove(const string& id, const string& version)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &version](const FitablePtr& existing) -> int32_t {
        return existing->Compare(id, version);
    };
    int32_t index = VectorUtils::BinarySearch<FitablePtr>(fitables_, compare);
    FitablePtr fitable;
    if (index > -1) {
        fitable = VectorUtils::Remove(fitables_, index);
    } else {
        fitable = nullptr;
    }
    return fitable;
}

vector<FitablePtr> MemoryFitableRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return fitables_;
}