/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-09-06 15:43:06
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
    meta->SetGenericId("838c8f3ea8e149b3bab72bbf5c9d8a4d");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<Fit::string> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    argsOutConverter.push_back(
        ConverterBuilder<Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(
        CreateArgOutBuilder<Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **>::Build());

    FormatterPluginCollector::Register({meta});
}
}
}
}
}