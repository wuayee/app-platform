/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/24 15:48
 */

#include <fit/internal/framework/formatter_repo.hpp>
#include "default_formatter_repo.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
unique_ptr<FormatterRepo> CreateFormatterRepo()
{
    return make_unique<DefaultFormatterRepo>();
}
}
}
} // LCOV_EXCL_LINE