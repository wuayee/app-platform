/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory worker repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <memory/memory_worker_repo.hpp>

#include <domain/application.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryWorkerRepo::MemoryWorkerRepo(const ApplicationPtr& application)
    : application_(application)
{
}

RegistryListenerPtr MemoryWorkerRepo::GetRegistryListener() const
{
    ApplicationPtr application = GetApplication();
    return (application == nullptr) ? nullptr : (application->GetRegistryListener());
}

ApplicationPtr MemoryWorkerRepo::GetApplication() const
{
    return application_.lock();
}

WorkerPtr MemoryWorkerRepo::Get(
    const string& id, const string& environment, const map<string, string>& extensions, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &environment, &extensions](const WorkerPtr& existing) -> int32_t {
        return existing->Compare(id, environment, extensions);
    };
    int32_t index = VectorUtils::BinarySearch<WorkerPtr>(workers_, compare);
    WorkerPtr worker;
    if (index > -1) {
        worker = workers_[index];
    } else if (createNew) {
        worker = std::make_shared<Worker>(shared_from_this(), id, environment, extensions);
        VectorUtils::Insert(workers_, -1 - index, worker);
    } else {
        worker = nullptr;
    }
    return worker;
}

WorkerPtr MemoryWorkerRepo::Remove(const string& id, const string& environment, const map<string, string>& extensions)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&id, &environment, &extensions](const WorkerPtr& existing) -> int32_t {
        return existing->Compare(id, environment, extensions);
    };
    int32_t index = VectorUtils::BinarySearch<WorkerPtr>(workers_, compare);
    WorkerPtr worker;
    if (index > -1) {
        worker = Util::VectorUtils::Remove(workers_, index);
    } else {
        worker = nullptr;
    }
    return worker;
}

uint32_t MemoryWorkerRepo::Count() const
{
    lock_guard<mutex> guard {mutex_};
    return workers_.size();
}

vector<WorkerPtr> MemoryWorkerRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return workers_;
}
