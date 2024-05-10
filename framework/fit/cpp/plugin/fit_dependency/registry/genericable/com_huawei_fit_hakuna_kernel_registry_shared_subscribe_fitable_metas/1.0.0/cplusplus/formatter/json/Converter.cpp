/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-09-06 15:44:47
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
    meta->SetGenericId("302b69af338c4fb585c1fac4ec6f1adc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<Fit::string> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
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