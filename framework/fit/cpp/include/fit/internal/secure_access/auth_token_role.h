/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef SECURE_ACCESS_AUTH_TOKEN_H
#define SECURE_ACCESS_AUTH_TOKEN_H
#include <fit/stl/string.hpp>
#include <fit/internal/fit_time_utils.h>
#include <fit/internal/util/fit_uuid.hpp>
#include <fit/internal/registry/repository/util_by_repo.h>
#include <fit/fit_log.h>
namespace Fit {
constexpr const char *FRESH_TOKEN_TYPE = "refresh_token";
constexpr const char *ACCESS_TOKEN_TYPE = "access_token";
constexpr const uint64_t DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS = 60 * 60; // 默认访问令牌超时时间 60 min
constexpr const uint64_t DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS = 24 * 60 * 60; // 默认刷新令牌超时时间 24 h
constexpr const uint64_t SECOND_TO_MILLION_SECOND = 1000; // 1s 等于 1000 ms

struct AuthTokenRole {
public:
    AuthTokenRole() = default;
    AuthTokenRole(const string& tokenIn, const string& typeIn, uint64_t timeoutIn, time_t endTimeIn,
        const string& roleIn) : token(tokenIn), type(typeIn), timeout(timeoutIn), endTime(endTimeIn), role(roleIn) {}
    AuthTokenRole(const AuthTokenRole& tokenRole) : token(tokenRole.token), type(tokenRole.type),
        timeout(tokenRole.timeout), endTime(tokenRole.endTime), role(tokenRole.role) {}
    AuthTokenRole& operator=(const AuthTokenRole& tokenRole)
    {
        if (this == &tokenRole) {
            return *this;
        }
        this->token = tokenRole.token;
        this->type = tokenRole.type;
        this->timeout = tokenRole.timeout;
        this->endTime = tokenRole.endTime;
        this->role = tokenRole.role;
        return *this;
    }

    bool operator==(const AuthTokenRole& tokenRole) const
    {
        return (this->token == tokenRole.token) && (this->type == tokenRole.type) &&
            (this->timeout == tokenRole.timeout) && (this->endTime == tokenRole.endTime) &&
            (this->role == tokenRole.role);
    }

    bool IsTimeout(time_t curTime) const
    {
        return curTime >= this->endTime;
    }
public:
    string token;
    string type;
    uint64_t timeout;
    time_t endTime;
    string role;
};

static AuthTokenRole CreateTokenRole(const string& typeIn, const string& role, uint64_t expire, time_t curTime)
{
    Fit::string uuid;
    int32_t ret = UtilByRepo::Instance().GetUUid(uuid);
    if (ret != FIT_OK) {
        FIT_LOG_WARN("Generate uuid from pg failed, error: %d.", ret);
        uuid = GenerateUuid();
    }
    return AuthTokenRole(uuid, typeIn, expire, curTime + expire * SECOND_TO_MILLION_SECOND, role);
}
}
#endif