/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/23 16:21
 */
#ifndef ROUTER_HPP
#define ROUTER_HPP

#include <memory>
#include "fit/stl/string.hpp"

namespace Fit {
class Router {
public:
    virtual ~Router() = default;
    virtual Fit::string Route() = 0;
};

using RoutePtr = std::shared_ptr<Router>;
}

#endif