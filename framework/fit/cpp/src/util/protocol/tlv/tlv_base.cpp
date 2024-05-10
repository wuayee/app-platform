/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/28 21:25
 */

#include <fit/internal/util/protocol/tlv/tlv_base.hpp>
#include <fit/internal/util/protocol/tlv/varying_number.hpp>

namespace Fit {
namespace {
constexpr uint32_t MAX_INT32_BYTE_SIZE = 5;
}

void TlvBase::SetTagValue(uint32_t tag, const Fit::string &value)
{
    tagValueLen_ += static_cast<uint32_t>(value.size()) + MAX_INT32_BYTE_SIZE;
    tlv_[tag] = value;
}

uint32_t TlvBase::GetTagValueLen() const
{
    return tagValueLen_;
}

const Fit::map<uint32_t, Fit::string> &TlvBase::GetTlv() const
{
    return tlv_;
}

Fit::string TlvBase::GetValueByTag(uint32_t tag) const
{
    auto it = tlv_.find(tag);
    if (it != tlv_.end()) {
        return it->second;
    }
    return "";
}

FitCode TlvBase::Serialize(Fit::string &result) const
{
    const auto &tlv = GetTlv();
    for (const auto &item : tlv) {
        result.append(Fit::VaryNumber::TransToBytes(item.first));
        result.append(Fit::VaryNumber::TransToBytes(item.second.size()));
        result.append(item.second);
    }
    return FIT_OK;
}

FitCode TlvBase::Deserialize(const Fit::string &rawData)
{
    uint32_t tag {0};
    uint32_t length {0};
    auto tlvData = rawData.c_str();
    uint32_t tlvDataLen = rawData.size();

    while (tlvDataLen > 0) {
        tlvData = Fit::VaryNumber::BytesToInt(tlvData, tlvDataLen, tag);
        tlvData = Fit::VaryNumber::BytesToInt(tlvData, tlvDataLen, length);
        if (length > tlvDataLen) {
            return FIT_ERR_DESERIALIZE;
        }
        SetTagValue(tag, Fit::string(tlvData, length));
        tlvData += length;
        tlvDataLen -= length;
    }
    return FIT_OK;
}
}
