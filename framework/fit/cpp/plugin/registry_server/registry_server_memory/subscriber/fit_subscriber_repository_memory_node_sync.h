/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/15
 * Notes:       :
 */
#ifndef FIT_SUBSCRIBER_REPOSITORY_MEMORY_NODE_SYNC_H
#define  FIT_SUBSCRIBER_REPOSITORY_MEMORY_NODE_SYNC_H

#include <fit/stl/mutex.hpp>
#include <fit/memory/fit_base.hpp>
#include <fit/internal/util/thread/fit_thread_pool.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/internal/util/thread/fit_serial_executor.h>
#include <fit/internal/registry/repository/fit_subscription_node_sync.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_sync_subscription_fit_service/1.0.0/cplusplus/syncSubscriptionFitService.hpp>

namespace Fit {
namespace Registry {
class FitSubscriberRepositoryMemoryNodeSync : public FitSubscriptionNodeSync {
public:
    struct SyncSubscriptionServiceInner : public FitBase {
        // 订阅的服务
        ::fit::registry::Fitable fitable {};
        // 监听者地址
        ::fit::registry::Address listenerAddress {};
        // 操作类型0增加，1删除
        int32_t operateType {};
        Fit::string callbackFitId {};
    };
public:
    FitSubscriberRepositoryMemoryNodeSync(int32_t minServicePerTime, int32_t maxIntervalPerTime);
    ~FitSubscriberRepositoryMemoryNodeSync() override;
    bool Start() override;
    bool Stop() override;
    int32_t Add(const fit_fitable_key_t &key, const listener_t &listener) override;
    int32_t Remove(const fit_fitable_key_t &key, const listener_t &listener) override;
    void TimeoutSync();
private:
    // 将add和remove两种操作合并处理
    int32_t Sync(const fit_fitable_key_t &key, const listener_t &listener, int32_t operatorType);
    // 将数据推导队列中
    int32_t PushToQueue(
        std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr);
    int32_t SyncToOtherRegistry(
        std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr,
        const RegistryInfo::FlatAddress& address);
    uint64_t GetIdByAddress(const string& workerId);
private:
    Fit::mutex syncSubscriptionServiceSetMutex_ {};
    Fit::vector<SyncSubscriptionServiceInner> syncSubscriptionServiceSet_ {};
    std::shared_ptr<Fit::timer> timeoutTimer_ {nullptr}; // 自定义单线程timer
    std::shared_ptr<Fit::Thread::thread_pool> timerWorker_ {nullptr};
    std::shared_ptr<Fit::Thread::thread_pool> syncWorker_ {nullptr}; // 自定义单线程threadpool
    Fit::vector<std::shared_ptr<Fit::Thread::serial_executor>> serialExecutorSet_ {};
    Fit::timer::timer_handle_t taskId_ {Fit::timer::INVALID_TASK_ID};
    int32_t minServicePerTime_ {}; // 满minServicePerTime个推一次
    int32_t maxIntervalPerTime_ {}; // 不满minServicePerTime个，每maxIntervalPerTime毫秒推一次，单位ms
};
}
}

#endif