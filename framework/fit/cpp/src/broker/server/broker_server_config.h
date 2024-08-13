/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : server 的配置
 * Author       : wangpanbo
 * Date:        : 2024/08/06
 */
#ifndef BROKER_SERVER_CONFIG_H
#define BROKER_SERVER_CONFIG_H
#include <atomic>
namespace Fit {
class BrokerServerConfig {
public:
    static BrokerServerConfig* Instance() {
        static BrokerServerConfig* config = new BrokerServerConfig();
        return config;
    }

    void SetIsEnableAccessToken(bool isEnable)
    {
        isEnableAccessToken.store(isEnable);
    }

    bool IsEnableAccessToken()
    {
        return isEnableAccessToken.load();
    }
private:
    BrokerServerConfig() = default;
    ~BrokerServerConfig() = default;

private:
    std::atomic<bool> isEnableAccessToken {false};
};
}
#endif
