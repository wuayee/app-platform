/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#ifndef FIT_CODE_H
#define FIT_CODE_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif
// 大于0x7F000000的错误码为框架的异常码
//
//    错误码分区
//
// 1、错误码分段处理，后两位为扩展位
//
// 2、0x7F000200 路由异常
//
// 3、0x7F000300 负载均衡异常
//
// 4、0x7F000400 序列化异常
//
// 5、0x7F000500 网络异常

static const int32_t FIT_OK = 0;
static const int32_t FIT_ERR_SUCCESS = 0;
static const int32_t FIT_ERR_PARAM = 0x7F000002;
static const int32_t FIT_ERR_SKIP = 0x7F000003;
static const int32_t FIT_ERR_EXIST = 0x7F000004;
static const int32_t FIT_ERR_NOT_EXIST = 0x7F000005;
static const int32_t FIT_ERR_NOT_SUPPORT = 0x7F000006;
static const int32_t FIT_ERR_TRUST_VALIDATE = 0x7F000008;
static const int32_t FIT_ERR_TRUST_BEFORE = 0x7F000009;
static const int32_t FIT_ERR_TRUST_AFTER = 0x7F00000A;
static const int32_t FIT_NULL_PARAM = 0x7F00000C;
static const int32_t FIT_BAD_ALLOC = 0x7F00000D;
static const int32_t FIT_ERR_PARSE_JSON_FAIL = 0x7F00000E;
static const int32_t FIT_ERR_CTX_BAD_ALLOC = 0x7F00000F;
static const int32_t FIT_ERR_PARSE_LOCAL_ADDRESS_CONF = 0x7F000010;
static const int32_t FIT_ERR_NOT_MATCH = 0x7F000011;

// 一般错误 0x7F000100
#define FIT_ERR_COMMON_BEGIN 0x7F000100
static const int32_t FIT_ERR_FAIL = FIT_ERR_COMMON_BEGIN;
static const int32_t FIT_ERR_NOT_FOUND = FIT_ERR_COMMON_BEGIN + 1;
static const int32_t FIT_ERR_VALUE = FIT_ERR_COMMON_BEGIN + 2;
static const int32_t FIT_ERR_DUPLICATED_FITABLE_ID = FIT_ERR_COMMON_BEGIN + 3;
static const int32_t FIT_ERR_NOT_READY = FIT_ERR_COMMON_BEGIN + 4;
static const int32_t FIT_ERR_EXCEPTION_FROM_USER = FIT_ERR_COMMON_BEGIN + 5;
static const int32_t FIT_ERR_FILTER_TARGET = FIT_ERR_COMMON_BEGIN + 6;

// 路由异常 0x7F000200
static const int32_t FIT_ERR_NULL_FITABLE_PROXY = 0x7F000201;
static const int32_t FIT_ERR_NULL_DISCOVERY = 0x7F000202;
static const int32_t FIT_ERR_FITABLE_NOT_FOUND = 0x7F000203;
static const int32_t FIT_ERR_RULE_ROUTE = 0x7F000204;
static const int32_t FIT_ERR_ROUTE = 0x7F000205;

// 负载均衡异常 0x7F000300
static const int32_t FIT_ERR_FITABLE_NO_INSTANCE = 0x7F000301;

// 序列化异常 0x7F000400
#define FIT_ERR_SERIALIZE_BEGIN 0x7F000400
static const int32_t FIT_ERR_SERIALIZE = FIT_ERR_SERIALIZE_BEGIN;
static const int32_t FIT_ERR_SERIALIZE_PB = FIT_ERR_SERIALIZE_BEGIN + 1;
static const int32_t FIT_ERR_DESERIALIZE_PB = FIT_ERR_SERIALIZE_BEGIN + 2;
static const int32_t FIT_ERR_SERIALIZE_JSON = FIT_ERR_SERIALIZE_BEGIN + 3;
static const int32_t FIT_ERR_DESERIALIZE_JSON = FIT_ERR_SERIALIZE_BEGIN + 4;
static const int32_t FIT_ERR_SERIALIZE_TYPE_NOT_SUPPORTED = FIT_ERR_SERIALIZE_BEGIN + 5;
static const int32_t FIT_ERR_SERIALIZE_PROTOCOL_NOT_SUPPORTED = FIT_ERR_SERIALIZE_BEGIN + 6;
static const int32_t FIT_ERR_DESERIALIZE = 0x7F000410;

// 网络错误 0x7F000500
#define FIT_ERR_NET_BEGIN 0x7F000500
static const int32_t FIT_ERR_NET_TIMEOUT = FIT_ERR_NET_BEGIN + 1;
static const int32_t FIT_ERR_NET_CONNECT_FAIL = FIT_ERR_NET_BEGIN + 2;
static const int32_t FIT_ERR_NET_SEND_FAIL = FIT_ERR_NET_BEGIN + 3;
static const int32_t FIT_ERR_NET_RECEIVE_FAIL = FIT_ERR_NET_BEGIN + 4;
static const int32_t FIT_ERR_NET_INVALID_REQUEST_METADATA = FIT_ERR_NET_BEGIN + 5;
static const int32_t FIT_ERR_NET_INVALID_RESPONSE_METADATA = FIT_ERR_NET_BEGIN + 6;
static const int32_t FIT_ERR_NET_NO_RESPONSE = FIT_ERR_NET_BEGIN + 3;
static const int32_t FIT_ERR_NET_NO_RESPONSE_METADATA = FIT_ERR_NET_BEGIN + 5;
static const int32_t FIT_ERR_NET_INTERNAL_FAULT = FIT_ERR_NET_BEGIN + 6;
static const int32_t FIT_ERR_NET_NO_REQUEST_METADATA = FIT_ERR_NET_BEGIN + 7;
static const int32_t FIT_ERR_NET_END = 0x7F0005FF;

// 认证鉴权错误 0x7F000600
#define FIT_ERR_AUTHENTICATION_BEGIN 0x7F000600
static const int32_t FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN = FIT_ERR_AUTHENTICATION_BEGIN + 1;
static const int32_t FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN = FIT_ERR_AUTHENTICATION_BEGIN + 2;
static const int32_t FIT_ERR_AUTHENTICATION_ROLE_NO_PERMISSION = FIT_ERR_AUTHENTICATION_BEGIN + 3;

typedef int32_t FitCode;

inline static bool FitSuccess(FitCode code)
{
    return code == FIT_ERR_SUCCESS;
}

#ifdef __cplusplus
}
#endif

#endif  // FITCODE_H
