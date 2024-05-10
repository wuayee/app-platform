/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/2/17
 * Notes:       :
 */

#ifndef FORMATTER_REPO_MOCK_HPP
#define FORMATTER_REPO_MOCK_HPP

#include <gmock/gmock.h>
#include <fit/internal/framework/formatter_repo.hpp>

class FormatterRepoMock : public Fit::Framework::Formatter::FormatterRepo {
public:
    using FormatterMetaPtr = ::Fit::Framework::Formatter::FormatterMetaPtr;
    using FormatterMetaPtrList = ::Fit::Framework::Formatter::FormatterMetaPtrList;
    using BaseSerialization = ::Fit::Framework::Formatter::BaseSerialization;

    MOCK_METHOD1(Get, FormatterMetaPtr(const BaseSerialization&));
    MOCK_METHOD1(Add, FitCode(FormatterMetaPtrList));
    MOCK_METHOD1(Remove, FitCode(FormatterMetaPtrList));
    MOCK_METHOD1(GetFormats, Fit::vector<int32_t>(const Fit::string &));
    MOCK_METHOD0(Clear, void());
};

#endif // FORMATTER_REPO_MOCK_HPP
