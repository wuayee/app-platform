/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/12
 * Notes:       :
 */

#ifndef BM_GENERICABLE_ADD_HPP
#define BM_GENERICABLE_ADD_HPP

#include <fit/external/framework/proxy_client.hpp>

namespace Fit {
namespace Benchmark {
struct AddArguments {
    using InType = ::Fit::Framework::ArgumentsIn<const int32_t *, const int32_t *>;
    using OutType = ::Fit::Framework::ArgumentsOut<int32_t **>;
};

class Add : public ::Fit::Framework::ProxyClient<FitCode(AddArguments::InType, AddArguments::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "2461fa5c6ab24650b137bff387a6f958";

    Add() : ::Fit::Framework::ProxyClient<FitCode(AddArguments::InType, AddArguments::OutType)>(GENERIC_ID) {}

    ~Add() = default;
};
}
}
#endif // GENERICABLE_ADD_HPP
