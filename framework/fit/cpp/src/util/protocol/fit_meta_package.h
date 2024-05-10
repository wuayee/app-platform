/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data
 * Author       : songyongtan
 * Create       : 2020/11/13 10:53
 * Notes:       :
 */

#ifndef FIT_META_PACKAGE_H
#define FIT_META_PACKAGE_H

#include <cstdint>

#pragma pack(1)
struct fit_meta_header_package {
    uint16_t version;
    uint8_t payload_format; // 数据的序列化化协议类型
    uint8_t generic_version[3]; // 版本号，major.minor.revision
    uint8_t generic_id[16];   // u.u.i.d十六进制值需要转换为字符串使用
    uint16_t fit_id_length; // fitable id字段的长度
};
#pragma pack()

constexpr uint8_t MAJOR_INDEX = 0;
constexpr uint8_t MINOR_INDEX = 1;
constexpr uint8_t REVISION_INDEX = 2;
constexpr uint32_t EXPECTED_META_HEADER_PACKAGE_SIZE = 24;
static_assert(sizeof(fit_meta_header_package) == EXPECTED_META_HEADER_PACKAGE_SIZE,
    "expected fit meta data header length is 24 bytes.");

#endif // FIT_META_PACKAGE_H
