/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory fitable application repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#include <memory/memory_fitable_application_repo.hpp>

#include <domain/application.hpp>
#include <domain/fitable.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryFitableApplicationRepo::MemoryFitableApplicationRepo(const FitablePtr& fitable)
    : fitable_(fitable)
{
}

RegistryListenerPtr MemoryFitableApplicationRepo::GetRegistryListener() const
{
    FitablePtr fitable = GetFitable();
    return (fitable == nullptr) ? nullptr : (fitable->GetRegistryListener());
}

FitablePtr MemoryFitableApplicationRepo::GetFitable() const
{
    return fitable_.lock();
}

ApplicationFitablePtr MemoryFitableApplicationRepo::Get(const ApplicationPtr& application, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &application](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(application, GetFitable());
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = relations_[index];
    } else if (createNew) {
        relation = std::make_shared<ApplicationFitable>(application, GetFitable());
        Util::VectorUtils::Insert(relations_, -1 - index, relation);
        application->GetFitables()->Attach(relation);
    } else {
        relation = nullptr;
    }
    return relation;
}

ApplicationFitablePtr MemoryFitableApplicationRepo::Remove(const ApplicationPtr& application)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &application](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(application, GetFitable());
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = Util::VectorUtils::Remove(relations_, index);
        application->GetFitables()->RemoveOnlyMyself(GetFitable());
    } else {
        relation = nullptr;
    }
    return relation;
}

ApplicationFitablePtr MemoryFitableApplicationRepo::RemoveOnlyMyself(const ApplicationPtr& application)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [this, &application](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(application, GetFitable());
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    ApplicationFitablePtr relation;
    if (index > -1) {
        relation = Util::VectorUtils::Remove(relations_, index);
    } else {
        relation = nullptr;
    }
    return relation;
}

void MemoryFitableApplicationRepo::Attach(ApplicationFitablePtr relation)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&relation](const ApplicationFitablePtr& existing) -> int32_t {
        return existing->Compare(relation);
    };
    int32_t index = Util::VectorUtils::BinarySearch<ApplicationFitablePtr>(relations_, compare);
    if (index < 0) {
        VectorUtils::Insert(relations_, -1 - index, std::move(relation));
    }
}

vector<ApplicationFitablePtr> MemoryFitableApplicationRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return relations_;
}

