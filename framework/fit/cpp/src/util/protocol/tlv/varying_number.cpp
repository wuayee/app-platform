/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/28 9:27
 */
#include <fit/internal/util/protocol/tlv/varying_number.hpp>
#include <fit/stl/stack.hpp>
#include <fit/stl/vector.hpp>

namespace Fit {
namespace VaryNumber {
Fit::string TransToBytes(uint32_t data)
{
    Fit::string res;
    Fit::stack<unsigned char> stack;
    while (data != 0) {
        unsigned char cur = (unsigned char)data & DATA_MASK;
        data = data >> DATA_LEN_IN_BYTE;
        stack.push(cur);
    }

    while (!stack.empty()) {
        auto cur = stack.top();
        stack.pop();
        if (!stack.empty()) {
            cur |= (unsigned char)DATA_FLAG_MASK;
        }
        res.append(1, cur);
    }

    return res;
}

uint32_t BytesToInt(const Fit::string &data)
{
    uint32_t res {0};
    Fit::vector<unsigned char> bs;
    for (size_t i = 0; i < data.size(); i++) {
        auto curData = (unsigned char)data[i];
        res <<= DATA_LEN_IN_BYTE;
        res |= (curData & DATA_MASK);
        if ((curData & DATA_FLAG_MASK) == 0) {
            break;
        }
    }
    return res;
}

const char *BytesToInt(const char *data, uint32_t &size, uint32_t &value)
{
    value = 0;
    size_t i {0};
    for (; i < size; i++) {
        auto curData = (unsigned char)data[i];
        value <<= DATA_LEN_IN_BYTE;
        value |= (curData & DATA_MASK);
        if ((curData & DATA_FLAG_MASK) == 0) {
            break;
        }
    }
    i++;
    size -= i;
    return data + i;
}
}
}
