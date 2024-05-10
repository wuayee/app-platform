/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/08/03
 * Notes:       :
 */
#include <src/plugin/plugin_manager_impl.hpp>
#include <fit/internal/plugin/plugin.hpp>
#include <fit/stl/vector.hpp>
#include <functional>
#include "gtest/gtest.h"
#include "gmock/gmock.h"
using namespace Fit;
using namespace Fit::Plugin;
class PluginManagerImplTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }
    void TearDown() override {}
};

TEST_F(PluginManagerImplTest, should_return_void_when_observer_user_plugin_given_callback)
{
    // given
    std::function<void(const Fit::vector<Fit::Plugin::Plugin*> &plugin)> \
        func = [](const Fit::vector<Fit::Plugin::Plugin*> &plugin) {};
    Fit::vector<Fit::Plugin::Plugin*> plugins;
    // when
    PluginManagerImpl pluginManager(nullptr);
    pluginManager.ObserveUserPluginsStarted(func);
    // then
}
