/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory application fitable repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#include <memory/memory_application_fitable_repo.hpp>

#include <domain/application.hpp>
#include <domain/fitable.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryApplicationFitableRepo::MemoryApplicationFitableRepo(const ApplicationPtr& application)
    : application_(application)
{
}

RegistryListenerPtr MemoryApplicationFitableRepo::GetRegistryListener() const
{
    ApplicationPtr application = GetApplication();
    return (application == nullptr) ? nullptr : (application->GetRegistryListener());
}

ApplicationPtr MemoryApplicationFitableRepo::GetApplication() const
{
    return application_.lock();
}

ApplicationFitablePtr MemoryApplicationFitableRepo::Get(const FitablePtr& fitable, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &fitable](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(GetApplication(), fitable);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = relations_[index];
    } else if (createNew) {
        relation = std::make_shared<ApplicationFitable>(GetApplication(), fitable);
        VectorUtils::Insert(relations_, -1 - index, relation);
        fitable->GetApplications()->Attach(relation);
    } else {
        relation = nullptr;
    }
    return relation;
}

ApplicationFitablePtr MemoryApplicationFitableRepo::Remove(const FitablePtr& fitable)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &fitable](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(GetApplication(), fitable);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = VectorUtils::Remove(relations_, index);
        fitable->GetApplications()->RemoveOnlyMyself(GetApplication());
    } else {
        relation = nullptr;
    }
    return relation;
}

ApplicationFitablePtr MemoryApplicationFitableRepo::RemoveOnlyMyself(const FitablePtr& fitable)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &fitable](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(GetApplication(), fitable);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = VectorUtils::Remove(relations_, index);
    } else {
        relation = nullptr;
    }
    return relation;
}

void MemoryApplicationFitableRepo::Attach(ApplicationFitablePtr relation)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&relation](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(relation);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    if (index < 0) {
        VectorUtils::Insert(relations_, -1 - index, std::move(relation));
    }
}
