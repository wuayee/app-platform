/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : 心跳客户端服务接口
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef ClientService_H
#define ClientService_H

#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/mutex.hpp>
#include "fit/internal/heartbeat/heartbeat_entity.hpp"

namespace Fit {
namespace Heartbeat {
namespace Client {

using BeatStatusInfoSet = vector<BeatStatusInfo>;
using SceneBeatStatusInfoSet = map<SceneType, vector<BeatStatusInfo>>;

class ClientService {
public:
    explicit ClientService(string);
    ~ClientService() = default;

    int32_t Online(const BeatInfo &);
    int32_t Offline(const BeatInfo &);

    void Heartbeat();

protected:
    void UpdateHeartbeatStatus(BeatStatusInfo &, HeartbeatStatus);
    void Notify(const BeatInfo &, HeartbeatStatus);
    void Leave(const BeatInfo &);
    int32_t Remove(const BeatInfo &, bool &);

private:
    mutex mt_ {};
    SceneBeatStatusInfoSet sceneBeatInfo_ {};
    string id_ {};
};

}  // namespace Client
}  // namespace Heartbeat
}  // namespace Fit
#endif  // ClientService_H
