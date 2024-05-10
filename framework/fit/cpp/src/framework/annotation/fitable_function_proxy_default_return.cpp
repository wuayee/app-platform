/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/21
 * Notes:       :
 */
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_code.h>
#include <fit/fit_log.h>

namespace Fit {
namespace Framework {
namespace Annotation {
FitCode __attribute__ ((visibility ("default"))) FitableFunctionProxyDefaultReturn::operator()(
    const FunctionWrapperError& err, Arguments& args)
{
    FIT_LOG_ERROR("Invoke failed, code = %d, msg = %s.", err.code, err.msg.c_str());
    return FIT_ERR_PARAM;
}
}
void ThrowInvalidArgument(const std::string &msg)
{
#ifdef __cpp_exceptions
    throw std::invalid_argument(msg);
#endif
}
}
} // LCOV_EXCL_LINE