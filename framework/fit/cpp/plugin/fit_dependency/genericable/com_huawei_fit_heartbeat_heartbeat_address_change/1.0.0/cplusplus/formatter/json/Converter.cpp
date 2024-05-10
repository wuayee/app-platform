/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
namespace {
using namespace Fit::Framework::Formatter::Json;
using namespace Fit::Framework::Formatter;
using namespace Fit::Framework;
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::heartbeat::__heartbeatAddressChange, fit::heartbeat::heartbeatAddressChange,
        PROTOCOL_TYPE_JSON>(Annotation::FitableType::MAIN);
}
}