/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2022-01-12 19:35:24
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_BROKER_CLIENT_GENERICINVOKEV3_G_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_BROKER_CLIENT_GENERICINVOKEV3_G_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include "../../genericInvokeV3.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::broker::client::GenericInvokeOptions &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::broker::client::GenericInvokeOptions &value);
}
}
}
}
#endif