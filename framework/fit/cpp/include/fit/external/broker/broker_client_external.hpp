/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef IBROKER_CLIENT_EXTERNAL_HPP
#define IBROKER_CLIENT_EXTERNAL_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/any.hpp>
#include <fit/stl/vector.hpp>
#include <fit/external/util/context/context_api.hpp>
namespace Fit {
int32_t GenericableInvoke(ContextObj context, const Fit::string& genericableId,
    Fit::vector<Fit::any>& in, Fit::vector<Fit::any>& out);
}
#endif