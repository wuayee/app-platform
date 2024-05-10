/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-05-07 18:54:25
 */

#include <fit/fit_code.h>
#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include "gtest/gtest.h"
#include "genericable/com_huawei_fit_heartbeat_offline_heartbeat/1.0.0/cplusplus/offlineHeartbeat.hpp"
#include "genericable/com_huawei_fit_heartbeat_online_heartbeat/1.0.0/cplusplus/onlineHeartbeat.hpp"

TEST(HeartbeatClientTest, should_return_true_when_online_heartbeat)
{
    fit::heartbeat::onlineHeartbeat online;
    fit::heartbeat::BeatInfo beatInfo{};
    bool* result{};

    auto ret = online(&beatInfo, &result);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}