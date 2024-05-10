/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#include <src/framework/fitable_discovery_default_impl.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;
using namespace ::testing;

TEST(FitableDiscoveryDefaultImplTest, should_return_empty_when_get_local_fitable_given_invalid_fitable)
{
    Fit::string genericId = "1";
    Fit::string invalidGenericId = "invalid";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;

    fitableDiscovery.RegisterLocalFitable(details);

    Fit::Framework::Fitable fitable;
    fitable.genericId = invalidGenericId;
    fitable.genericVersion = "";
    fitable.fitableId = fitableId;
    fitable.fitableVersion = "";
    auto result = fitableDiscovery.GetLocalFitable(fitable);

    ASSERT_TRUE(result.empty());
}

TEST(FitableDiscoveryDefaultImplTest, should_get_registered_fitable_when_get_local_fitable_given_valid_fitable)
{
    Fit::string genericId = "1";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;

    fitableDiscovery.RegisterLocalFitable(details);
    Fit::Framework::Fitable fitable;
    fitable.genericId = genericId;
    fitable.genericVersion = "";
    fitable.fitableId = fitableId;
    fitable.fitableVersion = "";
    auto result = fitableDiscovery.GetLocalFitable(fitable);

    ASSERT_THAT(result.size(), ::testing::Eq(details.size()));
    EXPECT_THAT(result, Contains(detail1));
    EXPECT_THAT(result, Contains(detail2));
}

TEST(FitableDiscoveryDefaultImplTest, should_return_empty_when_get_local_fitable_given_unregistered_fitable)
{
    Fit::string genericId = "1";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;

    fitableDiscovery.RegisterLocalFitable(details);
    fitableDiscovery.UnRegisterLocalFitable(details);
    Fit::Framework::Fitable fitable;
    fitable.genericId = genericId;
    fitable.genericVersion = "";
    fitable.fitableId = fitableId;
    fitable.fitableVersion = "";
    auto result = fitableDiscovery.GetLocalFitable(fitable);

    ASSERT_TRUE(result.empty());
}

TEST(FitableDiscoveryDefaultImplTest, should_return_fitables_when_GetLocalFitableByGenericId_given_registered_fitable)
{
    Fit::string genericId = "1";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;
    fitableDiscovery.RegisterLocalFitable(details);

    auto result = fitableDiscovery.GetLocalFitableByGenericId(genericId);

    ASSERT_THAT(result.size(), ::testing::Eq(details.size()));
    EXPECT_THAT(result, Contains(detail1));
    EXPECT_THAT(result, Contains(detail2));
}

TEST(FitableDiscoveryDefaultImplTest, should_return_all_fitables_when_GetAllLocalFitables)
{
    Fit::string genericId = "1";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;
    fitableDiscovery.RegisterLocalFitable({detail1});
    fitableDiscovery.RegisterLocalFitable({detail2});

    auto result = fitableDiscovery.GetAllLocalFitables();

    ASSERT_THAT(result.size(), ::testing::Eq(details.size()));
    EXPECT_THAT(result, Contains(detail1));
    EXPECT_THAT(result, Contains(detail2));
}

TEST(FitableDiscoveryDefaultImplTest,
    should_return_empty_fitables_when_GetAllLocalFitables_given_after_ClearAllLocalFitables)
{
    Fit::string genericId = "1";
    Fit::string fitableId = "fitable";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId(fitableId.c_str());
    FitableDetailPtrList details{detail1, detail2};

    FitableDiscoveryDefaultImpl fitableDiscovery;
    fitableDiscovery.RegisterLocalFitable({detail1});
    fitableDiscovery.RegisterLocalFitable({detail2});

    fitableDiscovery.ClearAllLocalFitables();
    auto result = fitableDiscovery.GetAllLocalFitables();

    ASSERT_THAT(result.empty(), Eq(true));
}

TEST(FitableDiscoveryDefaultImplTest,
    should_not_return_disabled_fitables_when_GetAllLocalFitables_given_after_DisableFitables)
{
    Fit::string genericId = "1";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId("1");
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId("2");

    FitableDiscoveryDefaultImpl fitableDiscovery;
    fitableDiscovery.RegisterLocalFitable({detail1});
    fitableDiscovery.RegisterLocalFitable({detail2});

    auto disabledResult = fitableDiscovery.DisableFitables(genericId, {"1", "2"});
    auto result = fitableDiscovery.GetAllLocalFitables();

    ASSERT_THAT(result.empty(), Eq(true));
    ASSERT_THAT(disabledResult.size(), Eq(2));
    EXPECT_THAT(disabledResult, Contains(detail1));
    EXPECT_THAT(disabledResult, Contains(detail2));
}

TEST(FitableDiscoveryDefaultImplTest,
    should_return_enabled_fitables_when_GetAllLocalFitables_given_after_EnableFitables)
{
    Fit::string genericId = "1";
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId(genericId.c_str()).SetFitableId("1");
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId(genericId.c_str()).SetFitableId("2");

    FitableDiscoveryDefaultImpl fitableDiscovery;
    fitableDiscovery.RegisterLocalFitable({detail1});
    fitableDiscovery.RegisterLocalFitable({detail2});

    fitableDiscovery.DisableFitables(genericId, {"1", "2"});
    auto enabledResult = fitableDiscovery.EnableFitables(genericId, {"1", "2"});
    auto result = fitableDiscovery.GetAllLocalFitables();

    ASSERT_THAT(result.size(), Eq(2));
    EXPECT_THAT(result, Contains(detail1));
    EXPECT_THAT(result, Contains(detail2));
    ASSERT_THAT(enabledResult.size(), Eq(2));
    EXPECT_THAT(enabledResult, Contains(detail1));
    EXPECT_THAT(enabledResult, Contains(detail2));
}