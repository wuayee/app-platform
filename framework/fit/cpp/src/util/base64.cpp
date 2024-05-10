/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/7/26 16:12
 * Notes        :
 */

#include <fit/external/util/base64.h>

namespace Fit {
constexpr const char* encodeTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
Fit::string Base64Encode(const Fit::bytes &bytes)
{
    Fit::string strEncode;
    size_t encodeSize = bytes.size() / 3 * 4; // 3,4
    if (bytes.size() % 3 != 0) { // 3
        encodeSize += 4; // 4
    }
    strEncode.reserve(encodeSize);
    auto data = reinterpret_cast<const unsigned char *>(bytes.data());
    auto size = bytes.size();
    for (size_t i = 0; i < size / 3 * 3; i += 3) { // 3
        strEncode += encodeTable[data[0] >> 2]; // 2
        strEncode += encodeTable[((data[0] << 4) | (data[1] >> 4)) & 0x3F]; // 4
        strEncode += encodeTable[((data[1] << 2) | (data[2] >> 6)) & 0x3F]; // 2,6
        strEncode += encodeTable[data[2] & 0x3F]; // 2
        data += 3; // 3
    }
    // 对剩余数据进行编码
    size_t mod = size % 3; // 3
    if (mod == 1) {
        strEncode += encodeTable[data[0] >> 2]; // 2
        strEncode += encodeTable[(data[0] << 4) & 0x30]; // 4
        strEncode += "==";
    } else if (mod == 2) { // 2
        strEncode += encodeTable[data[0] >> 2]; // 2
        strEncode += encodeTable[((data[0] << 4) | (data[1] >> 4)) & 0x3F]; // 4
        strEncode += encodeTable[(data[1] << 2) & 0x3F]; // 2
        strEncode += "=";
    }

    return strEncode;
}

Fit::bytes Base64Decode(const Fit::string &buffer)
{
    // 解码表
    const char decodeTable[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0,
        62,  // '+'
        0, 0, 0,
        63,                                      // '/'
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61,  // '0'-'9'
        0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
        25,  // 'A'-'Z'
        0, 0, 0, 0, 0, 0, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51,  // 'a'-'z'
    };
    string result;
    result.reserve(buffer.size() * 3 / 4); // 3,4
    uint32_t value;
    size_t i = 0;
    const char *data = buffer.data();
    while (i < buffer.size()) {
        value = static_cast<uint32_t>(decodeTable[(size_t)*data++]) << 18; // 18
        value += static_cast<uint32_t>(decodeTable[(size_t)*data++]) << 12; // 12
        result += (value & 0x00FF0000) >> 16; // 16
        if (*data != '=') {
            value += static_cast<uint32_t>(decodeTable[(size_t)*data++]) << 6; // 6
            result += (value & 0x0000FF00) >> 8; // 8
            if (*data != '=') {
                value += decodeTable[(size_t)*data++];
                result += value & 0x000000FF;
            }
        }
        i += 4; // 4
    }
    return result;
}
}
