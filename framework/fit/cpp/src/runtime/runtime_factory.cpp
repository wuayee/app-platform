/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/15
 * Notes:       :
 */

#include <fit/internal/runtime/runtime_factory.hpp>
#include <fit/fit_log.h>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <fit/internal/runtime/config/system_config_default.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/util/task_stream.hpp>
#include "configuration_service.h"
#include "config/configuration_client_v2.hpp"
#include "config/key_value_config_service_impl.hpp"
#include "elements/heartbeat_client_element.hpp"
#include "elements/disable_fitables_element.hpp"
#include "elements/broker_server_starter_element.hpp"
#include "elements/registry_client_element.hpp"
#include "elements/tracer_config_element.hpp"
#include "elements/broker_client_starter_element.hpp"
#include "elements/component_starter_element.hpp"
#include "elements/cli_element.hpp"
#include "framework/default_formatter_service.hpp"
#include "default_runtime.hpp"

using namespace ::Fit::Framework;
using ::Fit::Runtime;
using ::Fit::RuntimeElement;
using ::Fit::RuntimeElementBase;
using ::Fit::Config::SystemConfigDefault;

namespace Fit {
using Fit::Plugin::CreatePluginManager;
using Fit::Plugin::PluginManager;
using Framework::CreateFitableDiscovery;
using Framework::FitableDiscovery;
using Framework::Annotation::FitableDetailFlowTo;
using Framework::Annotation::FitableDetailReceiver;
using Framework::Annotation::PopFitableDetailCache;
using namespace Fit::Framework::Formatter;

class RuntimeFactoryDefaultImpl : public RuntimeFactory {
public:
    constexpr static const char *WORKER_CONFIG_FILE = "worker_config.json";

    FitCode Init(const char *runtimeConfigFile, const Fit::map<Fit::string, Fit::string> &options) override
    {
        runtime_ = make_unique<DefaultRuntime>();

        auto defaultRuntime = std::make_shared<DefaultRuntime>();

        FIT_LOG_INFO("Init begin.");
        Fit::string config;
        if (!runtimeConfigFile || runtimeConfigFile[0] == 0) {
            config = WORKER_CONFIG_FILE;
        } else {
            config = runtimeConfigFile;
        }

        runtime_->AddElement(make_unique<SystemConfigDefault>(config, options));
        runtime_->AddElement(make_unique<ComponentStarterElement>());
        runtime_->AddElement(CreateFitableDiscovery());
        runtime_->AddElement(Framework::Formatter::CreateFormatterRepo());
        runtime_->AddElement(make_unique<DefaultFormatterService>());
        runtime_->AddElement(Framework::ParamJsonFormatter::CreateParamJsonFormatterService());
        runtime_->AddElement(make_unique<Fit::Configuration::ConfigurationClientV2>());
        runtime_->AddElement(Fit::Configuration::ConfigurationService::Create());
        runtime_->AddElement(CreateBrokerClient(runtime_.get()));
        runtime_->AddElement(make_unique<BrokerClientStarterElement>());
        runtime_->AddElement(CreatePluginManager());
        runtime_->AddElement(make_unique<HeartbeatClientElement>());
        runtime_->AddElement(make_unique<DisableFitablesElement>());
        runtime_->AddElement(make_unique<BrokerServerStarterElement>());
        runtime_->AddElement(make_unique<RegistryClientElement>());
        runtime_->AddElement(make_unique<TracerConfigElement>());
        runtime_->AddElement(make_unique<CliElement>());
        auto ret = runtime_->Start();
        if (!ret) { // LCOV_EXCL_LINE
            FIT_LOG_ERROR("Start runtime failed.");
            Finit();
            return FIT_ERR_FAIL;
        }

        FIT_LOG_INFO("Init success.");

        return FIT_OK;
    }

    FitCode Finit() override
    {
        runtime_->Stop();
        FIT_LOG_INFO("Exit.");
        return FIT_OK;
    }

    void EnableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) override
    {
        auto registryClient = runtime_->GetElementIs<RegistryClientElement>();
        if (registryClient) { // LCOV_EXCL_LINE
            registryClient->EnableFitables(genericId, fitIds);
        }
    }

    void DisableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) override
    {
        auto registryClient = runtime_->GetElementIs<RegistryClientElement>();
        if (registryClient) { // LCOV_EXCL_LINE
            registryClient->DisableFitables(genericId, fitIds);
        }
    }
private:
    std::unique_ptr<DefaultRuntime> runtime_;
};

RuntimeFactoryPtr CreateRuntimeFactory()
{
    return std::make_shared<RuntimeFactoryDefaultImpl>();
}
} // LCOV_EXCL_LINE