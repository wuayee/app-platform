/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/28 9:27
 */
#ifndef VARYING_NUMBER_HPP
#define VARYING_NUMBER_HPP

#include <fit/stl/string.hpp>
#include <cstdint>

namespace Fit {
namespace VaryNumber {
constexpr unsigned char DATA_MASK = 0x7F;
constexpr unsigned char DATA_LEN_IN_BYTE = 7;
constexpr unsigned char DATA_FLAG_MASK = ~0x7F;
constexpr uint32_t UINT32_BYTES_MAX_SIZE = 5; // 序列化后最大长度有4个字节

Fit::string TransToBytes(uint32_t data);

uint32_t BytesToInt(const Fit::string &data);
const char *BytesToInt(const char *data, uint32_t &size, uint32_t &value);
}
}


#endif // VARYING_NUMBER_HPP
