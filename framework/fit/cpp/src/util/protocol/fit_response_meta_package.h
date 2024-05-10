/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/5/15
 * Notes:       :
 */

#ifndef FIT_RESPONSE_META_PACKAGE_H
#define FIT_RESPONSE_META_PACKAGE_H

#include <cstdint>

#pragma pack(1)
struct fit_response_meta_header_package {
    uint16_t version; // 协议版本号，默认从1开始
    uint8_t payload_format; // 数据的序列化化协议类型 0-pb, 1-json
    uint8_t flag; // 标记位， 0b00000001 表示允许降级
    uint32_t code; // 错误码 0-成功
    uint32_t message_size; // 错误码对应的描述内容字节数
    char message[0]; // 描述信息。描述信息之后剩余的内容为TLV格式内容
};
#pragma pack()


namespace Fit {
namespace protocol {
    constexpr uint32_t EXPECTED_RESPONSE_META_HEADER_PACKAGE_SIZE = 12;
}
}

static_assert(sizeof(fit_response_meta_header_package) == Fit::protocol::EXPECTED_RESPONSE_META_HEADER_PACKAGE_SIZE,
    "expected fit response meta data header length is 12 bytes.");

#endif // FIT_RESPONSE_META_PACKAGE_H
