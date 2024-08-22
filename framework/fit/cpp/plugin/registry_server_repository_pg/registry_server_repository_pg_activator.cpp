/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : activator for registry server repository in pgsql
 * Author       : x00649642
 * Create       : 2023-11-21
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_ACTIVATOR_H
#define REGISTRY_SERVER_REPOSITORY_PG_ACTIVATOR_H

#include <array>

#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/external/util/string_utils.hpp>

#include <fit/internal/runtime/crypto/crypto_manager.hpp>
#include "src/connection_pool.hpp"
#include "src/sql_wrapper/sql_connection.hpp"

using namespace Fit;

namespace {
namespace ConfigFields {
struct Field {
    constexpr Field(const char* configKey, const char* pgName) : configKey(configKey), pgName(pgName)
    {
    }

    const char* configKey;
    const char* pgName;
};

constexpr Field PG_HOST{"pg.host", "host"};
constexpr Field PG_HOSTADDR{"pg.host_addr", "hostaddr"};
constexpr Field PG_PORT{"pg.port", "port"};
constexpr Field PG_NAME{"pg.db_name", "dbname"};
constexpr Field PG_USER{"pg.username", "user"};
constexpr Field PG_CODE{"pg.password", "password"};
constexpr Field PG_SSLMODE{"pg.sslmode", "sslmode"}; // ssl mode选项，disable 不关心安全，require采用ssl连接
constexpr size_t PG_FIELD_CNT = 7;
std::array<Field, PG_FIELD_CNT> FIELDS = {PG_HOST, PG_HOSTADDR, PG_PORT, PG_NAME, PG_USER, PG_CODE, PG_SSLMODE};

static constexpr const char* PG_CONNECTION_NUM{"pg.connections.num"};
static constexpr const char* PG_CONNECTION_MAX_RETRY{"pg.connections.max_retry"};
static constexpr const char* PG_PASSWORD_CRYPTO_TYPE{"pg.password-crypto-type"};
}  // namespace ConfigFields

FitCode Start(::Fit::Framework::PluginContext* context)
{
    auto config = context->GetConfig();
    Fit::vector<Fit::string> configStrVec;
    string cryptoType = config->Get(ConfigFields::PG_PASSWORD_CRYPTO_TYPE).AsString("");
    for (const auto& field : ConfigFields::FIELDS) {
        auto jsonValue = config->Get(field.configKey).AsString("");
        if (!cryptoType.empty() && field.configKey == ConfigFields::PG_CODE.configKey) {
            auto Crypto = CryptoManager::Instance().Get(cryptoType);
            if (Crypto != nullptr) {
                Crypto->Decrypt(jsonValue.c_str(), jsonValue.length(), jsonValue);
            }
            FIT_LOG_INFO("Decrypt password type is %s.", cryptoType.c_str());
        }
        if (jsonValue != "") {
            configStrVec.push_back(StringUtils::Format("%s=%s", field.pgName, jsonValue.c_str()));
        }
        if (field.configKey == ConfigFields::PG_CODE.configKey) {
            jsonValue.clear();
        }
        FIT_LOG_INFO("Config key:value (%s:%s).", field.pgName, jsonValue.c_str());
    }
    auto configStr = StringUtils::Join(' ', configStrVec);
    auto connectionNum = config->Get(ConfigFields::PG_CONNECTION_NUM).AsInt(1);
    auto maxRetry = config->Get(ConfigFields::PG_CONNECTION_MAX_RETRY).AsInt(1);
    FIT_LOG_INFO("Activator Start with (conn num: %d, max retry: %d).", connectionNum, maxRetry);
    return Repository::Pg::ConnectionPool::Instance().SetUp(configStr, connectionNum, maxRetry,
        [](const char* connectionInfo) { return make_unique<Fit::Repository::Pg::SqlConnection>(connectionInfo); });
}

FitCode Stop()
{
    FIT_LOG_INFO("Activator Stop.");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar().SetStart(Start).SetStop(Stop);
}
}  // namespace
#endif  // REGISTRY_SERVER_REPOSITORY_PG_ACTIVATOR_H
