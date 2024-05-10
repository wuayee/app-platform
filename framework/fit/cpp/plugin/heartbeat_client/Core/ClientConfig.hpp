/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef CLIENTCONFIG_HPP
#define CLIENTCONFIG_HPP

namespace Fit {
namespace Heartbeat {
namespace Client {
// 心跳周期
static const auto HEARTBEAT_WAIT_TIME_MS = 1000;
// 连续稳定周期数，稳定后才发送状态通知
static const auto HEARTBEAT_STEADY_COUNT = 3;
}  // namespace Client
}  // namespace Heartbeat
}  // namespace Fit

#endif // CLIENTCONFIG_HPP