/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-05-06 20:09:58
 */

#include <genericable/com_huawei_fit_registry_get_registry_addresses/1.0.0/cplusplus/getRegistryAddresses.hpp>
#include <genericable/com_huawei_fit_heartbeat_query_heartbeat_address_list/1.0.0/cplusplus/queryHeartbeatAddressList.hpp>
#include <genericable/com_huawei_fit_hakuna_system_shared_get_worker_extensions/1.0.0/cplusplus/get_worker_extensions.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <config.h>

using namespace Fit;
namespace {
constexpr const char* const REGISTRY_ADDRESS_KEY = "registry_address";
constexpr const char* const REGISTRY_ADDRESS_HOST = "host";
constexpr const char* const REGISTRY_ADDRESS_PORT = "port";
constexpr const char* const REGISTRY_ADDRESS_ID = "id";
constexpr const char* const REGISTRY_ADDRESS_PROTOCOL = "protocol";
constexpr const char* const REGISTRY_ADDRESS_FORMAT = "formats";
constexpr const char* const REGISTRY_ADDRESS_ENVIRONMENT = "environment";
constexpr const char* const REGISTRY_SERVER_CLUSTER_NAME = "fit_registry_server";

::fit::hakuna::kernel::registry::shared::FitableInstance &RegistryAddressesInstance()
{
    static ::fit::hakuna::kernel::registry::shared::FitableInstance instance;
    return instance;
}

void FillWorkerExtensions(Config::Value& workerNode, fit::hakuna::kernel::registry::shared::Worker& worker)
{
    if (!workerNode.IsObject()) {
        return;
    }
    auto& extensions = workerNode["extensions"];
    if (!extensions.IsObject()) {
        return;
    }
    for (const auto& key : extensions.GetKeys()) {
        worker.extensions[key] = extensions[key.c_str()].AsString("");
    }
}

void FillAppInstanceFormats(
    Config::Value& registryNode, fit::hakuna::kernel::registry::shared::ApplicationInstance& appInstance)
{
    if (!registryNode.IsObject()) {
        return;
    }
    Fit::vector<int32_t> formats {};
    auto& formatItem = registryNode[REGISTRY_ADDRESS_FORMAT];
    for (int32_t j = 0; j < formatItem.Size(); ++j) {
        formats.push_back(formatItem[j].AsInt());
    }
    appInstance.formats = move(formats);
}

void FillWorker(Config::Value& registryNode, fit::hakuna::kernel::registry::shared::Worker& worker)
{
    if (!registryNode.IsObject()) {
        return;
    }
    worker.environment = registryNode[REGISTRY_ADDRESS_ENVIRONMENT].AsString();
    worker.id = registryNode[REGISTRY_ADDRESS_ID].AsString("");
    worker.addresses.resize(worker.addresses.size() + 1);
    auto& address = worker.addresses.back();
    address.host = registryNode[REGISTRY_ADDRESS_HOST].AsString();
    address.endpoints.resize(address.endpoints.size() + 1);
    auto& endpoint = address.endpoints.back();
    endpoint.port = registryNode[REGISTRY_ADDRESS_PORT].AsInt();
    endpoint.protocol = registryNode[REGISTRY_ADDRESS_PROTOCOL].AsInt();
    FillWorkerExtensions(registryNode, worker);

    FIT_LOG_DEBUG("Registry address(%s:%d), protocol(%d), environment(%s).", address.host.c_str(), endpoint.port,
        endpoint.protocol, worker.environment.c_str());
}

FitCode Start(::Fit::Framework::PluginContext* context)
{
    /*
    [
        {
            "host" : "127.0.0.1",
            "port" : 7001,
            "id" : "test",
            "protocol" : 0,
            "formats" : [0,1,2],
            "environment" : "debug",
            "extensions": {
                "cluster.context-path: "/xxxx"
            }
        }
    ]
    */
    auto& registryAddress = context->GetConfig()->Get(REGISTRY_ADDRESS_KEY);
    if (!registryAddress.IsArray()) {
        FIT_LOG_ERROR("Config item(%s) not set.", REGISTRY_ADDRESS_KEY);
        return FIT_ERR_NOT_FOUND;
    }
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance appInstance;
    appInstance.workers.reserve(registryAddress.Size());
    for (int32_t i = 0; i < registryAddress.Size(); ++i) {
        auto& item = registryAddress[i];
        if (!item.IsObject()) {
            return FIT_ERR_PARAM;
        }
        if (appInstance.formats.empty()) {
            FillAppInstanceFormats(item, appInstance);
        }
        appInstance.workers.resize(appInstance.workers.size() + 1);
        FillWorker(item, appInstance.workers.back());
    }
    RegistryAddressesInstance().applicationInstances.emplace_back(move(appInstance));

    return FIT_OK;
}

FitCode Stop()
{
    return FIT_OK;
}

map<string, string> GetWorkerExtensions()
{
    map<string, string>* result;
    fit::hakuna::system::shared::getWorkerExtensions proxy;
    auto ret = proxy(&result);
    if (ret != FIT_OK || result == nullptr) {
        FIT_LOG_ERROR("Get worker extensions failed %d.", ret);
        return map<string, string> {};
    }
    return *result;
}

FitCode GetAddressFromClusterManager(ContextObj ctx, ::fit::hakuna::kernel::registry::shared::FitableInstance& result)
{
    fit::heartbeat::queryHeartbeatAddressList queryHeartbeatAddressListExecutor;
    Fit::string clusterName = REGISTRY_SERVER_CLUSTER_NAME;
    Fit::vector<fit::registry::Address>* addresses;
    int32_t ret = queryHeartbeatAddressListExecutor(&clusterName, &addresses); // local调用
    if (ret != FIT_OK || addresses == nullptr) {
        FIT_LOG_CORE("Query cluster view failed, %d.", ret);
        return ret;
    }

    fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    for (const auto& address : *addresses) {
        ::fit::hakuna::kernel::registry::shared::Endpoint endpointTemp;
        endpointTemp.port = address.port;
        endpointTemp.protocol = static_cast<int32_t>(address.protocol);

        ::fit::hakuna::kernel::registry::shared::Address addressTemp;
        addressTemp.endpoints.emplace_back(std::move(endpointTemp));
        addressTemp.host = address.host;

        fit::hakuna::kernel::registry::shared::Worker worker;
        worker.id = address.id;
        worker.environment = address.environment;
        worker.addresses.emplace_back(std::move(addressTemp));
        worker.extensions = GetWorkerExtensions();

        applicationInstance.workers.emplace_back(std::move(worker));
    }
    result.applicationInstances.emplace_back(std::move(applicationInstance));
    return ret;
}

FitCode GetRegistryAddresses(ContextObj ctx, ::fit::hakuna::kernel::registry::shared::FitableInstance** result)
{
    *result = Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::FitableInstance>(ctx);
    if (*result == nullptr) {
        return FIT_ERR_CTX_BAD_ALLOC;
    }

    // 注册中心内存，则不查询配置文件
    if (Fit::IsRegistryServer()) {
        return GetAddressFromClusterManager(ctx, **result);
    }

    **result = RegistryAddressesInstance();
    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetRegistryAddresses)
        .SetGenericId(fit::registry::getRegistryAddresses::GENERIC_ID)
        .SetFitableId("get_registry_addresses_impl");

    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
}