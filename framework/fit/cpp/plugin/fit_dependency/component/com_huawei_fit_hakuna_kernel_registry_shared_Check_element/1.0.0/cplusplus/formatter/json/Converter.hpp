/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-10-17 11:32:13
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECKELEMENT_C_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECKELEMENT_C_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include "../../CheckElement.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<>
FitCode MessageToJson(ContextObj ctx,
                      const ::fit::hakuna::kernel::registry::shared::CheckElement& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer);


template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::shared::CheckElement& value);
}
}
}
}
#endif