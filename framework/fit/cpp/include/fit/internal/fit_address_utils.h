/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description:
 * Author: s00558940
 * Date: 2020-09-01
 */

#ifndef FIT_ADDRESS_UTILS_H
#define FIT_ADDRESS_UTILS_H

#include <fit/stl/string.hpp>
#include <fit/internal/fit_string_util.h>
#include <fit/external/util/string_utils.hpp>
#include "fit_fitable.h"

class fit_address_utils final {
public:
    // return such as: "127.0.0.1:8080:1"
    static Fit::string convert_to_string(const Fit::fit_address &address)
    {
        return address.ip + ":" +
            Fit::to_string(address.port) +
            ":" +
            Fit::to_string(static_cast<uint32_t>(address.protocol)) +
            ":" +
            address.id;
    }
    static Fit::fit_format_type_set parse_formats(const Fit::string &str)
    {
        Fit::fit_format_type_set result;
        auto string_formats = Fit::StringUtils::Split(str, ',');
        for (const auto &value : string_formats) {
            if (is_number(value)) {
                result.push_back(static_cast<Fit::fit_format_type>(Fit::stoi(value)));
            }
        }

        return result;
    }
};
#endif // FIT_ADDRESS_UTILS_H
