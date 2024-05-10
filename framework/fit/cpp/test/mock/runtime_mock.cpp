/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/3/11
 * Notes:       :
 */

#include "runtime_mock.hpp"

#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <memory>

#include <mock/configuration_repo_mock.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <mock/system_config_mock.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/broker/broker_client_external.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>

using namespace ::testing;

namespace {
/**
 * mock for fit::sdk::system::getSystemProperty
 */
class SystemPropertyGetMock {
public:
    MOCK_METHOD3(Invoke, FitCode(ContextObj ctx,
        const Fit::string *subscription, Fit::string**paths));
};
}

namespace Fit {
class RuntimeMock::Impl {
public:
    void SetUp()
    {
        auto systemConfig = make_unique<SystemConfigMock>();
        systemConfigMock_ = systemConfig.get();
        EXPECT_CALL(*systemConfigMock_, GetWorkerId())
            .WillRepeatedly(ReturnRefOfCopy(Fit::string("")));
        EXPECT_CALL(*systemConfigMock_, GetEnvName())
            .WillRepeatedly(ReturnRefOfCopy(Fit::string("")));
        EXPECT_CALL(*systemConfigMock_, GetAppName())
            .WillRepeatedly(ReturnRefOfCopy(Fit::string("")));
        runtimeContainerMock_.AddElement(move(systemConfig));
        auto cachedFitables = ::Fit::Framework::Annotation::PopFitableDetailCache();
        oldFitableReceiver_ = ::Fit::Framework::Annotation::FitableDetailFlowTo(nullptr);
        ::Fit::Framework::Annotation::FitableCollector::Register(cachedFitables);

        auto fitableDiscoveryTmp = Framework::CreateFitableDiscovery();
        fitableDiscovery_ = Framework::FitableDiscoveryPtr(
            fitableDiscoveryTmp.get(), [](Framework::FitableDiscovery*) {});
        runtimeContainerMock_.AddElement(move(fitableDiscoveryTmp));
        FIT_LOG_INFO("cached fitable size = %ld", cachedFitables.size());
        fitableDiscovery_->RegisterLocalFitable(cachedFitables);
        PrepareBrokerClient();

        MockConfigurationService();

        PrepareSystemFitable();
    }

    void TearDown()
    {
        GetBrokerClient() = oldBrokerClient_;
        GetConfigServiceInstance() = oldConfigServicePtr;
        GetDiscoveryInstance() = oldFitableDiscoveryPtr;
        ::Fit::Framework::Annotation::FitableDetailFlowTo(oldFitableReceiver_);
    }

    RuntimeMock::Impl &RegisterFitable(::Fit::Framework::Annotation::FitableFunctionProxyType func,
        const char *genericId, const char *fitableId)
    {
        auto detail = std::make_shared<::Fit::Framework::Annotation::FitableDetail>(std::move(func));
        detail->SetGenericId(genericId).SetFitableId(fitableId);
        fitableDiscovery_->RegisterLocalFitable({detail});

        return *this;
    }

    RuntimeMock::Impl &ClearFitable()
    {
        fitableDiscovery_->ClearAllLocalFitables();

        return *this;
    }

    RuntimeMock::Impl &SetGenericableConfig(GenericableConfiguration config)
    {
        genericableConfig_[config.GetGenericId()] = config;

        return *this;
    }

    GenericableConfiguration &GetGenericableConfig(const char *genericId)
    {
        return genericableConfig_[genericId];
    }

    template<typename F>
    RuntimeMock::Impl &RegisterFitable(F &&f, const char *genericId, const char *fitableId)
    {
        return RegisterFitable(Fitable(std::forward<F>(f)), genericId, fitableId);
    }

protected:
    void PrepareBrokerClient()
    {
        oldConfigServicePtr = GetConfigServiceInstance();
        oldFitableDiscoveryPtr = GetDiscoveryInstance();
        oldBrokerClient_ = GetBrokerClient();
        GetBrokerClient() = nullptr;
        runtimeContainerMock_.AddElement(Fit::Framework::Formatter::CreateFormatterRepo());
        runtimeContainerMock_.AddElement(
            Fit::Framework::ParamJsonFormatter::CreateParamJsonFormatterService());

        auto configService = make_unique<ConfigurationServiceMock>();
        configurationServiceMock_ =
            std::shared_ptr<ConfigurationServiceMock>(configService.get(), [](ConfigurationServiceMock*) {});
        runtimeContainerMock_.AddElement(move(configService));
        runtimeContainerMock_.AddElement(make_unique<FormatterServiceMock>());

        Fit::InitBrokerInstance(fitableDiscovery_, configurationServiceMock_);
        brokerClient_ = Fit::CreateBrokerClient(&runtimeContainerMock_);
        brokerClient_->Start();
    }

    void MockConfigurationService()
    {
        EXPECT_CALL(*configurationServiceMock_, GetGenericableConfigPtr(_)).WillRepeatedly(
            Invoke([this](const string &genericId) -> GenericConfigPtr {
                FIT_LOG_WARN("get config %s", genericId.c_str());
                return std::make_shared<GenericableConfiguration>(GetGenericableConfig(genericId.c_str()));
            }));
        EXPECT_CALL(*configurationServiceMock_, GetGenericableConfig(_)).WillRepeatedly(
            Invoke([this](const string &genericId) -> GenericableConfiguration {
                return GetGenericableConfig(genericId.c_str());
            }));
        EXPECT_CALL(*configurationServiceMock_, GetGenericableDefaultFitableId(_)).WillRepeatedly(
            Invoke([this](const string &genericId) -> string {
                return GetGenericableConfig(genericId.c_str()).GetDefaultFitableId();
            }));
        EXPECT_CALL(*configurationServiceMock_, GenericableHasTag(_, _)).WillRepeatedly(
            Invoke([this](const string &genericId, const string &tag) -> bool {
                return GetGenericableConfig(genericId.c_str()).HasTag(tag);
            }));
        EXPECT_CALL(*configurationServiceMock_, GetGenericableTrust(_)).WillRepeatedly(
            Invoke([this](const string &genericId) -> TrustConfiguration {
                return GetGenericableConfig(genericId.c_str()).GetTrust();
            }));
        EXPECT_CALL(*configurationServiceMock_, GetFitableDegradationId(_, _)).WillRepeatedly(
            Invoke([this](const string &genericId, const string &fitableId) -> string {
                FitableConfiguration fitableConfig {};
                GetGenericableConfig(genericId.c_str()).GetFitable(fitableId, fitableConfig);
                return fitableConfig.degradation;
            }));
    }

    void PrepareSystemFitable()
    {
        const char *defaultFitableId = "default";
        GenericableConfiguration systemPropertyGetConfig;
        systemPropertyGetConfig.SetGenericId(fit::sdk::system::getSystemProperty::GENERIC_ID);
        systemPropertyGetConfig.SetDefaultFitableId(defaultFitableId);
        systemPropertyGetConfig.SetTags({"localOnly"});
        SetGenericableConfig(systemPropertyGetConfig);

        RegisterFitable(
            std::function<FitCode(ContextObj, const Fit::string *, Fit::string **)>(
                std::bind(&SystemPropertyGetMock::Invoke, &systemPropertyGetMock_,
                    std::placeholders::_1, std::placeholders::_2, std::placeholders::_3)),
            systemPropertyGetConfig.GetGenericId().c_str(),
            defaultFitableId);

        EXPECT_CALL(systemPropertyGetMock_, Invoke(_, _, NotNull()))
            .WillRepeatedly(Invoke([](ContextObj ctx, const Fit::string *key, Fit::string **result) {
                *result = Context::NewObj<Fit::string>(ctx);
                **result = "value";
                return FIT_OK;
            }));
    }

    template<typename Ret, typename... Args>
    ::Fit::Framework::FunctionProxyType<FitCode> Fitable(Ret(func)(Args...))
    {
        return ::Fit::Framework::Annotation::FitableFunctionWrapper<Ret, Args...>(func).GetProxy();
    }

    template<typename Ret, typename... Args>
    ::Fit::Framework::FunctionProxyType<FitCode> Fitable(std::function<Ret(Args...)> func)
    {
        return ::Fit::Framework::Annotation::FitableFunctionWrapper<Ret, Args...>(func).GetProxy();
    }

private:
    std::shared_ptr<ConfigurationServiceMock> configurationServiceMock_;
    ::Fit::Framework::FitableDiscoveryPtr fitableDiscovery_;
    ::Fit::map<Fit::string, GenericableConfiguration> genericableConfig_;
    ::Fit::BrokerClientPtr oldBrokerClient_;
    ::Fit::Framework::Annotation::FitableDetailReceiver *oldFitableReceiver_ {};
    ::Fit::Framework::FitableDiscoveryPtr oldFitableDiscoveryPtr;
    ::Fit::Configuration::ConfigurationServicePtr oldConfigServicePtr;
    unique_ptr<IBrokerClient> brokerClient_;

    SystemPropertyGetMock systemPropertyGetMock_;
    RuntimeContainerMock runtimeContainerMock_;
    SystemConfigMock* systemConfigMock_ {};
};

RuntimeMock::RuntimeMock() : impl_(make_unique<Impl>()) {}
RuntimeMock::~RuntimeMock() = default;

void RuntimeMock::SetUp()
{
    impl_->SetUp();
}

void RuntimeMock::TearDown()
{
    impl_->TearDown();
}

RuntimeMock &Fit::RuntimeMock::RegisterFitable(::Fit::Framework::Annotation::FitableFunctionProxyType func,
    const char *genericId, const char *fitableId)
{
    impl_->RegisterFitable(func, genericId, fitableId);

    return *this;
}

RuntimeMock &Fit::RuntimeMock::ClearFitable()
{
    impl_->ClearFitable();

    return *this;
}

RuntimeMock &Fit::RuntimeMock::SetGenericableConfig(GenericableConfiguration config)
{
    impl_->SetGenericableConfig(config);

    return *this;
}

GenericableConfiguration &Fit::RuntimeMock::GetGenericableConfig(const char *genericId)
{
    return impl_->GetGenericableConfig(genericId);
}
}