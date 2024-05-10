/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : crypto for scc
 * Author       : w00561424
 * Create       : 2024-03-18
 * Notes:       :
 */

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <fit/runtime/crypto/crypto_collector.hpp>
#include "scc_client.h"
#include "scc_crypto.h"
using namespace Fit;
namespace {
FitCode Start(::Fit::Framework::PluginContext* context)
{
    auto config = context->GetConfig();
    string sccConfFilePath = config->Get("scc-crypto.config-file").AsString("");
    Fit::shared_ptr<Fit::SccClient> client = SccClient::Create(sccConfFilePath);
    if (client->Init() != FIT_OK) {
        FIT_LOG_ERROR("Failed to init scc client. config file=%s.", sccConfFilePath.c_str());
        return FIT_ERR_FAIL;
    }
    CryptoRegister(make_shared<Fit::SccCrypto>("scc", client));
    FIT_LOG_INFO("Start scc crypto, config file is %s.", sccConfFilePath.c_str());
    return FIT_OK;
}

FitCode Stop()
{
    CryptoUnregister("scc");
    FIT_LOG_INFO("Stop scc crypto.");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar().SetStart(Start).SetStop(Stop);
}
} // LCOV_EXCL_LINE