/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  : auto generate by FIT IDL
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
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("2bd87d1847e94811aec5054f374792dc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    argsOutConverter.push_back(ConverterBuilder<Fit::vector<::fit::registry::Address> **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<Fit::vector<::fit::registry::Address> **>::Build());

    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("2bd87d1847e94811aec5054f374792dc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::VALIDATE);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("2bd87d1847e94811aec5054f374792dc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::BEFORE);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("2bd87d1847e94811aec5054f374792dc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::AFTER);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("2bd87d1847e94811aec5054f374792dc");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::ERROR);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
}
}
}
}