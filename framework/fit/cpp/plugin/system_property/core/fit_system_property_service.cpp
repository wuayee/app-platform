/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/8/17 21:45
 * Notes:       :
 */

//

#include "fit_system_property_service.h"
#include "fit/fit_log.h"

namespace Fit {
namespace SDK {
namespace System {
static void FillExtensions(Config::Value& v, const string& key, map<string, string>& result)
{
    // not support for array attributes
    switch (v.GetType()) {
        case Config::VALUE_TYPE_NULL:
            result.emplace(key, "null");
            break;
        case Config::VALUE_TYPE_BOOL:
            result.emplace(key, v.AsBool() ? "true" : "false");
            break;
        case Config::VALUE_TYPE_INT:
            result.emplace(key, to_string(v.AsInt()));
            break;
        case Config::VALUE_TYPE_DOUBLE:
            result.emplace(key, to_string(v.AsDouble()));
            break;
        case Config::VALUE_TYPE_STRING:
            result.emplace(key, v.AsString());
            break;
        case Config::VALUE_TYPE_OBJECT:
            for (const auto& item : v.GetKeys()) {
                FillExtensions(v[item.c_str()], key.empty() ? item : (key + "." + item), result);
            }
            break;
        default:
            FIT_LOG_DEBUG("Skip app attribute. (key=%s).", key.c_str());
            return;
    }
}

Fit::string FitSystemPropertyService::Get(const Fit::string &key) const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);

    auto value = systemProperty_.find(key);
    if (value == systemProperty_.end()) {
        FIT_LOG_INFO("Not found, Key = %s.", key.c_str());
        return "";
    }

    FIT_LOG_DEBUG("Key = %s, value = %s.", key.c_str(), value->second.value.c_str());
    return value->second.value;
}

bool FitSystemPropertyService::Put(const Fit::string &key, const Fit::string &value, bool isReadonly)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);

    auto iter = systemProperty_.find(key);
    if (iter != systemProperty_.end() && iter->second.isReadonly) {
        FIT_LOG_ERROR("Readonly. not put key = %s, value = %s.", key.c_str(), value.c_str());
        return false;
    }
    systemProperty_[key] = {value, isReadonly};

    FIT_LOG_DEBUG("Key = %s, value = %s, readonly = %d.", key.c_str(), value.c_str(), isReadonly);

    return true;
}

void FitSystemPropertyService::Remove(const Fit::string &key)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    auto iter = systemProperty_.find(key);
    if (iter != systemProperty_.end() && iter->second.isReadonly) {
        FIT_LOG_ERROR("Readonly. not remove key = %s.", key.c_str());
        return;
    }

    FIT_LOG_DEBUG("Key = %s.", key.c_str());
    systemProperty_.erase(iter);
}

FitSystemPropertyServicePtr FitSystemPropertyService::GetService()
{
    static FitSystemPropertyServicePtr fitSystemPropertyService = std::make_shared<FitSystemPropertyService>();
    return fitSystemPropertyService;
}


void FitSystemPropertyService::SetLocalAddresses(const Fit::vector<fit::registry::Address>& addresses)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    addresses_ = addresses;
    externalAddresses_ = addresses_;
    for (auto& item : externalAddresses_) {
        auto iter = mappingPorts_.find(item.port);
        if (iter != mappingPorts_.end()) {
            FIT_LOG_INFO("The mapping port. (%d->%d).", item.port, iter->second);
            item.port = iter->second;
        }
    }
}
Fit::vector<fit::registry::Address> FitSystemPropertyService::GetLocalAddresses() const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    return addresses_;
}

void FitSystemPropertyService::SetRegistryMatchedAddress(const fit::registry::Address& address)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    registryMatchedAddress_ = address;
}

fit::registry::Address FitSystemPropertyService::GetRegistryMatchedAddress() const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    return registryMatchedAddress_;
}

void FitSystemPropertyService::SetApplicationExtensions(Config::Value& extensions)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    // collect app extensions's content
    if (extensions.IsObject()) {
        FillExtensions(extensions, "", applicationExtensions_);
    }
    for (const auto& it : applicationExtensions_) {
        FIT_LOG_INFO("Application extensions, key:value (%s:%s).", it.first.c_str(), it.second.c_str());
    }
}
map<string, string> FitSystemPropertyService::GetApplicationExtensions() const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    return applicationExtensions_;
}

void FitSystemPropertyService::SetWorkerExtensions(Config::Value& extensions)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    // collect app extensions's content
    if (extensions.IsObject()) {
        FillExtensions(extensions, "", workerExtensions_);
    }
    for (const auto& it : workerExtensions_) {
        FIT_LOG_INFO("worker extensions, key:value (%s:%s).", it.first.c_str(), it.second.c_str());
    }
}
map<string, string> FitSystemPropertyService::GetWorkerExtensions() const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    return workerExtensions_;
}
void FitSystemPropertyService::SetMappingPorts(map<int32_t, int32_t> mappingPorts)
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    mappingPorts_ = move(mappingPorts);
}
vector<fit::registry::Address> FitSystemPropertyService::GetExternalAddresses() const
{
    Fit::lock_guard<Fit::mutex> guard(mt_);
    return externalAddresses_;
}
}
}
}