/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/1
 * Notes:       :
 */

#include <fit/internal/framework/plugin_activator_collector_inner.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;

class PluginActivatorTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST(PluginActivatorTest, should_return_registered_activator_when_PopPluginActivatorCache_given_register_activator)
{
    int32_t startCount = 0;
    int32_t stopCount = 0;
    int32_t expectedStartCount=2;
    int32_t expectedStopCount=2;
    int32_t expectedCacheCount=2;
    PopPluginActivatorCache();
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });
    auto cache = PopPluginActivatorCache();

    for (auto& activator : cache) {
        activator->GetStart()(nullptr);
        activator->GetStop()();
    }

    EXPECT_THAT(cache.size(), ::testing::Eq(expectedCacheCount));
    EXPECT_THAT(expectedStartCount, ::testing::Eq(expectedStartCount));
    EXPECT_THAT(stopCount, ::testing::Eq(expectedStopCount));
}

TEST(PluginActivatorTest, should_return_registered_activator_when_change_receiver_given_register_activator)
{
    int32_t startCount = 0;
    int32_t stopCount = 0;
    int32_t expectedStartCount=2;
    int32_t expectedStopCount=2;
    int32_t expectedCacheCount=2;
    PluginActivatorPtrList cache;
    PluginActivatorReceiver receiver;
    receiver.Register = [&cache](const PluginActivatorPtrList& val) {
        cache.insert(cache.end(), val.begin(), val.end());
    };
    receiver.UnRegister = [&cache](const PluginActivatorPtrList& val) {
        for (auto& item : val) {
            cache.erase(std::remove_if(cache.begin(), cache.end(), [&item](const PluginActivatorPtr& target) {
                return target.get() == item.get();
            }), cache.end());
        }
    };
    auto old = PluginActivatorFlowTo(&receiver);
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });

    for (auto& activator : cache) {
        activator->GetStart()(nullptr);
        activator->GetStop()();
    }
    PluginActivatorFlowTo(old);

    EXPECT_THAT(cache.size(), ::testing::Eq(expectedCacheCount));
    EXPECT_THAT(expectedStartCount, ::testing::Eq(expectedStartCount));
    EXPECT_THAT(stopCount, ::testing::Eq(expectedStopCount));
}

TEST(PluginActivatorTest, should_clear_cache_when_UnRegister_given_register_activator_and_change_receiver)
{
    int32_t startCount = 0;
    int32_t stopCount = 0;
    int32_t expectedStartCount=2;
    int32_t expectedStopCount=2;
    int32_t expectedCacheCount=0;
    PluginActivatorPtrList cache;
    PluginActivatorReceiver receiver;
    receiver.Register = [&cache](const PluginActivatorPtrList& val) {
        cache.insert(cache.end(), val.begin(), val.end());
    };
    receiver.UnRegister = [&cache](const PluginActivatorPtrList& val) {
        for (auto& item : val) {
            cache.erase(std::remove_if(cache.begin(), cache.end(), [&item](const PluginActivatorPtr& target) {
                return target.get() == item.get();
            }), cache.end());
        }
    };
    auto old = PluginActivatorFlowTo(&receiver);
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });
    PluginActivatorRegistrar()
        .SetStart([&startCount](PluginContext* context) {
            startCount++;
            return FIT_OK;
        })
        .SetStop([&stopCount]() {
            stopCount++;
            return FIT_OK;
        });

    for (auto& activator : cache) {
        activator->GetStart()(nullptr);
        activator->GetStop()();
    }
    auto tmp = cache;
    PluginActivatorCollector::UnRegister(tmp);
    PluginActivatorFlowTo(old);

    EXPECT_THAT(cache.size(), ::testing::Eq(expectedCacheCount));
    EXPECT_THAT(expectedStartCount, ::testing::Eq(expectedStartCount));
    EXPECT_THAT(stopCount, ::testing::Eq(expectedStopCount));
}
