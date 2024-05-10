/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/22
 * Notes:       :
 */

#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/internal/framework/formatter/formatter_collector_inner.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;
using namespace Fit::Framework::Formatter;
using namespace ::testing;

TEST(FormatterCollectorTest, should_pop_cached_formatter_when_pop_cache_given_fitable)
{
    auto formatter = std::make_shared<FormatterMeta>();
    formatter->SetGenericId("1");
    formatter->SetFitableType(Annotation::FitableType::MAIN);

    PopFormatterMetaCache();
    FormatterCollector::Register({formatter});
    auto result = PopFormatterMetaCache();
    auto resultAfterPop = PopFormatterMetaCache();

    ASSERT_THAT(result.size(), Eq(1));
    EXPECT_THAT(result, Contains(formatter));
    EXPECT_TRUE(resultAfterPop.empty());
}

TEST(FormatterCollectorTest, should_register_to_setted_receiver_when_pop_cache_given_receiver_and_register_data)
{
    auto formatter = std::make_shared<FormatterMeta>();
    formatter->SetGenericId("1");
    FormatterMetaPtrList formatters = {formatter};

    FormatterMetaPtrList registeredData;
    FormatterMetaReceiver receiver;
    receiver.Register = [&registeredData](const FormatterMetaPtrList &data) { registeredData = data; };
    receiver.UnRegister = [&registeredData](const FormatterMetaPtrList &data) {
        auto removeIter = std::remove_if(registeredData.begin(), registeredData.end(),
            [&data](const FormatterMetaPtr &existItem) {
                return std::find_if(data.begin(), data.end(),
                    [&existItem](const FormatterMetaPtr &item) { return item.get() == existItem.get(); }) != data.end();
            });
        registeredData.erase(removeIter, registeredData.end());
    };

    auto old = FormatterMetaFlowTo(&receiver);
    FormatterCollector::Register(formatters);
    FormatterMetaFlowTo(old);

    ASSERT_THAT(registeredData.size(), ::testing::Eq(formatters.size()));
    EXPECT_THAT(registeredData, Contains(formatter));
}

TEST(FormatterCollectorTest, should_return_empty_when_pop_cache_given_receiver_and_register_data)
{
    auto formatter = std::make_shared<FormatterMeta>();
    formatter->SetGenericId("1");
    FormatterMetaPtrList formatters = {formatter};

    FormatterMetaReceiver receiver;
    receiver.Register = [](const FormatterMetaPtrList &data) {};
    receiver.UnRegister = [](const FormatterMetaPtrList &data) {};

    auto old = FormatterMetaFlowTo(&receiver);
    FormatterCollector::Register(formatters);
    auto result = PopFormatterMetaCache();
    FormatterMetaFlowTo(old);

    EXPECT_TRUE(result.empty());
}

TEST(FormatterCollectorTest, should_return_empty_when_pop_cache_given_after_unregister_data)
{
    auto formatter = std::make_shared<FormatterMeta>();
    formatter->SetGenericId("1");
    FormatterMetaPtrList formatters = {formatter};

    FormatterMetaPtrList registeredData;
    FormatterMetaReceiver receiver;
    receiver.Register = [&registeredData](const FormatterMetaPtrList &data) { registeredData = data; };
    receiver.UnRegister = [&registeredData](const FormatterMetaPtrList &data) {
        auto removeIter = std::remove_if(registeredData.begin(), registeredData.end(),
            [&data](const FormatterMetaPtr &existItem) {
                return std::find_if(data.begin(), data.end(),
                    [&existItem](const FormatterMetaPtr &item) { return item.get() == existItem.get(); }) != data.end();
            });
        registeredData.erase(removeIter, registeredData.end());
    };

    auto old = FormatterMetaFlowTo(&receiver);
    FormatterCollector::Register(formatters);
    FormatterCollector::UnRegister(formatters);
    FormatterMetaFlowTo(old);

    EXPECT_TRUE(registeredData.empty());
}