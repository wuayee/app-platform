/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/19 15:56
 */
#ifndef NETWORK_DEFINE_H
#define NETWORK_DEFINE_H

#include <fit/stl/string.hpp>
#include <functional>
#include <fit/fit_code.h>

namespace Fit {
namespace Network {
struct Request {
    Fit::string metadata;
    Fit::string payload;
};

using Response = Request;

using RequestResponseHandle = std::function<FitCode(const Request &, Response &)>;
}
}


#endif // NETWORKDEFINE_H
