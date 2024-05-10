/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-11-02 16:30:16
 */

#ifndef FIT_PLUGINS_TEST_ENVIRONMENT_H
#define FIT_PLUGINS_TEST_ENVIRONMENT_H
#include <fit/fit_code.h>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_collector.hpp>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/fit_filesystem_util.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <runtime/fit_runtime.h>
#include "gtest/gtest.h"

class FitPluginsTestEnvironment : public ::testing::Environment {
public:
    void SetUp() override
    {
        auto chRet = chdir(Fit::Util::Filesystem::GetCurrentExeDir().c_str());
        Fit::GetBrokerClient() = nullptr;
        auto ret = FitRuntimeStart("worker_config_plugins_test.json");
        ASSERT_EQ(ret, FIT_OK);
    }

    void TearDown() override
    {
        FitRuntimeStop();
    }
};

#endif
