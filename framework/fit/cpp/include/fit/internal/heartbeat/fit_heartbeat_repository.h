/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 心跳数据存储层接口
 * Author       : s00558940
 * Create       : 2020/10/8 16:31
 * Notes:       :
 */

#ifndef FIT_HEARTBEAT_REPOSITORY_H
#define FIT_HEARTBEAT_REPOSITORY_H

#include "fit/internal/heartbeat/heartbeat_entity.hpp"

class fit_heartbeat_repository {
public:
    fit_heartbeat_repository() = default;
    virtual ~fit_heartbeat_repository() = default;

    virtual int32_t add_beat(const Fit::Heartbeat::AddressStatusInfo &info) = 0;
    virtual int32_t modify_beat(const Fit::Heartbeat::AddressStatusInfo &info) = 0;
    virtual int32_t remove_beat(const Fit::Heartbeat::AddressBeatInfo &info) = 0;
    virtual int32_t query_beat(const Fit::Heartbeat::AddressBeatInfo &info,
    Fit::Heartbeat::AddressStatusInfo &result) = 0;
    virtual Fit::Heartbeat::AddressStatusSet query_all_beat() = 0;
    virtual FitCode get_current_time_ms(uint64_t& result) = 0;
};

using fit_heartbeat_repository_ptr = std::shared_ptr<fit_heartbeat_repository>;


class fit_heartbeat_repository_factory final {
public:
    static fit_heartbeat_repository_ptr create();
};
#endif // FIT_HEARTBEAT_REPOSITORY_H
