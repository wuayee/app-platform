/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/6/15 21:50
 * Notes        :
 */

#include "util.h"
#include <fit/stl/set.hpp>
#include <sstream>
#include <fit/fit_log.h>
#include <fit/internal/fit_system_property_utils.h>
#include <core/fit_registry_conf.h>
#include <genericable/com_huawei_fit_registry_get_registry_addresses/1.0.0/cplusplus/getRegistryAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include "core/fit_registry_mgr.h"
#include "registry/repository/fit_registry_application_repo.h"
#include "registry_app_meta_desc.h"

namespace Fit {
namespace Registry {
using ApplicationInstanceIndex = Fit::unordered_map<Fit::RegistryInfo::Application,
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance,
    Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;
using ::fit::hakuna::kernel::registry::shared::Worker;

static void TryFillApplicationMeta(RegistryInfo::ApplicationMeta& app)
{
    auto appRepo = fit_registry_mgr::instance()->get_application_repo();
    if (appRepo == nullptr) {
        return;
    }
    appRepo->Query(app.id, app);
}
vector<RegistryInfo::FlatAddress> GetRegistryAddresses()
{
    fit::registry::getRegistryAddresses proxy;
    ::fit::hakuna::kernel::registry::shared::FitableInstance *registryAddresses;
    auto ret = proxy(&registryAddresses);
    if (ret != FIT_ERR_SUCCESS || registryAddresses == nullptr) {
        FIT_LOG_ERROR("Failed to get registry addresses. (ret=%x).", ret);
        return {};
    }
    FIT_LOG_DEBUG("Successful to get registry addresses. (count=%lu).", registryAddresses->applicationInstances.size());

    vector<RegistryInfo::FlatAddress> addresses;
    auto flatEndpoints = [&addresses](const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& instance,
        const ::fit::hakuna::kernel::registry::shared::Worker& worker,
        const ::fit::hakuna::kernel::registry::shared::Address& address) {
        for (auto& endpoint : address.endpoints) {
            RegistryInfo::FlatAddress flatAddress;
            flatAddress.workerId = worker.id;
            flatAddress.environment = worker.environment;
            flatAddress.host = address.host;
            flatAddress.port = endpoint.port;
            flatAddress.protocol = static_cast<fit_protocol_type>(endpoint.protocol);
            flatAddress.extensions = worker.extensions;
            addresses.emplace_back(std::move(flatAddress));
        }
    };
    for (auto& instance : registryAddresses->applicationInstances) {
        for (auto& worker : instance.workers) {
            for (auto& address : worker.addresses) {
                flatEndpoints(instance, worker, address);
            }
        }
    }

    fit::registry::Address address = FitSystemPropertyUtils::Address();
    addresses.erase(std::remove_if(addresses.begin(), addresses.end(),
        [&address](const RegistryInfo::FlatAddress& v) { return v.workerId == address.id; }), addresses.end());
    return addresses;
}

void PrintService(const db_service_info_t &dbService, const string& otherText)
{
    std::ostringstream result;
    result << "onlineStatus" << "='" << dbService.is_online << ","
        << "gid" << "=" << dbService.service.fitable.generic_id << ","
        << "fid" << "='" << dbService.service.fitable.fitable_id << ",";
    for (const auto& address : dbService.service.addresses) {
        result << "ip" << "=" <<  address.ip << ","
            << "port" << "=" << address.port << ","
            << "protocol" << "=" << static_cast<uint32_t>(address.protocol) << ","
            << "id" << "=" << address.id << ",";
    }
    result << "timeout" << "=" << dbService.service.timeoutSeconds << ","
        << "applicationName" << "=" << dbService.service.application.name << ","
        << "applicationNameVersion" << "=" << dbService.service.application.nameVersion << ","
        << "handle" << "=" << dbService.handle;

    FIT_LOG_DEBUG("%s, %s.", otherText.c_str(), result.str().c_str());
}
void MergeAddress(Fit::vector<::fit::hakuna::kernel::registry::shared::Address>& addresses,
    const ::fit::hakuna::kernel::registry::shared::Address& addressIn)
{
    size_t pos = 0;
    for (; pos < addresses.size(); ++pos) {
        if (addresses[pos].host == addressIn.host) {
            addresses[pos].endpoints.emplace_back(addressIn.endpoints.front());
            return;
        }
    }
    addresses.emplace_back(addressIn);
}
::fit::hakuna::kernel::registry::shared::FitableInstance Aggregate(
    const ::fit::hakuna::kernel::shared::Fitable& fitable,
    const db_service_set& services, ContextObj ctx)
{
    ::fit::hakuna::kernel::registry::shared::FitableInstance fitableInstance {};
    fitableInstance.fitable
        = ::Fit::Context::NewObj<::fit::hakuna::kernel::shared::Fitable>(ctx);
    *(fitableInstance.fitable) = fitable;

    using ApplicationWorkers = Fit::unordered_map<Fit::RegistryInfo::Application, set<Worker*>,
        Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;
    ApplicationWorkers appWorkers;
    Fit::unordered_map<Fit::string, ::fit::hakuna::kernel::registry::shared::Worker> workerMap;
    // 获取同一id下的所有的address
    for (auto &item : services) {
        for (const auto& address : item.service.addresses) {
            ::fit::hakuna::kernel::registry::shared::Worker& worker = workerMap[address.id];
            worker.id = address.id;
            worker.environment = address.environment;
            worker.extensions = address.extensions;
            MergeAddress(worker.addresses, ::Fit::RegistryCommonConverter::ConvertToNewAddress(address));
            appWorkers[item.service.application].insert(&worker);
        }
    }
    ApplicationInstanceIndex applicationInstanceIndex;
    for (const auto& dbService : services) {
        if (dbService.service.addresses.empty()) {
            continue;
        }
        ::fit::hakuna::kernel::registry::shared::Application application =
            RegistryCommonConverter::Convert(dbService.service.application);
        ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstanceTemp
            = applicationInstanceIndex[dbService.service.application];

        if (applicationInstanceTemp.application == nullptr) {
            applicationInstanceTemp.application =
                Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::Application>(ctx);
            *(applicationInstanceTemp.application) = application;
            // 同一应用下的服务都是一致的
            for (const auto& it : dbService.service.addresses.front().formats) {
                applicationInstanceTemp.formats.push_back(static_cast<int32_t>(it));
            }
            // 收集同一服务，同一应用下的所有worker应用下
            for (auto& worker : appWorkers[dbService.service.application]) {
                applicationInstanceTemp.workers.push_back(*worker);
            }
        }
    }
    for (auto& applicationInstance : applicationInstanceIndex) {
        fitableInstance.applicationInstances.emplace_back(move(applicationInstance.second));
    }
    TryFillApplicationMeta(fitableInstance);
    PreProcessFitableInstance(fitableInstance);
    return fitableInstance;
}

bool IsSubsetOfBaseAddresses(const Fit::vector<Fit::fit_address>& baseAddresses,
    const Fit::vector<Fit::fit_address>& addressesIn)
{
    if (addressesIn.size() > baseAddresses.size()) {
        return false;
    }
    for (const auto& address : addressesIn) {
        bool isFound = false;
        for (const auto& baseAddress : baseAddresses) {
            if (address.port == baseAddress.port) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            return false;
        }
    }
    return true;
}
void TrySaveApplicationMeta(const ::fit::hakuna::kernel::registry::shared::Application& application)
{
    auto* appRepo = Registry::fit_registry_mgr::instance()->get_application_repo();
    if (appRepo != nullptr) {
        appRepo->Save(RegistryCommonConverter::ConvertApplicationMeta(application));
    }
}
void TryFillApplicationMeta(::fit::hakuna::kernel::registry::shared::Application& application)
{
    auto* appRepo = fit_registry_mgr::instance()->get_application_repo();
    if (appRepo == nullptr) {
        return;
    }
    RegistryInfo::ApplicationMeta meta {};
    if (appRepo->Query(RegistryCommonConverter::ConvertApplicationMeta(application).id, meta) == FIT_OK) {
        application = RegistryCommonConverter::ConvertApplicationMeta(meta);
    };
}
void TryFillApplicationMeta(::fit::hakuna::kernel::registry::shared::FitableInstance& result)
{
    for (auto& appInstance : result.applicationInstances) {
        TryFillApplicationMeta(*appInstance.application);
    }
}
void PreProcessFitableInstance(::fit::hakuna::kernel::registry::shared::FitableInstance& result)
{
    for (auto& appInstance : result.applicationInstances) {
        PreProcessAppInstance(appInstance);
    }
}
void PreProcessAppInstance(::fit::hakuna::kernel::registry::shared::ApplicationInstance& result)
{
    if (result.workers.empty()) {
        return;
    }
    RegistryAppMetaDesc desc(*result.application);
    string domain {};
    desc.GetClusterDomain(domain);
    if (domain.empty()) {
        return;
    }
    ::fit::hakuna::kernel::registry::shared::Address address;
    address.host = domain;
    ::fit::hakuna::kernel::registry::shared::Endpoint endpoint;
    // 当前支持http和https的集群通信方式，后续扩展其它通信协议可以基于扩展信息进行规则匹配解析
    // 这块依赖后续计划的替换protocol为字符串形式（如：grpc,http,https），更易读和维护
    if (desc.GetClusterHttpPort(endpoint.port)) {
        constexpr int32_t protocolHttp = 2;
        endpoint.protocol = protocolHttp;
        address.endpoints.emplace_back(endpoint);
    }
    if (desc.GetClusterHttpsPort(endpoint.port)) {
        constexpr int32_t protocolHttps = 4;
        endpoint.protocol = protocolHttps;
        address.endpoints.emplace_back(endpoint);
    }
    if (desc.GetClusterGrpcPort(endpoint.port)) {
        constexpr int32_t protocolGrpc = 3;
        endpoint.protocol = protocolGrpc;
        address.endpoints.emplace_back(endpoint);
    }
    string contextPath;
    desc.GetClusterContextPath(contextPath);
    // 不同环境的app的version不会相同，同一个app下worker环境标一致, 集群模式下聚合一条worker信息
    auto env = result.workers.front().environment;
    ::fit::hakuna::kernel::registry::shared::Worker worker {};
    worker.id = desc.GetName() + "_" + desc.GetVersion();
    worker.environment = result.workers.front().environment;
    worker.extensions["http.context-path"] = move(contextPath);
    worker.addresses.emplace_back(address);
    result.workers.clear();
    result.workers.emplace_back(move(worker));
}
void CompatibleJavaRegistry(fit_fitable_key_t& key)
{
    // compatible with python broker config: register fitables、heartbeat
    bool hit = false;
    auto src = key;
    if (key.generic_id == "85bdce64cf724589b87cb6b6a950999d") {
        hit = key.fitable_id != "dedaa28cfb2742819a9b0271bc34f72a";
        key.fitable_id = "dedaa28cfb2742819a9b0271bc34f72a";
    } else if (key.generic_id == "e12fd1c57fd84f50a673d93d13074082") {
        hit = key.fitable_id != "DBC9E2F7C0E443F1AC986BBC3D58C27B";
        key.fitable_id = "DBC9E2F7C0E443F1AC986BBC3D58C27B";
    }
    if (hit) {
        FIT_LOG_WARN("Java registry's fitable is querying. (fitable=%s:%s).", src.generic_id.c_str(),
                     src.fitable_id.c_str());
    }
}
}
} // LCOV_EXCL_LINE