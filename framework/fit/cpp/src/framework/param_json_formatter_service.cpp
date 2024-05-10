/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/24 15:52
 */

#include <fit/internal/framework/param_json_formatter_service.hpp>
#include "default_param_json_formatter_service.hpp"

namespace Fit {
namespace Framework {
namespace ParamJsonFormatter {
unique_ptr<ParamJsonFormatterService> CreateParamJsonFormatterService()
{
    return make_unique<DefaultParamJsonFormatterService>();
}
}
}
} // LCOV_EXCL_LINE