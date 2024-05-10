/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/7/20 20:41
 */

#include <fit/external/broker/broker_client_external.hpp>
#include <mock/router_mock.h>
#include <mock/broker_genericable_config_mock.hpp>
#include "gtest/gtest.h"
#include "broker_client_fit_config.h"
#include "gmock/gmock.h"
#include "param_rule_serializer.hpp"
#include "tag_rule_serializer.hpp"
#include "context_rule_serializer.hpp"

using namespace std;
using namespace Fit::Framework;

class RuleSerializerTest : public testing::Test {
public:
    void SetUp() override
    {
        ctx_ = NewContextDefault();
        formatter_.reset(new MockParamJsonFormatter);
        serializer_.reset(new MockRuleSerializer);
        config_.reset(new Fit::BrokerGenericableConfigMock);
    }

    void TearDown() override
    {
        ContextDestroy(ctx_);
    }

    shared_ptr<MockParamJsonFormatter> formatter_;
    shared_ptr<MockRuleSerializer> serializer_;
    shared_ptr<Fit::BrokerGenericableConfigMock> config_;
    ContextObj ctx_;
};

TEST_F(RuleSerializerTest, should_return_empty_json_when_base_serializer_given_not_add_serializer)
{
    Fit::string expect {"{}"};
    Fit::string result {};

    auto serializer = Fit::BaseRuleSerializer();
    auto ret = serializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_FIT_ERR_when_base_serializer_given_add_bad_serializer)
{
    Fit::string expect {""};
    Fit::string result {};
    Fit::string tmp {};

    std::shared_ptr<MockRuleSerializer> serializerM = std::make_shared<MockRuleSerializer>();
    EXPECT_CALL(*serializerM, Serialize(tmp)).WillOnce(::testing::Return(FIT_ERR_FAIL));

    auto serializer = Fit::BaseRuleSerializer();
    serializer.AddSerializer(serializerM);

    auto ret = serializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Ne(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_target_json_when_base_serializer_given_add_serializer)
{
    Fit::string expect {"{{param},{tag}}"};
    Fit::string result {};
    Fit::string param {"{param}"};
    Fit::string rule {"{tag}"};

    EXPECT_CALL(*serializer_, Serialize(::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<0>(param), ::testing::Return(FIT_OK)));

    shared_ptr<MockRuleSerializer> serializer2 = std::make_shared<MockRuleSerializer>();
    EXPECT_CALL(*serializer2, Serialize(::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<0>(rule), ::testing::Return(FIT_OK)));

    auto serializer = Fit::BaseRuleSerializer();
    serializer.AddSerializer(serializer_);
    serializer.AddSerializer(serializer2);

    auto ret = serializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_FIT_ERR_SERIALIZE_when_tag_serializer_given_paramJsonFormat_error)
{
    Fit::string result;
    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());

    Fit::vector<Fit::string> configRes;
    configRes.push_back("123");
    EXPECT_CALL(*config_, GetParamTagByIdx(::testing::_))
        .WillOnce(::testing::Return(configRes));

    EXPECT_CALL(*formatter_,
        SerializeIndexParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::Return(FIT_ERR_SERIALIZE));

    auto tagSerializer = Fit::TagRuleSerializer(ctx_, config_,
        formatter_, params, "", nullptr);

    auto ret = tagSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SERIALIZE));
}

TEST_F(RuleSerializerTest,
    should_return_OK_and_result_when_tag_serializer_given_one_params_GetParamTagByIdx_return_empty)
{
    Fit::string expect {"\"T\" : {\"arg0\":[]}"};
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());

    Fit::vector<Fit::string> configRes;
    EXPECT_CALL(*config_, GetParamTagByIdx(::testing::_))
        .WillOnce(::testing::Return(configRes));

    auto tagSerializer = Fit::TagRuleSerializer(ctx_, config_,
        formatter_, params, "", nullptr);

    auto ret = tagSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_tag_serializer_given_one_params_one_tag)
{
    Fit::string expect {"\"T\" : {\"arg0\":[\"123\"]}"};
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());

    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");
    EXPECT_CALL(*config_, GetParamTagByIdx(::testing::_))
        .WillOnce(::testing::Return(configRes));

    EXPECT_CALL(*formatter_,
        SerializeIndexParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<4>("res"), ::testing::Return(FIT_OK)));

    auto functor = [](const Fit::string &environment,
        const Fit::vector<Fit::string> &tagIds,
        const Fit::string &argJson,
        Fit::vector<Fit::string> &tags) -> FitCode {
        tags.push_back("123");
        return FIT_OK;
    };

    auto tagSerializer = Fit::TagRuleSerializer(ctx_,
        config_,
        formatter_,
        params,
        "",
        std::move(functor));

    auto ret = tagSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_tag_serializer_given_one_params_two_tags)
{
    Fit::string expect {"\"T\" : {\"arg0\":[\"123\",\"456\"]}"};
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());

    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");
    EXPECT_CALL(*config_, GetParamTagByIdx(::testing::_))
        .WillOnce(::testing::Return(configRes));

    EXPECT_CALL(*formatter_,
        SerializeIndexParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<4>("res"), ::testing::Return(FIT_OK)));

    auto functor = [](const Fit::string &environment,
        const Fit::vector<Fit::string> &tagIds,
        const Fit::string &argJson,
        Fit::vector<Fit::string> &tags) -> FitCode {
        tags.push_back("123");
        tags.push_back("456");
        return FIT_OK;
    };

    auto tagSerializer = Fit::TagRuleSerializer(ctx_,
        config_,
        formatter_,
        params,
        "",
        std::move(functor));

    auto ret = tagSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_tag_serializer_given_two_params_two_tags)
{
    Fit::string expect {"\"T\" : {\"arg0\":[\"123\",\"456\"],\"arg1\":[\"123\",\"456\"]}"};
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());
    params.push_back(Fit::any());
    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");

    EXPECT_CALL(*config_, GetParamTagByIdx(::testing::_))
        .WillOnce(::testing::Return(configRes))
        .WillOnce(::testing::Return(configRes));

    EXPECT_CALL(*formatter_,
        SerializeIndexParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<4>("res"), ::testing::Return(FIT_OK)))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<4>("res"), ::testing::Return(FIT_OK)));

    auto functor = [](const Fit::string &environment,
        const Fit::vector<Fit::string> &tagIds,
        const Fit::string &argJson,
        Fit::vector<Fit::string> &tags) -> FitCode {
        tags.push_back("123");
        tags.push_back("456");
        return FIT_OK;
    };

    auto tagSerializer = Fit::TagRuleSerializer(ctx_,
        config_,
        formatter_,
        params,
        "",
        std::move(functor));

    auto ret = tagSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_FIT_ERR_FAIL_when_param_serializer_SerializeParamToJson_return_err)
{
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());
    params.push_back(Fit::any());
    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");

    EXPECT_CALL(*formatter_,
        SerializeParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<3>("res"), ::testing::Return(FIT_ERR_FAIL)));

    auto paramSerializer = Fit::ParamRuleSerializer(ctx_,
        formatter_,
        params);

    auto ret = paramSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(RuleSerializerTest,
    should_return_FIT_ERR_FAIL_when_param_serializer_SerializeParamToJson_return_result_empty)
{
    Fit::string result {};

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());
    params.push_back(Fit::any());
    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");

    EXPECT_CALL(*formatter_,
        SerializeParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<3>(""), ::testing::Return(FIT_OK)));

    auto paramSerializer = Fit::ParamRuleSerializer(ctx_,
        formatter_,
        params);

    auto ret = paramSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FAIL));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_param_serializer_SerializeParamToJson_return_result)
{
    Fit::string expect {"\"P\" : {123}"};
    Fit::string result;

    Fit::vector<Fit::any> params;
    params.push_back(Fit::any());
    params.push_back(Fit::any());
    Fit::vector<Fit::string> configRes;
    configRes.push_back("key");

    EXPECT_CALL(*formatter_,
        SerializeParamToJson(::testing::_, ::testing::_, ::testing::_, ::testing::_))
        .WillOnce(::testing::DoAll(::testing::SetArgReferee<3>("{123}"), ::testing::Return(FIT_OK)));

    auto paramSerializer = Fit::ParamRuleSerializer(ctx_,
        formatter_,
        params);

    auto ret = paramSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_context_serializer_given_empty_global_context)
{
    Fit::string expect {"\"C\" : {}"};
    Fit::string result;

    auto ctxSerializer = Fit::ContextRuleSerializer(ctx_);
    auto ret = ctxSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RuleSerializerTest, should_return_OK_and_result_when_context_serializer_given_global_context)
{
    Fit::string expect {"\"C\" : {\"000\":\"111\",\"123\":\"456\"}"};
    Fit::string result;

    Fit::Context::PutRouteContext(ctx_, "123", "456");
    Fit::Context::PutRouteContext(ctx_, "000", "111");

    auto ctxSerializer = Fit::ContextRuleSerializer(ctx_);
    auto ret = ctxSerializer.Serialize(result);

    EXPECT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, ::testing::Eq(expect));

    Fit::Context::Global::RestoreGlobalContext(ctx_, {});
}