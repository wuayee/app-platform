/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-19 22:17:28
 */

#ifndef FITABLE_ADD_HPP
#define FITABLE_ADD_HPP

#include <cstdint>
#include <fit/external/framework/proxy_client.hpp>

struct FitableAddResult {
    int32_t result;
};

namespace Fit {
namespace Demo {
using AddArgumentsIn = Fit::Framework::ArgumentsIn<int32_t, int32_t>;
using AddArgumentsOut = Fit::Framework::ArgumentsOut<FitableAddResult *>;

class AddProxyClient : public Fit::Framework::ProxyClient<FitCode(AddArgumentsIn, AddArgumentsOut)> {
public:
    static constexpr const char *GENERIC_ID = "fit.math.add";
    AddProxyClient() : ProxyClient<FitCode(AddArgumentsIn, AddArgumentsOut)>(GENERIC_ID) {}
};

}  // namespace Demo
}  // namespace Fit

#endif