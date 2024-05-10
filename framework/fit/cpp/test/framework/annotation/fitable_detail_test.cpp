/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/22
 * Notes:       :
 */

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;
using namespace ::testing;

TEST(FitableDetailTest, should_get_setted_info_when_get_given_fitable_detail)
{
    FitableDetail detail(nullptr);
    detail.SetGenericId("1")
        .SetFitableId("fitable")
        .SetFitableVersion("v1")
        .SetType(FitableType::MAIN);

    EXPECT_EQ(detail.GetGenericId() == Fit::string("1"), true);
    EXPECT_EQ(detail.GetFitableId() == Fit::string("fitable"), true);
    EXPECT_EQ(detail.GetFitableVersion() == Fit::string("v1"), true);
    EXPECT_THAT(detail.GetType(), Eq(FitableType::MAIN));
    EXPECT_THAT(detail.GetFunctionProxy(), IsNull());
}

TEST(FitableDetailTest, should_get_moved_info_when_get_given_rvalue)
{
    FitableDetail temp(nullptr);
    temp.SetGenericId("1")
        .SetFitableId("fitable")
        .SetFitableVersion("v1")
        .SetType(FitableType::MAIN);

    FitableDetail detail(nullptr);
    detail = std::move(temp);

    EXPECT_EQ(detail.GetGenericId() == Fit::string("1"), true);
    EXPECT_EQ(detail.GetFitableId() == Fit::string("fitable"), true);
    EXPECT_EQ(detail.GetFitableVersion() == Fit::string("v1"), true);
    EXPECT_THAT(detail.GetType(), Eq(FitableType::MAIN));
    EXPECT_THAT(detail.GetFunctionProxy(), IsNull());
}

TEST(FitableDetailTest, should_do_nothing_when_get_given_rvalue_self)
{
    FitableDetail detail(nullptr);
    detail.SetGenericId("1")
        .SetFitableId("fitable")
        .SetFitableVersion("v1")
        .SetType(FitableType::MAIN);
    detail = std::move(detail);

    EXPECT_EQ(detail.GetGenericId() == Fit::string("1"), true);
    EXPECT_EQ(detail.GetFitableId() == Fit::string("fitable"), true);
    EXPECT_EQ(detail.GetFitableVersion() == Fit::string("v1"), true);
    EXPECT_THAT(detail.GetType(), Eq(FitableType::MAIN));
    EXPECT_THAT(detail.GetFunctionProxy(), IsNull());
}
