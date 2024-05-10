/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#include <runtime/config/genericable_configuration_parser.hpp>
#include <fit/external/util/string_utils.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Configuration;

class GenericableConfigurationParserTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(GenericableConfigurationParserTest, should_return_correct_params_when_get_params_given_params_config_items)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"params.x.taggers.a2100ca0652646208f2e2f98911ac781.id", "a2100ca0652646208f2e2f98911ac781"},
        {"params.x.index",                                       "0"},
        {"params.y.taggers.a2100ca0652646208f2e2f98911ac782.id", "a2100ca0652646208f2e2f98911ac782"},
        {"params.y.index",                                       "1"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        ParamsRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    EXPECT_THAT(ParamsRoutine::Key(), ::testing::Eq("params"));
    ASSERT_THAT(genericableConfiguration->GetParams().size(), ::testing::Eq(2));

    EXPECT_THAT(genericableConfiguration->GetParams()[0].name, ::testing::Eq("x"));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].index, ::testing::Eq(0));
    ASSERT_THAT(genericableConfiguration->GetParams()[0].taggerIds.size(), ::testing::Eq(1));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].taggerIds[0],
        ::testing::Eq("a2100ca0652646208f2e2f98911ac781"));
    EXPECT_THAT(genericableConfiguration->GetParams()[1].name, ::testing::Eq("y"));
    EXPECT_THAT(genericableConfiguration->GetParams()[1].index, ::testing::Eq(1));
    ASSERT_THAT(genericableConfiguration->GetParams()[1].taggerIds.size(), ::testing::Eq(1));
    EXPECT_THAT(genericableConfiguration->GetParams()[1].taggerIds[0],
        ::testing::Eq("a2100ca0652646208f2e2f98911ac782"));
}

TEST_F(GenericableConfigurationParserTest,
    should_return_correct_params_when_get_params_given_params_config_items_and_mutil_taggers)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"params.x.taggers.a2100ca0652646208f2e2f98911ac781.id", "a2100ca0652646208f2e2f98911ac781"},
        {"params.x.index",                                       "0"},
        {"params.x.taggers.a2100ca0652646208f2e2f98911ac782.id", "a2100ca0652646208f2e2f98911ac782"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        ParamsRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    EXPECT_THAT(ParamsRoutine::Key(), ::testing::Eq("params"));
    ASSERT_THAT(genericableConfiguration->GetParams().size(), ::testing::Eq(1));

    EXPECT_THAT(genericableConfiguration->GetParams()[0].name, ::testing::Eq("x"));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].index, ::testing::Eq(0));
    ASSERT_THAT(genericableConfiguration->GetParams()[0].taggerIds.size(), ::testing::Eq(2));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].taggerIds[0],
        ::testing::Eq("a2100ca0652646208f2e2f98911ac781"));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].taggerIds[1],
        ::testing::Eq("a2100ca0652646208f2e2f98911ac782"));
}

TEST_F(GenericableConfigurationParserTest,
    should_do_nothing_when_get_params_given_params_config_items_wrong_params_layer)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"params.x.taggers.less2",          "less2"},
        {"params.x.taggers.xx.unknown_key", "unknown_key"},
        {"params.x.XXX",                    "XXX"},
        {"params",                          "1"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        ParamsRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    ASSERT_THAT(genericableConfiguration->GetParams().size(), ::testing::Eq(1));
    ASSERT_THAT(genericableConfiguration->GetParams()[0].name, ::testing::Eq("x"));
}

TEST_F(GenericableConfigurationParserTest,
    should_do_nothind_when_get_params_given_params_config_items_and_repeatted_taggers_id)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"params.x.taggers.a2100ca0652646208f2e2f98911ac782.id", "a2100ca0652646208f2e2f98911ac782"},
        {"params.x.taggers.a2100ca0652646208f2e2f98911ac782.id", "a2100ca0652646208f2e2f98911ac782"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        ParamsRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    ASSERT_THAT(genericableConfiguration->GetParams().size(), ::testing::Eq(1));

    ASSERT_THAT(genericableConfiguration->GetParams()[0].taggerIds.size(), ::testing::Eq(1));
    EXPECT_THAT(genericableConfiguration->GetParams()[0].taggerIds[0],
        ::testing::Eq("a2100ca0652646208f2e2f98911ac782"));
}

TEST_F(GenericableConfigurationParserTest,
    should_return_correct_router_rule_when_get_rule_given_rule_config_items)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"route.default",   "a2100ca0652646208f2e2f98911ac781"},
        {"route.rule.id",   "a2100ca0652646208f2e2f98911ac782"},
        {"route.rule.type", "P,T"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        RouterRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    EXPECT_THAT(RouterRoutine::Key(), ::testing::Eq("route"));

    EXPECT_THAT(genericableConfiguration->GetRule().id, ::testing::Eq("a2100ca0652646208f2e2f98911ac782"));
    EXPECT_THAT(genericableConfiguration->GetRule().defaultFitableId,
        ::testing::Eq("a2100ca0652646208f2e2f98911ac781"));
    ASSERT_THAT(genericableConfiguration->GetRule().types.size(), ::testing::Eq(2));
    EXPECT_THAT(genericableConfiguration->GetRule().types[0], ::testing::Eq("P"));
    EXPECT_THAT(genericableConfiguration->GetRule().types[1], ::testing::Eq("T"));
}

TEST_F(GenericableConfigurationParserTest,
    should_do_nothing_when_get_rule_given_rule_config_items_wrong_layer_keys)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"route.rule",             "no child key"},
        {"route.unknown_key",      "not matched"},
        {"route",                  "less key"},
        {"route.rule.unknown_key", "P,T"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        RouterRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    EXPECT_THAT(genericableConfiguration->GetRule().id, ::testing::Eq(""));
    EXPECT_THAT(genericableConfiguration->GetRoute(), ::testing::Eq(""));
    EXPECT_THAT(genericableConfiguration->GetLoadbalance(), ::testing::Eq(""));
}

TEST_F(GenericableConfigurationParserTest,
    should_do_nothing_when_get_fitables_given_config_items_wrong_layer_keys)
{
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    Fit::vector<std::pair<Fit::string, Fit::string>> configItems = {
        {"fitables.a-b-c.unknown_key", "unknown_key"},
        {"fitables.a-b-c.unknown_key.1", "unknown_key"},
        {"fitables.a-b-c", "no_key"},
        {"fitables", "degradation"}
    };
    for (const auto &item : configItems) {
        auto keys = Fit::StringUtils::Split(item.first, '.');
        FitablesRoutine::GetRoutine()(genericableConfiguration,
            Fit::range_skip<Fit::vector<Fit::string>>(keys, 1), item.second);
    }

    EXPECT_THAT(genericableConfiguration->GetFitables().size(), ::testing::Eq(1));
}
