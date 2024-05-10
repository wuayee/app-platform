/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : 版本 major.minor.revision
 * Author       : songyongtan
 * Create       : 2020/11/13 16:03
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_version.h>
#include <sstream>
#include "external/util/string_utils.hpp"

fit_version::fit_version(uint8_t major, uint8_t minor, uint8_t revision)
    : major_(major), minor_(minor), revision_(revision) {}

uint8_t fit_version::get_major() const
{
    return major_;
}

uint8_t fit_version::get_minor() const
{
    return minor_;
}

uint8_t fit_version::get_revision() const
{
    return revision_;
}

Fit::string fit_version::to_string() const
{
    std::ostringstream result;
    result << (uint32_t)get_major() << '.' << (uint32_t)get_minor() << '.' << (uint32_t)get_revision();
    return Fit::to_fit_string(result.str());
}

fit_version fit_version::parse_from(const Fit::string& s, const fit_version& default_val)
{
    auto ss = Fit::StringUtils::Split(s, '.');
    constexpr int32_t VERSION_SEG_COUNT = 3;
    if (ss.size() != VERSION_SEG_COUNT) {
        return default_val;
    }
    return fit_version(atoi(ss[0].c_str()), atoi(ss[1].c_str()), atoi(ss[2].c_str())); // 2 is revision index
}

bool fit_version::operator==(const fit_version &other) const
{
    return get_major() == other.get_major() && get_minor() == other.get_minor() &&
        get_revision() == other.get_revision();
}