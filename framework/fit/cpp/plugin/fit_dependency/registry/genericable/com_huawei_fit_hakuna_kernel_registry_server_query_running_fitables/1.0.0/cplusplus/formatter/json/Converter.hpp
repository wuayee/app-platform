/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-04-17 15:03:59
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_RUNNING_FITABLES_G_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_RUNNING_FITABLES_G_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/formatter/json/Converter.hpp>
#include "../../query_running_fitables.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<>
FitCode MessageToJson(ContextObj ctx,
                      const ::fit::hakuna::kernel::registry::server::QueryRunningFitablesParam& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer);

template<>
FitCode MessageToJson(ContextObj ctx,
                      const ::fit::hakuna::kernel::registry::server::RunningFitable& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer);


template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::server::QueryRunningFitablesParam& value);

template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::server::RunningFitable& value);
}
}
}
}
#endif