/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#include <fit/internal/framework/formatter_service.hpp>
#include <fit/internal/framework/formatter/formatter_collector_inner.hpp>
#include "default_formatter_service.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
FormatterServicePtr CreateFormatterService()
{
    return std::make_shared<DefaultFormatterService>();
}

void FinitFormatterService()
{
    delete FormatterMetaFlowTo(nullptr);
}
}
}
} // LCOV_EXCL_LINE