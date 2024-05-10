/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : runtime client
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#ifndef FIT_HEARTBEAT_CLIENT_ELE_HPP
#define FIT_HEARTBEAT_CLIENT_ELE_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>

namespace Fit {
class HeartbeatClientElement : public RuntimeElementBase {
public:
    HeartbeatClientElement();
    ~HeartbeatClientElement() override;

    bool Start() override;
    bool Stop() override;

protected:
    bool HasRegistry();
    bool HasHeartbeatClient();
    FitCode ObserveHeartbeatChanged();
    FitCode Online();
};
}
#endif // FIT_HEARTBEAT_CLIENT_ELE_HPP
