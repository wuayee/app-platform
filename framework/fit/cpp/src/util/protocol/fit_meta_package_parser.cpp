/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data 报文解析
 * Author       : songyongtan
 * Create       : 2020/11/13 16:14
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_meta_package_parser.h>
#include <fit/external/util/string_utils.hpp>
#include <fit/fit_log.h>
#include "fit_meta_header_package_net_util.h"
#include "fit_generic_id_util.h"

fit_meta_package_parser::fit_meta_package_parser(const Fit::string &meta)
    : meta_raw_data_(meta) {}

fit_meta_package_parser::fit_meta_package_parser(Fit::string &&meta)
    : meta_raw_data_(meta) {}

fit_meta_package_parser::~fit_meta_package_parser() = default;

bool fit_meta_package_parser::parse_to(fit_meta_data &result)
{
    if (!is_valid_raw_data_size()) {
        FIT_LOG_ERROR("Invalid meta data, size = %lu, data = %s.", meta_raw_data_.size(),
            ::Fit::StringUtils::ToHexString(meta_raw_data_).c_str());
        return false;
    }

    fit_meta_header_package meta_header_package {};
    meta_raw_data_.copy(reinterpret_cast<char *>(&meta_header_package), sizeof(meta_header_package));
    fit_meta_header_package_net_util::net_to_host(meta_header_package);

    if (!is_match_fit_id_length(meta_header_package.fit_id_length)) {
        FIT_LOG_ERROR("Invalid meta data, not match fit id length, size = %lu, data = %s.", meta_raw_data_.size(),
            ::Fit::StringUtils::ToHexString(meta_raw_data_).c_str());
        return false;
    }
    auto fit_id = meta_raw_data_.substr(sizeof(fit_meta_header_package), meta_header_package.fit_id_length);

    result = fit_meta_data(meta_header_package.version, meta_header_package.payload_format,
        fit_version(meta_header_package.generic_version[MAJOR_INDEX],
            meta_header_package.generic_version[MINOR_INDEX],
            meta_header_package.generic_version[REVISION_INDEX]),
        fit_generic_id_util::hex_to_str(meta_header_package.generic_id,
            sizeof(meta_header_package.generic_id), false), fit_id);

    auto tlvString = meta_raw_data_.substr(sizeof(fit_meta_header_package) + meta_header_package.fit_id_length);
    return result.Deserialize(tlvString) == FIT_OK;
}

bool fit_meta_package_parser::is_valid_raw_data_size() const
{
    return (meta_raw_data_.size() > sizeof(fit_meta_header_package));
}

bool fit_meta_package_parser::is_match_fit_id_length(uint32_t fit_id_length) const
{
    return meta_raw_data_.size() >= sizeof(fit_meta_header_package) + fit_id_length;
}
