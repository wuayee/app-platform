/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/2/23
 * Notes:       :
 */

#ifndef FORMATTER_SERVICE_MOCK_H
#define FORMATTER_SERVICE_MOCK_H
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
using namespace Fit::Framework;
using namespace Fit::Framework::Formatter;

class FormatterServiceMock : public Fit::Framework::Formatter::FormatterService {
public:
    MOCK_METHOD4(SerializeRequest, FitCode(ContextObj, const BaseSerialization&, const Arguments&, Fit::string&));
    MOCK_METHOD4(DeserializeRequest, FitCode(ContextObj, const BaseSerialization&, const Fit::string&, Arguments&));
    MOCK_METHOD3(SerializeResponse, Fit::string(ContextObj, const BaseSerialization&, const Response&));
    MOCK_METHOD3(DeserializeResponse, Response(ContextObj, const BaseSerialization&, const Fit::string&));
    MOCK_METHOD2(CreateArgOut, Arguments(ContextObj, const BaseSerialization&));
    MOCK_METHOD1(GetFormatter, FormatterMetaPtr(const BaseSerialization&));
    MOCK_METHOD1(GetFormats, Fit::vector<int32_t>(const Fit::string &));
    MOCK_METHOD0(ClearAllFormats, void());
};
#endif // FORMATTER_SERVICE_MOCK_H
