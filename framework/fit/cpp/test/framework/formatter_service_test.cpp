/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : formatter service test
 * Author       : songyongtan
 * Date         : 2021/6/1
 */

#include <fit/internal/framework/formatter_service.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <mock/formatter_repo_mock.hpp>
#include <src/framework/default_formatter_service.hpp>
#include "framework/formatter/json_formatter_entry.hpp"
#include "framework/formatter/protobuf_formatter_entry.hpp"

using namespace ::Fit;
using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Formatter;
using namespace ::testing;

class FormatterServiceTest : public testing::Test {
public:
    void SetUp() override
    {
        validTarget_.genericId = "add";
        validTarget_.fitableType = Fit::Framework::Annotation::FitableType::MAIN;
        validTarget_.formats = {PROTOCOL_TYPE_PROTOBUF, PROTOCOL_TYPE_JSON};

        validArgs_ = {&args1, &args2};
        mockRepo_ = std::make_shared<FormatterRepoMock>();
        service_ = make_unique<DefaultFormatterService>(mockRepo_);
        service_->AddFormatterEntry(make_shared<JsonFormatterEntry>());
        service_->AddFormatterEntry(make_shared<ProtobufFormatterEntry>());

        validJsonConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
        validJsonConverterMeta_->SetGenericId(validTarget_.genericId);
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
        validProtobufConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
        validProtobufConverterMeta_->SetGenericId(validJsonConverterMeta_->GetGenericId());
        validProtobufConverterMeta_->SetFormat(PROTOCOL_TYPE_PROTOBUF);
        validProtobufConverterMeta_->SetArgsInConverter(validJsonConverterMeta_->GetArgsInConverter());
    }

    void TearDown() override {}

protected:
    BaseSerialization validTarget_;
    Arguments validArgs_;
    std::shared_ptr<FormatterRepoMock> mockRepo_;
    std::unique_ptr<DefaultFormatterService> service_;
    int32_t args1 {1};
    int32_t args2 {2};
    int32_t *responseArg_ {&args2};
    Fit::string expectedSerializeRequestJsonResult = "[1,2]";
    Fit::string expectedSerializeRequestProtobufResult_ {"\n\001\000\022\001\061\022\001\062",
        sizeof("\n\001\000\022\001\061\022\001\062") - 1};
    FormatterMetaPtr validJsonConverterMeta_;
    FormatterMetaPtr validProtobufConverterMeta_;
};

TEST_F(FormatterServiceTest, should_return_success_when_SerializeRequest_given_matched_args_for_json)
{
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expectedSerializeRequestJsonResult));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_not_matched_args_converter)
{
    validJsonConverterMeta_->SetArgsInConverter({{},
                                                 {},
                                                 {}});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_MATCH));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_json_arg_converter_return_fail)
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

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_protobuf_arg_converter_return_fail)
{
    validProtobufConverterMeta_->SetArgsInConverter({
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_ERR_FAIL;
            }, {}
        },
        {}});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(FormatterServiceTest,
    should_return_success_when_SerializeRequest_given_json_arg_converter_return_null_praram)
{
    validJsonConverterMeta_->SetArgsInConverter({
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_NULL_PARAM;
            }, {}
        },
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_NULL_PARAM;
            }, {}
        }});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
}

TEST_F(FormatterServiceTest,
    should_return_success_when_SerializeRequest_given_protobuf_arg_converter_return_null_praram)
{
    validProtobufConverterMeta_->SetArgsInConverter({
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_NULL_PARAM;
            }, {}
        },
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_NULL_PARAM;
            }, {}
        }});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_wrong_arg_string_for_json)
{
    validJsonConverterMeta_->SetArgsInConverter({
        {
            [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                result = "{";
                return FIT_OK;
            }, {}
        },
        {}
    });
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_SERIALIZE_JSON));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_wrong_format)
{
    auto wrongConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    wrongConverterMeta_->SetGenericId(validTarget_.genericId);
    wrongConverterMeta_->SetFormat(100);
    wrongConverterMeta_->SetArgsInConverter({{}, {}});
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(wrongConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, {"",
            {100}, Fit::Framework::Annotation::FitableType::MAIN},
        validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_SUPPORT));
}

TEST_F(FormatterServiceTest, should_return_success_when_SerializeRequest_given_matched_args_for_protobuf)
{
    Fit::string result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));

    auto ret = service_->SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result == expectedSerializeRequestProtobufResult_, true);
}

TEST_F(FormatterServiceTest, should_return_success_when_DeserializeRequest_given_matched_args_for_json)
{
    Arguments result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->DeserializeRequest(nullptr, validTarget_, expectedSerializeRequestJsonResult, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_THAT(result.size(), ::testing::Eq(2));
    EXPECT_THAT(*Fit::any_cast<int32_t *>(result[0]), ::testing::Eq(args1));
    EXPECT_THAT(*Fit::any_cast<int32_t *>(result[1]), ::testing::Eq(args2));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeRequest_given_wrong_json_string)
{
    Arguments result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto ret = service_->DeserializeRequest(nullptr, validTarget_, "{", result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_DESERIALIZE_JSON));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeRequest_given_wrong_args_buffer_for_protobuf)
{
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));
    Arguments result;
    auto ret = service_->DeserializeRequest(nullptr, validTarget_, "{", result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_DESERIALIZE_PB));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeRequest_given_wrong_arg_buffer_for_protobuf)
{
    validProtobufConverterMeta_->SetArgsInConverter({
        {
            {},
            [](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                return FIT_ERR_FAIL;
            }
        },
        {}
    });

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));
    Arguments result;
    auto ret = service_->DeserializeRequest(nullptr, validTarget_, {"\n\001\000\022\001\061\022\001\062",
        sizeof("\n\001\000\022\001\061\022\001\062") - 1}, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeRequest_given_deserialize_arg_fail_for_json)
{
    validJsonConverterMeta_->SetArgsInConverter({
        {
            {},
            [](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                return FIT_ERR_FAIL;
            }
        },
        {}
    });

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));
    Arguments result;
    auto ret = service_->DeserializeRequest(nullptr, validTarget_, expectedSerializeRequestJsonResult, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(FormatterServiceTest,
    should_return_not_match_when_DeserializeRequest_given_args_buffer_not_matched_for_protobuf)
{
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));
    Arguments result;
    auto ret = service_->DeserializeRequest(nullptr, validTarget_, {"\n\001\000\022\001\061\022\001\062\022\001\062",
        sizeof("\n\001\000\022\001\061\022\001\062\022\001\062") - 1}, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_MATCH));
}

TEST_F(FormatterServiceTest, should_return_not_support_when_DeserializeRequest_given_not_support_format_type)
{
    auto wrongConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    wrongConverterMeta_->SetGenericId(validTarget_.genericId);
    wrongConverterMeta_->SetFormat(100);
    wrongConverterMeta_->SetArgsInConverter({{}, {}});
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(wrongConverterMeta_));
    Arguments result;
    auto ret = service_->DeserializeRequest(nullptr, {"", {100}, Fit::Framework::Annotation::FitableType::MAIN},
        expectedSerializeRequestProtobufResult_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_SUPPORT));
}

TEST_F(FormatterServiceTest, should_return_success_when_DeserializeRequest_given_matched_args_for_protobuf)
{
    Arguments result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));

    auto ret = service_->DeserializeRequest(nullptr, validTarget_, expectedSerializeRequestProtobufResult_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_THAT(result.size(), ::testing::Eq(2));
    EXPECT_THAT(*Fit::any_cast<int32_t *>(result[0]), ::testing::Eq(args1));
    EXPECT_THAT(*Fit::any_cast<int32_t *>(result[1]), ::testing::Eq(args2));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeRequest_given_null_repo)
{
    Arguments result;
    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    auto ret = service.DeserializeRequest(nullptr, validTarget_, expectedSerializeRequestProtobufResult_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_FOUND));
}

TEST_F(FormatterServiceTest, should_return_ok_when_DeserializeRequest_given_empty_arg_converter)
{
    validProtobufConverterMeta_->SetArgsOutConverter({});
    Arguments result;

    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validProtobufConverterMeta_));

    auto ret = service_->DeserializeRequest(nullptr, validTarget_, expectedSerializeRequestProtobufResult_, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
}

TEST_F(FormatterServiceTest, should_return_success_when_SerializeResponse_given_matched_args_for_json)
{
    Response response;
    response.code = FIT_OK;
    response.args = {&responseArg_};
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string("2"), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_error_code)
{
    Response response;
    response.code = FIT_ERR_FAIL;
    response.args = {&responseArg_};

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_null_repo)
{
    Response response;
    response.code = FIT_OK;
    response.args = {&responseArg_};
    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    auto result = service.SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_empty_converter)
{
    validJsonConverterMeta_->SetArgsOutConverter({});
    Response response;
    response.code = FIT_OK;
    response.args = {&responseArg_};
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_empty_args)
{
    validJsonConverterMeta_->SetArgsOutConverter({});
    Response response;
    response.code = FIT_OK;
    response.args;
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_not_matched_converter)
{
    validJsonConverterMeta_->SetArgsOutConverter({{}, {}});
    Response response;
    response.code = FIT_OK;
    response.args = {&responseArg_};
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_empty_when_SerializeResponse_given_converter_deserialize_fail)
{
    validJsonConverterMeta_->SetArgsOutConverter(
        {
            {
                [](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                    return FIT_ERR_FAIL;
                },
                {}
            }
        });
    Response response;
    response.code = FIT_OK;
    response.args = {&responseArg_};
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->SerializeResponse(nullptr, validTarget_, response);

    ASSERT_EQ(result == Fit::string(""), true);
}

TEST_F(FormatterServiceTest, should_return_success_when_DeserializeResponse_given_matched_args_for_json)
{
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->DeserializeResponse(nullptr, validTarget_, "");

    ASSERT_THAT(result.args.size(), ::testing::Eq(1));
    EXPECT_THAT(**Fit::any_cast<int32_t **>(result.args[0]), ::testing::Eq(*responseArg_));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeResponse_given_null_repo)
{
    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    auto result = service.DeserializeResponse(nullptr, validTarget_, "");

    ASSERT_THAT(result.code, ::testing::Eq(FIT_ERR_NOT_FOUND));
}

TEST_F(FormatterServiceTest, should_return_not_match_when_DeserializeResponse_given_multi_not_matched_converter)
{
    validJsonConverterMeta_->SetArgsOutConverter({
        {
            [this](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_OK;
            },
            [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                return FIT_OK;
            }
        },
        {
            [this](ContextObj ctx, const Fit::any &arg, Fit::string &result) -> FitCode {
                return FIT_OK;
            },
            [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                return FIT_OK;
            }
        }});
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->DeserializeResponse(nullptr, validTarget_, "");

    ASSERT_THAT(result.code, ::testing::Eq(FIT_ERR_NOT_MATCH));
}

TEST_F(FormatterServiceTest, should_return_fail_when_DeserializeResponse_given_deserialize_arg_fail)
{
    validJsonConverterMeta_->SetArgsOutConverter({
        {
            {},
            [this](ContextObj ctx, const Fit::string &buffer, Fit::any &result) -> FitCode {
                return FIT_ERR_FAIL;
            }
        }});
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->DeserializeResponse(nullptr, validTarget_, "");

    ASSERT_THAT(result.code, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(FormatterServiceTest, should_return_success_when_DeserializeResponse_given_empty_converter)
{
    validJsonConverterMeta_->SetArgsOutConverter({});
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->DeserializeResponse(nullptr, validTarget_, "");

    ASSERT_THAT(result.code, ::testing::Eq(FIT_OK));
}

TEST_F(FormatterServiceTest, should_return_given_formats_when_GetFormats_given_formats)
{
    EXPECT_CALL(*mockRepo_, GetFormats(_)).WillOnce(Return(Fit::vector<int32_t> {1}));

    auto result = service_->GetFormats(validTarget_.genericId);

    ASSERT_THAT(result.size(), ::testing::Eq(1));
    EXPECT_THAT(result[0], ::testing::Eq(1));
}

TEST_F(FormatterServiceTest, should_return_out_arg_when_CreateArgOut_given_valid_generiacable_id)
{
    EXPECT_CALL(*mockRepo_, Get(_)).WillOnce(Return(validJsonConverterMeta_));

    auto result = service_->CreateArgOut(nullptr, validTarget_);

    ASSERT_THAT(result.size(), ::testing::Eq(1));
    EXPECT_NO_THROW(Fit::any_cast<int32_t **>(result[0]));
}

TEST_F(FormatterServiceTest, should_return_empty_when_CreateArgOut_given_null_repo)
{
    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    auto result = service.CreateArgOut(nullptr, validTarget_);

    ASSERT_THAT(result.size(), ::testing::Eq(0));
}

TEST_F(FormatterServiceTest, should_return_fail_when_SerializeRequest_given_null_repo)
{
    Fit::string result;

    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    auto ret = service.SerializeRequest(nullptr, validTarget_, validArgs_, result);

    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_NOT_FOUND));
}

TEST_F(FormatterServiceTest, should_call_clear_when_ClearAllFormats_given_null_repo)
{
    Fit::string result;

    DefaultFormatterService service(FormatterRepoPtr(nullptr));

    service.ClearAllFormats();
    EXPECT_CALL(*mockRepo_, Clear()).Times(1);
    service_->ClearAllFormats();
}
