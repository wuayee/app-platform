/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : metadata操作数据
 * Author       : songyongtan
 * Date         : 2021/5/15
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/fit_log.h>
#include <fit/external/util/string_utils.hpp>
#include "fit_meta_header_package_net_util.h"

fit_response_meta_data::fit_response_meta_data(uint16_t version, uint8_t payload_format, uint8_t flag,
    uint32_t code, Fit::string msg)
    : version_(version),
      payload_format_(payload_format),
      flag_(flag),
      code_(code),
      message_(std::move(msg)) {}

uint16_t fit_response_meta_data::get_version() const
{
    return version_;
}

uint8_t fit_response_meta_data::get_payload_format() const
{
    return payload_format_;
}

uint8_t fit_response_meta_data::get_flag() const
{
    return flag_;
}

uint32_t fit_response_meta_data::get_code() const
{
    return code_;
}

const Fit::string &fit_response_meta_data::get_message() const
{
    return message_;
}

bool fit_response_meta_data::from_bytes(const Fit::string &buffer)
{
    if (!is_valid_buffer(buffer)) {
        FIT_LOG_ERROR("Invalid response meta data, size = %lu, data = %s.", buffer.size(),
            ::Fit::StringUtils::ToHexString(buffer).c_str());
        return false;
    }
    fit_response_meta_header_package package {};
    buffer.copy((char *)&package, sizeof(fit_response_meta_header_package));
    fit_response_meta_header_package_net_util::net_to_host(package);

    if (!is_match_message_size(buffer, package.message_size)) {
        FIT_LOG_ERROR("Invalid meta data, not match fit id length, size = %lu, data = %s.", buffer.size(),
            ::Fit::StringUtils::ToHexString(buffer).c_str());
        return false;
    }

    version_ = package.version;
    payload_format_ = package.payload_format;
    flag_ = package.flag;
    code_ = package.code;
    message_ = buffer.substr(sizeof(fit_response_meta_header_package), package.message_size);

    auto ret = Deserialize(buffer.substr(sizeof(fit_response_meta_header_package) + package.message_size));
    return ret == FIT_OK;
}

Fit::string fit_response_meta_data::to_bytes() const
{
    fit_response_meta_header_package package {};
    package.version = get_version();
    package.payload_format = get_payload_format();
    package.flag = get_flag();
    package.code = get_code();
    package.message_size = get_message().size();

    Fit::string result;
    result.reserve(sizeof(fit_response_meta_header_package) + package.message_size + GetTagValueLen());

    fit_response_meta_header_package_net_util::host_to_net(package);
    result.assign((const char *)&package, sizeof(fit_response_meta_header_package));
    result.append(get_message().data(), get_message().size());

    Serialize(result);
    return result;
}

bool fit_response_meta_data::is_match_message_size(const Fit::string &buffer, uint32_t message_size)
{
    return buffer.size() >= sizeof(fit_response_meta_header_package) + message_size;
}

bool fit_response_meta_data::is_valid_buffer(const Fit::string &buffer)
{
    return buffer.size() >= sizeof(fit_response_meta_header_package);
}
