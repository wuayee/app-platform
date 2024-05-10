/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/21 11:03
 * Notes        :
 */

#ifndef BASE64_H
#define BASE64_H

#include <fit/stl/string.hpp>

namespace Fit {
Fit::string Base64Encode(const Fit::bytes &bytes);

Fit::bytes Base64Decode(const Fit::string &buffer);
}

#endif // BASE64_H
