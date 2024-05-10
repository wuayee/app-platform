/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/26 17:10
 */
#ifndef MULTIPLEXINVOKER_HPP
#define MULTIPLEXINVOKER_HPP

#include <functional>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/any.hpp>
#include <fit/stl/vector.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/framework/proxy_define.hpp>

#include <fit/fit_code.h>

namespace Fit {
struct RouteFilterParam {
    Fit::string fitableId;
    Fit::string alias;
};

struct LBFilterParam {
    Fit::string host;
    uint32_t port;
    uint32_t protocol;
    Fit::string workerId;
};

struct CallBackInfo {
    FitCode code;
    Fit::string genericId;
    Fit::string fitableId;
    Fit::vector<Fit::string> aliases;
    Fit::string host;
    uint32_t port;
    Fit::string workerId;
};

using CallBack = std::function<void(const CallBackInfo &info, const Fit::Framework::Arguments &out)>;

class MultiplexInvoker {
public:
    using RouteFilter = std::function<bool(const RouteFilterParam &param)>;
    using LBFilter = std::function<bool(const LBFilterParam &param)>;
    virtual ~MultiplexInvoker() = default;

    virtual MultiplexInvoker &Route(RouteFilter filter) = 0;
    virtual MultiplexInvoker &Get(LBFilter filter) = 0;
    virtual FitCode Exec(ContextObj ctx,
        Fit::Framework::Arguments &in,
        Fit::Framework::Arguments &out,
        CallBack cb) = 0;
};

using MultiInvokerPtr = Fit::unique_ptr<MultiplexInvoker>;
MultiInvokerPtr CreateMultiInvoker(const Fit::string &genericID);
}

#endif // MULTIPLEXINVOKER_HPP
