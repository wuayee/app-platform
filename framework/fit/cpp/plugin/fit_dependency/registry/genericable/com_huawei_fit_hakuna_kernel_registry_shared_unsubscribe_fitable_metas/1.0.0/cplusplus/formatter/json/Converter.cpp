/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-09-06 15:47:35
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
    meta->SetGenericId("3a6cc3cb30ea45d49f70681d88601463");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<Fit::string> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());

    FormatterPluginCollector::Register({meta});
}
}
}
}
}