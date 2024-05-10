/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  : auto generate by FIT IDL
* Author       : auto
* Date         :
*/

#ifndef ECHO_CONVERTER_HPP
#define ECHO_CONVERTER_HPP

#include <fit/external/framework/formatter/protobuf_converter.hpp>
#include <fit/external/framework/formatter/json_converter.hpp>
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/internal/framework/formatter_service.hpp>

#include "echo.hpp"

namespace Fit {
namespace Benchmark {
template<size_t N>
void RegisterEchoProtobufFormatter()
{
    auto meta = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    meta->SetGenericId(::Fit::Benchmark::Echo<N>::GENERIC_ID);
    meta->SetFormat(0); // protobuf

    Fit::Framework::Formatter::ArgConverterList argsInConverter;
    argsInConverter.push_back(Fit::Framework::Formatter::Protobuf::ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    Fit::Framework::Formatter::ArgConverterList argsOutConverter;
    argsOutConverter.push_back(Fit::Framework::Formatter::Protobuf::ConverterBuilder<Fit::string **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(Fit::Framework::Formatter::Protobuf::CreateArgOutBuilder<Fit::string **>::Build());

    Fit::Framework::Formatter::FormatterPluginCollector::Register({meta});
}

template<size_t N>
void RegisterEchoJsonFormatter()
{
    auto meta = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    meta->SetGenericId(::Fit::Benchmark::Echo<N>::GENERIC_ID);
    meta->SetFormat(0); // protobuf

    Fit::Framework::Formatter::ArgConverterList argsInConverter;
    argsInConverter.push_back(Fit::Framework::Formatter::Json::ConverterBuilder<const Fit::string *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    Fit::Framework::Formatter::ArgConverterList argsOutConverter;
    argsOutConverter.push_back(Fit::Framework::Formatter::Json::ConverterBuilder<Fit::string **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(Fit::Framework::Formatter::Json::CreateArgOutBuilder<Fit::string **>::Build());

    Fit::Framework::Formatter::FormatterPluginCollector::Register({meta});
}
}
}
#endif // ECHO_CONVERTER_HPP
