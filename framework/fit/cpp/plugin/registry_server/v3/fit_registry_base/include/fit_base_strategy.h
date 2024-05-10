/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide base strategy interface.
 * Author       : w00561424
 * Date:        : 2023/10/17
 */

#ifndef FIT_REGISTRY_BASE_STRATEGY_H
#define FIT_REGISTRY_BASE_STRATEGY_H
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/memory.hpp>

namespace Fit {
namespace Registry {
class FitBaseStrategy {
public:
    virtual ~FitBaseStrategy() = default;
    virtual Fit::string Type() = 0;
    virtual int32_t Check(const Fit::map<Fit::string, Fit::string>& kvs) = 0;
};

using FitBaseStrategyPtr = Fit::shared_ptr<FitBaseStrategy>;
}
}
#endif
