/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/8/17 21:45
 * Notes:       :
 */

//


#ifndef FIT_SDK_CONTEXT_SERVICE_H
#define FIT_SDK_CONTEXT_SERVICE_H
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>

#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/map.hpp>
#include <fit/external/runtime/config/config_value.hpp>
#include <memory>
#include <thread>

namespace Fit {
namespace SDK {
namespace System {
struct PropertyValue {
    Fit::string value;
    bool isReadonly;
};
class FitSystemPropertyService;
using FitSystemPropertyServicePtr = std::shared_ptr<FitSystemPropertyService>;

class FitSystemPropertyService final {
public:
    FitSystemPropertyService() = default;
    ~FitSystemPropertyService() = default;

    static FitSystemPropertyServicePtr GetService();
    Fit::string Get(const Fit::string &key) const;
    bool Put(const Fit::string &key, const Fit::string &value, bool isReadonly);
    void Remove(const Fit::string &key);

    void SetLocalAddresses(const Fit::vector<fit::registry::Address>& addresses);
    Fit::vector<fit::registry::Address> GetLocalAddresses() const;

    void SetRegistryMatchedAddress(const fit::registry::Address& address);
    fit::registry::Address GetRegistryMatchedAddress() const;

    void SetApplicationExtensions(Config::Value& extensions);
    map<string, string> GetApplicationExtensions() const;
    void SetWorkerExtensions(Config::Value& extensions);
    map<string, string> GetWorkerExtensions() const;

    void SetMappingPorts(map<int32_t, int32_t> mappingPorts);
    vector<fit::registry::Address> GetExternalAddresses() const;
private:
    mutable Fit::mutex mt_ {};
    Fit::unordered_map<Fit::string, PropertyValue> systemProperty_;
    Fit::vector<fit::registry::Address> addresses_;
    fit::registry::Address registryMatchedAddress_ {};
    map<string, string> applicationExtensions_;
    map<string, string> workerExtensions_;
    map<int32_t, int32_t> mappingPorts_;
    Fit::vector<fit::registry::Address> externalAddresses_;
};
}
}
}
#endif // FIT_SDK_CONTEXT_SERVICE_H
