/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/4/1
 * Notes:       :
 */

#include <fit/external/framework/function_proxy.hpp>

namespace Fit {
namespace Framework {
static std::string PrintArgsType(const Arguments &packArgs)
{
    std::string inputType;
    inputType += "(";
    for (const auto &arg : packArgs) {
        inputType += arg.type().name();
        inputType += ",";
    }
    inputType += ")";

    return inputType;
}

__attribute__ ((visibility ("default"))) std::string ExceptionMessage(const char *desc,
    size_t expectArgCount, const char *functionSign, const Arguments &actualArgs)
{
    std::string result;
    constexpr int32_t DEFAULT_SIZE = 256;
    result.reserve(DEFAULT_SIZE);
    result += desc;
    result += " [expectCount=" + std::to_string(expectArgCount);
    result += ", actualCount=" + std::to_string(actualArgs.size());
    result += ", expectFunctionSign=";
    result += functionSign;
    result += ", actualArgsSign=";
    result += PrintArgsType(actualArgs);
    result += "]";
    return result;
}
}
} // LCOV_EXCL_LINE