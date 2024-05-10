/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/22
 * Notes:       :
 */

#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external//framework/formatter/formatter_collector.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <mock/formatter_repo_mock.hpp>
#include <src/framework/default_param_json_formatter_service.hpp>

using namespace ::Fit;
using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Formatter;
using namespace ::Fit::Framework::ParamJsonFormatter;
using namespace ::testing;

class JsonParamFormatterServiceTest : public testing::Test {
public:
    void SetUp() override
    {
        validGenericId_ = "add";
        validArgs_ = {&args1, &args2};
        mockRepo_ = std::make_shared<FormatterRepoMock>();
        service_ = make_unique<DefaultParamJsonFormatterService>(mockRepo_);

        validJsonConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
        validJsonConverterMeta_->SetGenericId(validGenericId_);
        validJsonConverterMeta_->SetFormat(PROTOCOL_TYPE_JSON);
        validJsonConverterMeta_->SetArgsInConverter(
            {
                {
                    [this](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                        result = Fit::to_string(args1);
                        return FIT_OK;
                    },
                    [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                        result = &args1;
                        return FIT_OK;
                    }
                },
                {
                    [this](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                        result = Fit::to_string(args2);
                        return FIT_OK;
                    },
                    [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                        result = &args2;
                        return FIT_OK;
                    }
                }
            });
        validJsonConverterMeta_->SetArgsOutConverter(
            {
                {
                    [this](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                        result = Fit::to_string(*responseArg_);
                        return FIT_OK;
                    },
                    [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                        result = &responseArg_;
                        return FIT_OK;
                    }
                }
            });
        validJsonConverterMeta_->SetCreateArgsOut([this](ContextObj ctx) -> Arguments {
            return Arguments {&responseArg_};
        });
    }

    void TearDown() override {}

protected:
    static ArgConverter GetArgConverterWithReturn(FitCode ret)
    {
        return {
            [ret](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return ret;
            }, {}
        };
    }

    Fit::string validGenericId_;
    Arguments validArgs_;
    std::shared_ptr<FormatterRepoMock> mockRepo_;
    std::unique_ptr<DefaultParamJsonFormatterService> service_;
    int32_t args1 {1};
    int32_t args2 {2};
    int32_t *responseArg_ {&args2};
    Fit::string expectedSerializeAllArgsResult_ = R"({"arg0":1,"arg1":2})";
    Fit::string expectedSerializeArg1JsonResult_ = R"(1)";
    FormatterMetaPtr validJsonConverterMeta_;
};

TEST_F(JsonParamFormatterServiceTest, should_return_success_when_SerializeParamToJson_given_all_args)
{
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeParamToJson(nullptr, validGenericId_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expectedSerializeAllArgsResult_));
}

TEST_F(JsonParamFormatterServiceTest,
    should_return_fail_when_SerializeParamToJson_given_arg_converter_return_fail)
{
    validJsonConverterMeta_->SetArgsInConverter({
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_ERR_FAIL;
            }, {}
        },
        {}});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeParamToJson(nullptr, validGenericId_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(JsonParamFormatterServiceTest,
    should_return_success_when_SerializeParamToJson_given_arg_converter_return_null_praram)
{
    validJsonConverterMeta_->SetArgsInConverter({
        GetArgConverterWithReturn(FIT_NULL_PARAM),
        GetArgConverterWithReturn(FIT_NULL_PARAM)
    });
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeParamToJson(nullptr, validGenericId_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_EQ(result == Fit::string("{\"arg0\":null,\"arg1\":null}"), true);
}

TEST_F(JsonParamFormatterServiceTest,
    should_return_fail_when_SerializeParamToJson_given_not_matched_args_converter)
{
    validJsonConverterMeta_->SetArgsInConverter({{},
                                                 {},
                                                 {}});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));
    auto ret = service_->SerializeParamToJson(nullptr, validGenericId_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_MATCH));
}

TEST_F(JsonParamFormatterServiceTest, should_return_fail_when_SerializeParamToJson_given_empty_formatter)
{
    FormatterMetaPtr empty;
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(empty));

    auto ret = service_->SerializeParamToJson(nullptr, validGenericId_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_FOUND));
}

TEST_F(JsonParamFormatterServiceTest, should_return_success_when_SerializeIndexParamToJson_given_arg1)
{
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeIndexParamToJson(nullptr, validGenericId_, 0, validArgs_[0], result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expectedSerializeArg1JsonResult_));
}

TEST_F(JsonParamFormatterServiceTest,
    should_return_success_when_SerializeIndexParamToJson_given_arg_converter_return_null_praram)
{
    validJsonConverterMeta_->SetArgsInConverter({
        GetArgConverterWithReturn(FIT_NULL_PARAM),
        GetArgConverterWithReturn(FIT_NULL_PARAM)
    });
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));
    auto ret = service_->SerializeIndexParamToJson(nullptr, validGenericId_, 0, validArgs_[0], result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_EQ(result == Fit::string("null"), true);
}

TEST_F(JsonParamFormatterServiceTest,
    should_return_not_matched_when_SerializeIndexParamToJson_given_arg_index_over_converter_size)
{
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeIndexParamToJson(nullptr, validGenericId_, 10, validArgs_[0], result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_MATCH));
}
