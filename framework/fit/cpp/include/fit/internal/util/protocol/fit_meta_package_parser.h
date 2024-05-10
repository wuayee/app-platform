/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data 报文解析
 * Author       : songyongtan
 * Create       : 2020/11/13 16:14
 * Notes:       :
 */

#ifndef FIT_META_PACKAGE_PARSER_H
#define FIT_META_PACKAGE_PARSER_H

#include <fit/stl/string.hpp>
#include "fit_meta_data.h"

class fit_meta_package_parser final {
public:
    explicit fit_meta_package_parser(const Fit::string &meta);

    explicit fit_meta_package_parser(Fit::string &&meta);

    ~fit_meta_package_parser();

    bool parse_to(fit_meta_data &result);

private:
    inline bool is_valid_raw_data_size() const;

    inline bool is_match_fit_id_length(uint32_t fit_id_length) const;

private:
    const Fit::string meta_raw_data_;
};

#endif // FIT_META_PACKAGE_PARSER_H
