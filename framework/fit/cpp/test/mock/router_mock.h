/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/9/7 11:15
 */
#ifndef ROUTER_MOCK_H
#define ROUTER_MOCK_H

#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <fit/internal/framework/param_json_formatter_service.hpp>
#include "fit_config.h"
#include "rule_serializer.hpp"
#include "router.hpp"

using namespace std;
using namespace Fit::Framework;

struct MockRouter : public Fit::Router {
    MOCK_METHOD0(Route, Fit::string());
};

struct MockRuleSerializer : public Fit::RuleSerializer {
    MOCK_METHOD1(Serialize, FitCode(Fit::string &serializeResult));
};

struct MockParamJsonFormatter : public ParamJsonFormatter::ParamJsonFormatterService {
    MOCK_METHOD4(SerializeParamToJson, FitCode(ContextObj ctx,
        const Fit::string& genericID,
        const Arguments& args,
        Fit::string& result));

    MOCK_METHOD5(SerializeIndexParamToJson, FitCode(ContextObj ctx,
        const Fit::string& genericID,
        int32_t idx,
        const Argument& arg,
        Fit::string& result));
};

#endif // ROUTER_MOCK_H
