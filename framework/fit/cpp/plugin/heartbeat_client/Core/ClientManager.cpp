/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2020/09/23
 * Notes:       :
 */

#include "ClientManager.hpp"

#include "ClientConfig.hpp"
#include "fit/internal/fit_system_property_utils.h"

namespace Fit {
namespace Heartbeat {
namespace Client {

ClientManagerPtr ClientManager::instance_;

ClientManagerPtr &ClientManager::Instance()
{
    static std::once_flag once;
    std::call_once(once, [] { instance_ = new ClientManager(); });
    return instance_;
}

void ClientManager::Destroy()
{
    if (instance_) {
        delete instance_;
        instance_ = nullptr;
    }
}

ClientManager::ClientManager()
{
    Init();
}

ClientManager::~ClientManager()
{
    Uninit();
}

void ClientManager::Init()
{
    auto id = FitSystemPropertyUtils::Get("fit_worker_id");
    heartbeatService_ = std::make_shared<ClientService>(id);

    isExit_ = false;
    checkThread_ = std::thread([this]() {
        while (!isExit_) {
            std::this_thread::sleep_for(std::chrono::milliseconds(HEARTBEAT_WAIT_TIME_MS));
            heartbeatService_->Heartbeat();
        }
    });
}

void ClientManager::Uninit()
{
    isExit_ = true;
    if (checkThread_.joinable()) {
        checkThread_.join();
    }
}

ClientService &ClientManager::GetHeartbeatService()
{
    return *heartbeatService_;
}
}  // namespace Client
}  // namespace Heartbeat
}  // namespace Fit