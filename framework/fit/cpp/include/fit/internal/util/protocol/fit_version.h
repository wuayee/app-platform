/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : 版本 major.minor.revision
 * Author       : songyongtan
 * Create       : 2020/11/13 16:03
 * Notes:       :
 */

#ifndef FIT_VERSION_H
#define FIT_VERSION_H

#include <fit/stl/string.hpp>

class fit_version {
public:
    fit_version(uint8_t major, uint8_t minor, uint8_t revision);
    ~fit_version() = default;

    bool operator==(const fit_version &other) const;

    uint8_t get_major() const;

    uint8_t get_minor() const;

    uint8_t get_revision() const;

    Fit::string to_string() const;

    static fit_version parse_from(const Fit::string& s, const fit_version& default_val);

private:
    uint8_t major_;
    uint8_t minor_;
    uint8_t revision_;
};

#endif // FIT_VERSION_H
