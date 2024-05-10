/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         :
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::hakuna::kernel::registry::server::__QueryAllWorkers,
        fit::hakuna::kernel::registry::server::QueryAllWorkers, PROTOCOL_TYPE_JSON>(Annotation::FitableType::MAIN);
}
}
}
}
}