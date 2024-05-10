/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker degradation decorator.
 * Author       : lijunchao
 * Date         : 2021/09/07
 */

#include "router_impl.hpp"

#include <fit/external/broker/broker_client_external.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/router_mock.h>
#include <mock/broker_genericable_config_mock.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace Fit::Framework;
struct TestFitableStruct {
    using InType = ::Fit::Framework::ArgumentsIn<const int *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};

class TestFitable : public ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                                 TestFitableStruct::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "test_fitable_genericable_id";
    TestFitable() : ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                          TestFitableStruct::OutType)>(GENERIC_ID) {}
    ~TestFitable() {}
};
class RouterImplTest : public testing::Test {
public:
    void SetUp() override
    {
        ctx_ = NewContextDefault();
        config_.reset(new BrokerGenericableConfigMock);
    }

    void TearDown() override
    {
        ContextDestroy(ctx_);
    }

    shared_ptr<BrokerGenericableConfigMock> config_;
    ContextObj ctx_;
};

TEST_F(RouterImplTest, should_return_empty_fitable_when_DefaultRouter_Router_given_ruleRouter_Router_empty)
{
    Fit::string expect{""};

    shared_ptr<MockRouter> router = std::make_shared<MockRouter>();
    EXPECT_CALL(*router, Route()).WillOnce(::testing::Return(""));

    auto defaultRoute = Fit::DefaultRouter(ctx_, config_, router);
    auto result = defaultRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest,
    should_return_test_fitable_when_DefaultRouter_Router_given_ruleRouter_Router_return_test_fitable)
{
    Fit::string expect{"test_fitable"};

    shared_ptr<MockRouter> router = std::make_shared<MockRouter>();
    EXPECT_CALL(*router, Route()).WillOnce(::testing::Return("test_fitable"));

    auto defaultRoute = Fit::DefaultRouter(ctx_, config_, router);
    auto result = defaultRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest,
    should_return_empty_fitable_when_DefaultRouter_Router_given_emptry_ruleRouter_and_config_default_is_empty)
{
    Fit::string expect{""};

    EXPECT_CALL(*config_, GetDefault()).WillOnce(::testing::Return(""));

    auto defaultRoute = Fit::DefaultRouter(ctx_, config_, nullptr);
    auto result = defaultRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest,
    should_return_test_fitable_when_DefaultRouter_Router_given_emptry_ruleRouter_and_config_default_is_test_fitable)
{
    Fit::string expect{"test_fitable"};

    EXPECT_CALL(*config_, GetDefault()).WillOnce(::testing::Return("test_fitable"));

    auto defaultRoute = Fit::DefaultRouter(ctx_, config_, nullptr);
    auto result = defaultRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest, should_return_test_fitable_when_AliasRouter_Router_given_config_fitable_is_test_fitable)
{
    Fit::string expect{"test_fitable"};

    EXPECT_CALL(*config_, GetFitableIdByAlias("")).WillOnce(::testing::Return("test_fitable"));

    auto aliasRoute = Fit::AliasRouter(ctx_, config_);
    auto result = aliasRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest, should_return_empty_fitable_when_AliasRouter_Router_given_config_fitable_is_empty)
{
    Fit::string expect{""};

    EXPECT_CALL(*config_, GetFitableIdByAlias("")).WillOnce(::testing::Return(""));

    auto aliasRoute = Fit::AliasRouter(ctx_, config_);
    auto result = aliasRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest, should_return_empty_fitable_when_RuleRouter_Router_given_ruleSerializer_Serialize_error)
{
    Fit::string expect{""};
    Fit::string tmp {""};
    std::shared_ptr<MockRuleSerializer> serializer = std::make_shared<MockRuleSerializer>();
    EXPECT_CALL(*serializer, Serialize(tmp)).WillOnce(::testing::Return(FIT_ERR_FAIL));

    auto ruleRoute = Fit::RuleRouter("", serializer, "", [](const Fit::string &environment,
        const Fit::string &ruleID, const Fit::string &param) -> Fit::string {
        return "test";
    });
    auto result = ruleRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest, should_return_empty_fitable_when_RuleRouter_Router_given_call_rule_engine_return_empty)
{
    Fit::string expect{""};
    Fit::string tmp {""};
    std::shared_ptr<MockRuleSerializer> serializer = std::make_shared<MockRuleSerializer>();
    EXPECT_CALL(*serializer, Serialize(tmp)).WillOnce(::testing::Return(FIT_OK));

    auto ruleRoute = Fit::RuleRouter("", serializer, "", [](const Fit::string &environment,
        const Fit::string &ruleID, const Fit::string &param) -> Fit::string {
        return "";
    });
    auto result = ruleRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest,
    should_return_test_fitable_when_RuleRouter_Router_given_call_rule_engine_return_test_fitable)
{
    Fit::string expect{"test_fitable"};
    Fit::string tmp {""};
    std::shared_ptr<MockRuleSerializer> serializer = std::make_shared<MockRuleSerializer>();
    EXPECT_CALL(*serializer, Serialize(tmp)).WillOnce(::testing::Return(FIT_OK));

    auto ruleRoute = Fit::RuleRouter("", serializer, "", [](const Fit::string &environment,
        const Fit::string &ruleID, const Fit::string &param) -> Fit::string {
        return "test_fitable";
    });
    auto result = ruleRoute.Route();

    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(RouterImplTest, should_return_rule_route_when_build_given_mock_rule_id)
{
    // given
    TestFitable testFitable;
    int a = 1;
    Fit::vector<Fit::any> params {&a};
    Fit::string env = "env_test";

    Fit::Framework::ParamJsonFormatter::ParamJsonFormatterPtr paramJsonFormatterService {nullptr};
    EXPECT_CALL(*config_, GetRuleId()).Times(testing::AtLeast(1)).WillRepeatedly(::testing::Return("test_rule_id"));
    // when
    std::unique_ptr<Fit::RuleRouter> ruleRoute =
        Fit::RuleRouter::Build(testFitable.ctx_, config_, paramJsonFormatterService, params, env);
    // then
    EXPECT_EQ(ruleRoute == nullptr, false);
}

TEST_F(RouterImplTest, should_return_null_when_build_given_param)
{
    // given
    TestFitable testFitable;
    int a = 1;
    Fit::vector<Fit::any> params {&a};
    Fit::string env = "env_test";

    Fit::Framework::ParamJsonFormatter::ParamJsonFormatterPtr paramJsonFormatterService {nullptr};
    // when
    std::unique_ptr<Fit::RuleRouter> ruleRoute =
        Fit::RuleRouter::Build(testFitable.ctx_, config_, paramJsonFormatterService, params, env);
    // then
    EXPECT_EQ(ruleRoute == nullptr, true);
}

TEST_F(RouterImplTest, should_return_given_fitableId_when_route_given_fitableId)
{
    // given
    string expectFitableId = "fitalbeId";
    ContextSetFitableId(ctx_, expectFitableId.c_str());
    Fit::FitableIdRouter router(ctx_);
    auto ret = router.Route();
    EXPECT_EQ(ret, expectFitableId);
}