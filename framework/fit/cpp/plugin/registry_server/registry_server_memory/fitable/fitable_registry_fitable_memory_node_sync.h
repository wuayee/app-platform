/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/08
 * Notes:       :
 */

#ifndef FITABLE_REGISTRY_FITABLE_MEMORY_NODE_SYNC_H
#define FITABLE_REGISTRY_FITABLE_MEMORY_NODE_SYNC_H

#include <fit/stl/mutex.hpp>
#include <memory>
#include <fit/internal/util/thread/fit_thread_pool.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/internal/util/thread/fit_serial_executor.h>
#include <fit/internal/registry/repository/fitable_registry_fitable_node_sync.h>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>

namespace Fit {
namespace Registry {
class FitableRegistryFitableMemoryNodeSync : public FitableRegistryFitableNodeSync {
public:
    struct ApplicationInstanceInner : public FitBase {
        Fit::vector<::fit::hakuna::kernel::registry::shared::Worker> workers;
        ::fit::hakuna::kernel::registry::shared::Application application;
        /**
         * protobuf-----0
         * json------1
         */
        Fit::vector<int32_t> formats;
    };
    struct FitableInstanceInner : public FitBase {
        Fit::vector<ApplicationInstanceInner> applicationInstances;
        ::fit::hakuna::kernel::shared::Fitable fitable;
        Fit::vector<Fit::string> aliases;
        Fit::vector<Fit::string> tags;
        Fit::map<Fit::string, Fit::string> extensions;
    };
    struct SyncFitableInstanceInner : public FitBase {
        // 操作类型0 增加；1删除
        int32_t operateType;
        FitableInstanceInner fitableInstanceInner;
    };
    using SyncFitableInstanceInnerPtr = std::shared_ptr<SyncFitableInstanceInner>;
public:
        FitableRegistryFitableMemoryNodeSync();
        ~FitableRegistryFitableMemoryNodeSync() override;
        bool Start() override;
        bool Stop() override;
        int32_t Add(const db_service_set& serviceSet) override;
        int32_t Remove(const db_service_set& serviceSet) override;
        void TimingSynchronizationService();
private:
    // 将add和remove两种操作合并处理
    int32_t Sync(const db_service_set& serviceSet, int32_t operatorType);
    // 将同步逻辑封装为异步任务
    int32_t SyncToOtherRegistryFitable(std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr);
    // 调用同步接口
    int32_t SyncFitable(
        std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr,
        const RegistryInfo::FlatAddress& address);
    uint64_t GetIdByAddress(const string& workerId) const;
private:

    Fit::mutex fitableInstancesMutex_ {};
    Fit::vector<SyncFitableInstanceInnerPtr> fitableInstances_ {};

    std::shared_ptr<Fit::timer> timeoutTimer_ {nullptr}; // 自定义单线程timer
    std::shared_ptr<Fit::Thread::thread_pool> syncWorker_ {nullptr}; // 自定义单线程threadpool
    Fit::vector<std::shared_ptr<Fit::Thread::serial_executor>> serialExecutorSet_ {};
    Fit::timer::timer_handle_t taskId_ {Fit::timer::INVALID_TASK_ID};
};
}
}

#endif