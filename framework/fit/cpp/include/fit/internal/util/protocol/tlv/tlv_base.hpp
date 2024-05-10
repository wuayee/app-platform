/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/28 21:05
 */
#ifndef TLV_BASE_HPP
#define TLV_BASE_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/fit_code.h>

namespace Fit {
class TlvBase {
public:
    ~TlvBase() = default;

    // 设置一组tlv的值
    void SetTagValue(uint32_t tag, const Fit::string &value);
    uint32_t GetTagValueLen() const;
    const Fit::map<uint32_t, Fit::string> &GetTlv() const;
    Fit::string GetValueByTag(uint32_t tag) const;

    FitCode Serialize(Fit::string &result) const;
    FitCode Deserialize(const Fit::string &rawData);

private:
    Fit::map<uint32_t, Fit::string> tlv_;
    uint32_t tagValueLen_ {0};
};
}

#endif // TLV_BASE_HPP
