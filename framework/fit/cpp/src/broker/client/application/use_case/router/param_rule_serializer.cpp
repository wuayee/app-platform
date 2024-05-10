/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:23
 */

#include "param_rule_serializer.hpp"
#include <fit/stl/string.hpp>

namespace Fit {
ParamRuleSerializer::ParamRuleSerializer(
    ContextObj ctx,
    const Framework::ParamJsonFormatter::ParamJsonFormatterPtr &paramJsonFormatterService,
    const Fit::vector<Fit::any> &params)
    : ctx_(ctx), paramJsonFormatterService_(paramJsonFormatterService), params_(params) {}

FitCode ParamRuleSerializer::Serialize(Fit::string &serializeResult)
{
    Fit::string result;
    auto ret = paramJsonFormatterService_->SerializeParamToJson(
        ctx_, Context::GetGenericableId(ctx_), params_, result);
    if (ret != FIT_OK || result.empty()) {
        return FIT_ERR_FAIL;
    }

    std::ostringstream ss;
    ss << "\"P\" : " << result;
    serializeResult = Fit::to_fit_string(ss.str());
    return FIT_OK;
}
} // LCOV_EXCL_LINE