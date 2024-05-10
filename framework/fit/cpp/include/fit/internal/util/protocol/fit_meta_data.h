/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data数据
 * Author       : songyongtan
 * Create       : 2020/11/13 16:01
 * Notes:       :
 */

#ifndef FIT_META_DATA_H
#define FIT_META_DATA_H

#include <utility>

#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include "tlv/tlv_base.hpp"
#include "fit_version.h"

namespace fit_meta_defines {
constexpr uint16_t META_VERSION_NO_RESPONSE_META = 1;
constexpr uint16_t META_VERSION_HAS_RESPONSE_META = 2;
constexpr uint8_t FORMAT_PROTOBUF = 0;
}

class fit_meta_data : public Fit::TlvBase {
public:
    fit_meta_data(uint16_t version,
        uint8_t payload_format,
        fit_version generic_version,
        const Fit::string &generic_id,
        const Fit::string &fit_id);

    fit_meta_data();
    ~fit_meta_data() = default;

    uint16_t get_version() const;

    uint8_t get_payload_format() const;

    const fit_version &get_generic_version() const;

    const Fit::string &get_generic_id() const;

    const Fit::string &get_fit_id() const;

    Fit::string &&move_generic_id();
    Fit::string &&move_fit_id();

private:
    uint16_t version_;
    uint8_t payload_format_;
    fit_version generic_version_;
    Fit::string generic_id_;
    Fit::string fit_id_;
};

#endif // FIT_META_DATA_H
