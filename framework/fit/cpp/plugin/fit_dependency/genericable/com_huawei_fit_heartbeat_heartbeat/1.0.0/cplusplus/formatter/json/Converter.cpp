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
    meta->SetGenericId("e12fd1c57fd84f50a673d93d13074082");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<::fit::heartbeat::BeatInfo> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const ::fit::registry::Address *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    argsOutConverter.push_back(ConverterBuilder<bool **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<bool **>::Build());

    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("e12fd1c57fd84f50a673d93d13074082");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::VALIDATE);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<::fit::heartbeat::BeatInfo> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const ::fit::registry::Address *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("e12fd1c57fd84f50a673d93d13074082");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::BEFORE);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<::fit::heartbeat::BeatInfo> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const ::fit::registry::Address *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("e12fd1c57fd84f50a673d93d13074082");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::AFTER);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<::fit::heartbeat::BeatInfo> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const ::fit::registry::Address *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("e12fd1c57fd84f50a673d93d13074082");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json
    meta->SetFitableType(Fit::Framework::Annotation::FitableType::ERROR);

    ArgConverterList argsInConverter;
    argsInConverter.push_back(ConverterBuilder<const Fit::vector<::fit::heartbeat::BeatInfo> *>::Build());
    argsInConverter.push_back(ConverterBuilder<const ::fit::registry::Address *>::Build());
    meta->SetArgsInConverter(argsInConverter);
    meta->SetCreateArgsOut(CreateArgOutBuilder<void>::Build());
    FormatterPluginCollector::Register({meta});
}
}
}
}
}