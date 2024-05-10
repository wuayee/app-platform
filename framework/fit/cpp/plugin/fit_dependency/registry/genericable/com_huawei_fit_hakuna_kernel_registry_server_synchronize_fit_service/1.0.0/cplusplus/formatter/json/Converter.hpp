/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2021-09-14 10:45:38
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SYNCHRONIZEFITSERVICE_G_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SYNCHRONIZEFITSERVICE_G_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/formatter/json/Converter.hpp>
#include "../../synchronizeFitService.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::server::SyncSeviceAddress &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::server::SyncSeviceAddress &value);
}
}
}
}
#endif