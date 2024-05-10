/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */
#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_SHARED_FITABLE_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_SHARED_FITABLE_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include "../../Fitable.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::shared::Fitable &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::hakuna::kernel::shared::Fitable &value);
}
}
}
}
#endif