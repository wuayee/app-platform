/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for default SPIs.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#include <support/default_load_balance_spi.hpp>

#include <fit/external/util/string_utils.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/util/vector_utils.hpp>

#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>

using namespace Fit;
using namespace Fit::LoadBalance;
using namespace Fit::Util;
using namespace FitSystemPropertyKey;

const string& DefaultLoadBalanceSpi::GetWorkerId() const
{
    static string workerId;
    if (workerId.empty()) {
        string key = "fit_worker_id";
        workerId = FitSystemPropertyUtils::Get(key);
        if (workerId.empty()) {
            FIT_LOG_WARN("Failed to read id of current worker.");
        }
    }
    return workerId;
}

const string& DefaultLoadBalanceSpi::GetEnvironment() const
{
    static string environment;
    if (environment.empty()) {
        string key = "fit_worker_env_type";
        environment = FitSystemPropertyUtils::Get(key);
        if (environment.empty()) {
            FIT_LOG_WARN("Failed to read environment of load worker.");
        }
    }
    return environment;
}

const vector<int32_t>& DefaultLoadBalanceSpi::GetProtocols() const
{
    static vector<int32_t> protocols {};
    if (protocols.empty()) {
        string protocolArrayString = FitSystemPropertyUtils::Get("fit.broker.client.protocols");
        vector<string> protocolStrings = StringUtils::Split(protocolArrayString, ',');
        vector<int32_t> actualProtocols {};
        actualProtocols.reserve(protocolStrings.size());
        for (auto& protocolString : protocolStrings) {
            int32_t protocol = StringUtils::ToInt32(protocolString);
            int32_t index = VectorUtils::BinarySearch<int32_t>(actualProtocols,
                [&protocol](const int32_t& existing) -> int32_t {
                    return existing - protocol;
                });
            if (index < 0) {
                VectorUtils::Insert(actualProtocols, -1 - index, protocol);
            }
        }
        protocols = actualProtocols;
    }
    return protocols;
}

const vector<string>& DefaultLoadBalanceSpi::GetEnvironmentChain() const
{
    static vector<string> chain {};
    if (chain.empty()) {
        string chainString = FitSystemPropertyUtils::Get("fit_worker_env_callchain");
        vector<string> actualChain = StringUtils::Split(chainString, ',');
        string environment = GetEnvironment();
        if (actualChain.empty() || actualChain[0] != environment) {
            if (!environment.empty()) {
                actualChain.insert(actualChain.begin(), std::move(environment));
            }
        }
        chain = actualChain;
    }
    return chain;
}
