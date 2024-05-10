/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-19 21:31:14
 */

#include "fitable_add.hpp"

#include <fit/fit_code.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/util/registration.hpp>

using Fit::Framework::ArgumentsIn;
using Fit::Framework::ArgumentsOut;
using Fit::Framework::ProxyClient;

int32_t AddImpl(void* ctx, int32_t x, int32_t y, FitableAddResult* ret)
{
    ret->result = x + y;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(AddImpl)
        .SetGenericId(Fit::Demo::AddProxyClient::GENERIC_ID)
        .SetFitableId("fit.math.add.test");
}