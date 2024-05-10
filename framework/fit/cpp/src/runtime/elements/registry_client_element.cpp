/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#include "registry_client_element.hpp"
#include <fit/internal/fit_string_util.h>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/util/string_utils.hpp>
#include <genericable/com_huawei_fit_heartbeat_online_heartbeat/1.0.0/cplusplus/onlineHeartbeat.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_set_registry_address/1.0.0/cplusplus/setRegistryAddress.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_remove_resgitry_address/1.0.0/cplusplus/removeResgitryAddress.hpp>
#include <register_fitable_v2.h>
#include <register_fitable.h>

using namespace Fit;
using Framework::Formatter::FormatterService;
using Framework::Formatter::FormatterServicePtr;
using Framework::FitableDiscovery;
using Framework::FitableDiscoveryPtr;
using Config::SystemConfig;
using ::fit::heartbeat::onlineHeartbeat;

namespace Fit {
constexpr uint32_t PRINT_LOG_FREQUENCY = 300; // 当失败后，每5分钟打印一次错误日志
constexpr uint32_t INTERVAL_TIME = 5; // 定时周期为5s
RegistryClientElement::RegistryClientElement() : RuntimeElementBase("registryClient") {}

RegistryClientElement::~RegistryClientElement() = default;

bool RegistryClientElement::Start()
{
    if (!HasRegistryClient()) {
        FIT_LOG_INFO("Working with no registry client, will not register anything.");
        return true;
    }
    commonConfig_ = make_shared<CommonConfig>(GetRuntime().GetElementIs<SystemConfig>());
    fitableDiscovery_ = FitableDiscoveryPtr(GetRuntime().GetElementIs<FitableDiscovery>(), [](FitableDiscovery*) {});
    formatterService_ = FormatterServicePtr(GetRuntime().GetElementIs<FormatterService>(), [](FormatterService*) {});
    serverAddresses_ = FitSystemPropertyUtils::Addresses();
    Configuration::ConfigurationServicePtr configurationService
        = Configuration::ConfigurationService::BaseConfigurationService();
    registerFitable_ = make_shared<RegisterFitableV2>(formatterService_, commonConfig_, configurationService);

    if (StartRegistrarTask() != FIT_OK) {
        FIT_LOG_ERROR("Start registrar task failed.");
        return false;
    }
    if (PublishIfHasRegistry() != FIT_OK) {
        FIT_LOG_ERROR("Publish registry failed.");
    }
    FIT_LOG_INFO("Init registry node success.");
    return true;
}

bool RegistryClientElement::Stop()
{
    threadExitFlag_ = true;
    if (registerTimer_) {
        registerTimer_->remove(timerHandle_);
        timerHandle_ = Fit::timer::INVALID_TASK_ID;
        registerTimer_->stop();
        registerTimer_.reset();
    }
    if (threadPool_) {
        threadPool_->stop();
        threadPool_.reset();
    }

    return true;
}

void RegistryClientElement::EnableFitables(const string& genericId, const vector<string>& fitIds)
{
    auto enabledFitables = GetRuntime().GetElementIs<FitableDiscovery>()->EnableFitables(genericId, fitIds);
    if (enabledFitables.empty()) {
        FIT_LOG_INFO("There is no fitables to enable.");
        return;
    }
    auto ret = RegisterFitables(fitableDiscovery_->GetAllLocalFitables());
    FIT_LOG_INFO("EnableFitables register result %d.", ret);
}

void RegistryClientElement::DisableFitables(const string& genericId, const vector<string>& fitIds)
{
    auto disabledFitables = GetRuntime().GetElementIs<FitableDiscovery>()->DisableFitables(genericId, fitIds);
    if (disabledFitables.empty()) {
        FIT_LOG_INFO("There is no fitables to disable.");
        return;
    }
    auto ret = RegisterFitables(fitableDiscovery_->GetAllLocalFitables());
    FIT_LOG_INFO("DisableFitables register result %d.", ret);
}

bool RegistryClientElement::HasRegistryClient()
{
    // 当前是否配置心跳客户端，作为是否注册服务的依据，如果后续registry_client提取为插件后，这里应替换判断条件
    return !GetRuntime().GetElementIs<FitableDiscovery>()->GetLocalFitableByGenericId(
        onlineHeartbeat::GENERIC_ID).empty();
}

FitCode RegistryClientElement::RegisterFitableInner(const Framework::Annotation::FitableDetailPtrList &fitables)
{
    return registerFitable_->RegisterFitService(fitables, DEFAULT_EXPIRE);
}
// 将注册任务丢入线程池，线程池执行线程为单线程；
// 保证动态注册和周期注册是单线程执行的，以保证注册数据的一致性
FitCode RegistryClientElement::RegisterFitables(const Framework::Annotation::FitableDetailPtrList &fitables)
{
    if (threadPool_) {
        return threadPool_->push([this, fitables] {
            return RegisterFitableInner(fitables);
        }).get();
    }

    return FIT_ERR_FAIL;
}

FitCode RegistryClientElement::UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables)
{
    if (threadPool_) {
        return threadPool_->push([this, fitables] {
            // 需要先卸载掉当前应用，重新注册新的应用
            return registerFitable_->UnregisterFitService(fitables);
        }).get();
    }

    return FIT_ERR_FAIL;
}

FitCode RegistryClientElement::PublishIfHasRegistry()
{
    if (HasRegistry()) {
        ClearRegistryAddress();
        SetRegistryAddress();
    }
    return FIT_OK;
}
bool RegistryClientElement::HasRegistry()
{
    Framework::Fitable fitable {};
    fitable.genericId = fit::hakuna::kernel::registry::server::registerFitables::GENERIC_ID;
    fitable.fitableId = "dedaa28cfb2742819a9b0271bc34f72a";
    fitable.genericVersion = "1.0.0";

    auto registerFitables = fitableDiscovery_->GetLocalFitable(fitable);
    if (registerFitables.empty()) {
        return false;
    }
    FIT_LOG_DEBUG("Registry node.");
    return true;
}
int32_t RegistryClientElement::StartRegistrarTask()
{
    if (serverAddresses_.empty() || registerFitable_ == nullptr) {
        return FIT_OK;
    }

    // 启动注册/注销服务任务池，以单线程运行，单线程保证消息的线性属性，因注册/注销是同步过程，极端情况下的消息乱序永不会出现
    constexpr int32_t regThreadNum = 2;
    threadPool_ = make_shared<Thread::thread_pool>(regThreadNum);
    threadPool_->set_name("fit.reg.client");
    registerTimer_ = make_unique<timer>(threadPool_);
    auto ret = RegisterFitables(fitableDiscovery_->GetAllLocalFitables());
    FIT_LOG_INFO("First register, ret (%x).", ret);
    if (commonConfig_->GetRegisteServiceFailedExitFlag() && ret != FIT_OK) {
        FIT_LOG_ERROR("Register failed, will exit.");
        return FIT_ERR_FAIL;
    }
    timerHandle_ = registerTimer_->set_interval(
        std::chrono::milliseconds(std::chrono::seconds(INTERVAL_TIME)).count(),
        threadPool_.get(),
        &RegistryClientElement::RegistrarTick, this);

    return FIT_ERR_SUCCESS;
}

FitCode RegistryClientElement::ClearRegistryAddress()
{
    // 删除相同workerId的所有注册中心地址
    fit::hakuna::kernel::registry::server::removeResgitryAddress removeResgitryAddress;
    fit::registry::Address addressTemp;
    addressTemp.id = commonConfig_->GetWorkerId();
    int32_t* result {nullptr};
    auto removeRet = removeResgitryAddress(&addressTemp, &result);
    if (removeRet != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Remove registry address error.[code=%x]", removeRet);
    }
    return removeRet;
}

FitCode RegistryClientElement::SetRegistryAddress()
{
    if (serverAddresses_.empty()) {
        return FIT_OK;
    }
    auto formats = formatterService_->GetFormats(
        ::fit::hakuna::kernel::registry::server::queryFitablesAddresses::GENERIC_ID);
    fit::hakuna::kernel::registry::server::setRegistryAddress setRegistryAddressInvoker;
    for (const auto &address : serverAddresses_) {
        fit::registry::Address addressWithFormats = address;
        addressWithFormats.formats = formats;
        addressWithFormats.environment = commonConfig_->GetWorkerEnvironment();
        auto ret = setRegistryAddressInvoker(&addressWithFormats);
        if (ret != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR("Publish address of registry error.[code=%x]", ret);
            return ret;
        }
        FIT_LOG_INFO("Publish address of registry.[%s-%s:%d:%d:%s]", addressWithFormats.id.c_str(),
            addressWithFormats.host.c_str(), addressWithFormats.port, addressWithFormats.protocol,
            join_to_string<Fit::vector<int32_t>>(addressWithFormats.formats, ",").c_str());
    }

    return FIT_OK;
}

void RegistryClientElement::RegistrarTick()
{
    if (threadExitFlag_) {
        return;
    }
    threadPool_->push([this] {
        int32_t ret = registerFitable_->CheckFitService(fitableDiscovery_->GetAllLocalFitables());
        if (ret == FIT_ERR_NOT_EXIST) {
            auto registerRet = RegisterFitableInner(fitableDiscovery_->GetAllLocalFitables());
            FIT_LOG_WARN("Check app or worker is diff, %x:%x.", ret, registerRet);
        }
    });
}
}