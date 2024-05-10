/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <fit/external/framework/annotation/fitable_collector.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

TEST(FitableCollectorTest, should_pop_cached_fitable_when_pop_cache_given_fitable)
{
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId("1");
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId("2");
    FitableDetailPtrList details {detail1, detail2};

    PopFitableDetailCache();
    FitableCollector::Register(details);
    auto result = PopFitableDetailCache();
    auto resultAfterPop = PopFitableDetailCache();

    ASSERT_THAT(result.size(), ::testing::Eq(details.size()));
    EXPECT_TRUE(std::find_if(result.begin(), result.end(),
        [&detail1](const FitableDetailPtr &item) { return item.get() == detail1.get(); }) != result.end());
    EXPECT_TRUE(std::find_if(result.begin(), result.end(),
        [&detail2](const FitableDetailPtr &item) { return item.get() == detail2.get(); }) != result.end());
    EXPECT_TRUE(resultAfterPop.empty());
}

TEST(FitableCollectorTest, should_register_to_setted_receiver_when_pop_cache_given_receiver_and_register_data)
{
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId("1");
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId("2");
    FitableDetailPtrList details {detail1, detail2};

    FitableDetailPtrList registeredDetails;
    FitableDetailReceiver receiver;
    receiver.Register = [&registeredDetails](const FitableDetailPtrList &data) { registeredDetails = data; };
    receiver.UnRegister = [&registeredDetails](const FitableDetailPtrList &data) {
        auto removeIter = std::remove_if(registeredDetails.begin(), registeredDetails.end(),
            [&data](const FitableDetailPtr &existItem) {
                return std::find_if(data.begin(), data.end(),
                    [&existItem](const FitableDetailPtr &item) { return item.get() == existItem.get(); }) != data.end();
            });
        registeredDetails.erase(removeIter, registeredDetails.end());
    };

    auto old = FitableDetailFlowTo(&receiver);
    FitableCollector::Register(details);
    FitableDetailFlowTo(old);
    ASSERT_THAT(registeredDetails.size(), ::testing::Eq(details.size()));

    EXPECT_TRUE(std::find_if(registeredDetails.begin(), registeredDetails.end(),
        [&detail1](const FitableDetailPtr &item) { return item.get() == detail1.get(); }) != registeredDetails.end());
    EXPECT_TRUE(std::find_if(registeredDetails.begin(), registeredDetails.end(),
        [&detail2](const FitableDetailPtr &item) { return item.get() == detail2.get(); }) != registeredDetails.end());
}

TEST(FitableCollectorTest, should_return_empty_when_pop_cache_given_receiver_and_register_data)
{
    auto detail1 = std::make_shared<FitableDetail>(nullptr);
    detail1->SetGenericId("1");
    auto detail2 = std::make_shared<FitableDetail>(nullptr);
    detail2->SetGenericId("2");
    FitableDetailPtrList details {detail1, detail2};

    FitableDetailReceiver receiver;
    receiver.Register = [](const FitableDetailPtrList &data) {};
    receiver.UnRegister = [](const FitableDetailPtrList &data) {};

    auto old = FitableDetailFlowTo(&receiver);
    FitableCollector::Register(details);
    auto result = PopFitableDetailCache();
    FitableDetailFlowTo(old);

    EXPECT_TRUE(result.empty());
}