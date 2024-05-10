/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data数据
 * Author       : songyongtan
 * Date         : 2021/5/15
 * Notes:       :
 */

#ifndef FIT_RESPONSE_META_DATA_H
#define FIT_RESPONSE_META_DATA_H

#include <utility>
#include <cstdint>
#include <fit/stl/string.hpp>
#include "tlv/tlv_base.hpp"

class fit_response_meta_data : public Fit::TlvBase {
public:
    fit_response_meta_data() = default;
    fit_response_meta_data(uint16_t version, uint8_t payload_format, uint8_t flag, uint32_t code, Fit::string msg);
    ~fit_response_meta_data() = default;

    uint16_t get_version() const;

    uint8_t get_payload_format() const;

    uint8_t get_flag() const;

    uint32_t get_code() const;

    const Fit::string &get_message() const;

    Fit::string to_bytes() const;
    bool from_bytes(const Fit::string& buffer);

protected:
    static bool is_match_message_size(const Fit::string& buffer, uint32_t message_size);

    static bool is_valid_buffer(const Fit::string& buffer);

private:
    uint16_t version_ {};
    uint8_t payload_format_ {};
    uint8_t flag_ {};
    uint32_t code_ {};
    Fit::string message_ {};
};

#endif // FIT_RESPONSE_META_DATA_H
