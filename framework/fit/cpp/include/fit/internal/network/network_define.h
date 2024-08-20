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
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>

namespace Fit {
namespace Network {
struct Request {
    fit_meta_data meta;
    Fit::string payload;
};

struct Response {
    fit_response_meta_data meta;
    Fit::string metadata;
    Fit::string payload;
};

using RequestResponseHandle = std::function<FitCode(const Request &, Response &)>;
}
}


#endif // NETWORKDEFINE_H
