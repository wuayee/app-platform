/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory application repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <memory/memory_application_repo.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryApplicationRepo::MemoryApplicationRepo(const RegistryListenerPtr& registryListener)
    : registryListener_(registryListener)
{
}

RegistryListenerPtr MemoryApplicationRepo::GetRegistryListener() const
{
    return registryListener_.lock();
}

ApplicationPtr MemoryApplicationRepo::Get(
    const string& name, const string& version, const map<string, string>& extensions, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&name, &version](const ApplicationPtr& existing) -> int32_t {
        return existing->Compare(name, version);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationPtr>(applications_, compare);
    ApplicationPtr application;
    if (index > -1) {
        application = applications_[index];
    } else if (createNew) {
        application = std::make_shared<Application>(shared_from_this(), name, version, extensions);
        VectorUtils::Insert(applications_, -1 - index, application);
    } else {
        application = nullptr;
    }
    return application;
}

ApplicationPtr MemoryApplicationRepo::Remove(const string& name, const string& version)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&name, &version](const ApplicationPtr& existing) -> int32_t {
        return existing->Compare(name, version);
    };
    int32_t index = VectorUtils::BinarySearch<ApplicationPtr>(applications_, compare);
    ApplicationPtr application;
    if (index > -1) {
        application = VectorUtils::Remove(applications_, index);
    } else {
        application = nullptr;
    }
    return application;
}

uint32_t MemoryApplicationRepo::Count() const
{
    lock_guard<mutex> guard {mutex_};
    return applications_.size();
}
