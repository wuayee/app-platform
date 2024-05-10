/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef FIT_HEARTBEAT_MANAGER_H
#define FIT_HEARTBEAT_MANAGER_H

#include <fit/stl/vector.hpp>
#include "ClientService.hpp"

#include <memory>
#include <thread>

namespace Fit {
namespace Heartbeat {
namespace Client {
using ClientService_ptr = std::shared_ptr<ClientService>;

class ClientManager;
using ClientManagerPtr = ClientManager *;

class ClientManager {
public:
    ~ClientManager();
    void Init();
    void Uninit();

    static ClientManagerPtr &Instance();
    static void Destroy();
    ClientService &GetHeartbeatService();

    ClientManager(ClientManager &) = delete;
    ClientManager &operator=(ClientManager &) = delete;

private:
    ClientManager();
    ClientService_ptr heartbeatService_{};
    std::thread checkThread_;
    volatile bool isExit_{false};

    static ClientManagerPtr instance_;
};
}  // namespace Client
}  // namespace Heartbeat
}  // namespace Fit

#endif  // FIT_HEARTBEAT_MANAGER_H
