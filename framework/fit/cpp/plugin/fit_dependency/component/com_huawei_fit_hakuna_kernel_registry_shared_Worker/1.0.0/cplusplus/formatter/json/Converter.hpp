/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */
#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_WORKER_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_WORKER_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Address/1.0.0/cplusplus/formatter/json/Converter.hpp>
#include "../../Worker.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::Worker &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::Worker &value);
}
}
}
}
#endif