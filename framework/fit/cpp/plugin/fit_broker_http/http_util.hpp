/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http util
 * Author       : songyongtan
 * Create       : 2023-07-31
 * Notes:       :
 */

#ifndef FIT_HTTP_UTIL_HPP
#define FIT_HTTP_UTIL_HPP

#include <fit/stl/string.hpp>

namespace Fit {
constexpr int32_t HTTP_STATUS_OK = 200;
constexpr int32_t HTTP_STATUS_BAT_REQUEST = 400;
constexpr int32_t HTTP_STATUS_INTERNAL_ERROR = 500;
constexpr int32_t HTTP_STATUS_UNAUTHORIZED = 401;

constexpr const char* HEADER_FIT_META = "FIT-Metadata";
constexpr const char* HEADER_FIT_VERSION = "FIT-Version";
constexpr const char* HEADER_FIT_DATA_FORMAT = "FIT-Data-Format";
constexpr const char* HEADER_FIT_GENERICABLE_VERSION = "FIT-Genericable-Version";
constexpr const char* HEADER_FIT_TLV = "FIT-TLV";
constexpr const char* HEADER_FIT_CODE = "FIT-Code";
constexpr const char* HEADER_FIT_MESSAGE = "FIT-Message";
constexpr const char* HEADER_FIT_ACCESS_TOKEN = "FIT-Access-Token";
constexpr const char* DEFAULT_GENERICABLE_VERSION = "1.0.0";

constexpr const char* HTTP_CONTENT_TYPE_JSON = "application/json";

constexpr const char* WORKER_CONTEXT_PATH_KEY = "http.context-path";

constexpr const char* HTTP_CONTENT_TYPE_KEY = "Content-Type";
}

#endif