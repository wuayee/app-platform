/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : metadata操作数据
 * Author       : songyongtan
 * Create       : 2020/11/13 16:01
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_meta_data.h>

fit_meta_data::fit_meta_data(uint16_t version, uint8_t payload_format, fit_version generic_version,
    const Fit::string &generic_id, const Fit::string &fit_id)
    : version_(version),
      payload_format_(payload_format),
      generic_version_(std::move(generic_version)),
      generic_id_(generic_id),
      fit_id_(fit_id) {}

fit_meta_data::fit_meta_data()
    : version_(0),
      payload_format_(0),
      generic_version_(0, 0, 0),
      generic_id_(),
      fit_id_() {}

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

Fit::string &&fit_meta_data::move_generic_id()
{
    return std::move(generic_id_);
}

Fit::string &&fit_meta_data::move_fit_id()
{
    return std::move(fit_id_);
}

