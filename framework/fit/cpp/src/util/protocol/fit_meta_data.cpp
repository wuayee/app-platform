/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : metadata操作数据
 * Author       : songyongtan
 * Create       : 2020/11/13 16:01
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/fit_log.h>

fit_meta_data::fit_meta_data(uint16_t version, uint8_t payload_format, fit_version generic_version,
    const Fit::string &generic_id, const Fit::string &fit_id, const Fit::string &access_token)
    : version_(version),
      payload_format_(payload_format),
      generic_version_(std::move(generic_version)),
      generic_id_(generic_id),
      fit_id_(fit_id),
      access_token_(access_token) {}

fit_meta_data::fit_meta_data()
    : version_(0),
      payload_format_(0),
      generic_version_(0, 0, 0),
      generic_id_(),
      fit_id_(),
      access_token_() {}

uint16_t fit_meta_data::get_version() const
{
    return version_;
}

uint8_t fit_meta_data::get_payload_format() const
{
    return payload_format_;
}

const fit_version &fit_meta_data::get_generic_version() const
{
    return generic_version_;
}

const Fit::string &fit_meta_data::get_generic_id() const
{
    return generic_id_;
}

const Fit::string &fit_meta_data::get_fit_id() const
{
    return fit_id_;
}

const Fit::string &fit_meta_data::get_access_token() const
{
    return access_token_;
}

Fit::string &&fit_meta_data::move_generic_id()
{
    return std::move(generic_id_);
}

Fit::string &&fit_meta_data::move_fit_id()
{
    return std::move(fit_id_);
}

Fit::string &&fit_meta_data::move_access_token()
{
    return std::move(access_token_);
}

fit::hakuna::kernel::broker::shared::MetaData fit_meta_data::convert(const fit_meta_data& data)
{
    fit::hakuna::kernel::broker::shared::MetaData metaData;
    metaData.version = data.get_version();
    metaData.payloadFormat = data.get_payload_format();
    metaData.genericableVersion = data.get_generic_version().to_string();
    metaData.genericableId = data.get_generic_id();
    metaData.fitableId = data.get_fit_id();
    metaData.accessToken = data.get_access_token();
    data.Serialize(metaData.tlv);
    return metaData;
}

fit_meta_data fit_meta_data::from(const fit::hakuna::kernel::broker::shared::MetaData& data)
{
    fit_meta_data metaData = fit_meta_data(data.version, data.payloadFormat,
        fit_version::parse_from(data.genericableVersion, fit_version(atoi("1"), atoi("0"), atoi("0"))),
        data.genericableId, data.fitableId, data.accessToken);
    if (metaData.Deserialize(data.tlv) != FIT_OK) {
        FIT_LOG_ERROR("Deserialize tlv error.");
    }
    return metaData;
}
