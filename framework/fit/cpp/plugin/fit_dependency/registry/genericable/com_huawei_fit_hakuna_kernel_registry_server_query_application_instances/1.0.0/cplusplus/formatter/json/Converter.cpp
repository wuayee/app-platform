/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-09-06 15:20:10
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
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("fbc49b115f784676b44e5fbdb664b0cc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(
        ConverterBuilder<const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    argsOutConverter.push_back(
        ConverterBuilder<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(
        CreateArgOutBuilder<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **>::Build());

    FormatterPluginCollector::Register({meta});
}
}
}
}
}