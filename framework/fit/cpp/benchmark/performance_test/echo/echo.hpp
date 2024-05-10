/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/8/9 11:19
 * Notes        :
 */

#ifndef ECHO_H
#define ECHO_H

#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/string.hpp>

namespace Fit {
namespace Benchmark {
namespace __Echo {
using InType = ::Fit::Framework::ArgumentsIn<::Fit::string *>;
using OutType = ::Fit::Framework::ArgumentsOut<::Fit::string **>;
}

template<size_t N>
class Echo : public ::Fit::Framework::ProxyClient<FitCode(__Echo::InType, __Echo::OutType)> {
public:
    static constexpr const char* GENERIC_ID_TAIL = "3e6ee232ebcd430d9bd1d47b2c49dcfd";
    static constexpr const char* FITABLE_ID_TAIL = "0c4e0db2a1cb4558a978309bbb451751";
    static const Fit::string GENERIC_ID_STR;
    static const char *GENERIC_ID;

    Echo() : ::Fit::Framework::ProxyClient<FitCode(__Echo::InType, __Echo::OutType)>(GENERIC_ID) {}

    ~Echo() = default;
};

template<size_t N>
const Fit::string Echo<N>::GENERIC_ID_STR = std::to_string(N) + GENERIC_ID_TAIL;

template<size_t N>
const char* Echo<N>::GENERIC_ID = GENERIC_ID_STR.c_str();
}
}

#endif // ECHO_H
