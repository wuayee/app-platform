/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#ifndef DATABUS_RESOURCE_MANAGER_H
#define DATABUS_RESOURCE_MANAGER_H

#include <deque>
#include <fstream>
#include <memory>
#include <unordered_map>
#include <unordered_set>

#include "ApplyPermissionRequest.h"
#include "ApplyPermissionResponse.h"
#include "config/DataBusConfig.h"
#include "MemoryMetadata.h"
#include "PermissionHeld.h"
#include "SharedMemoryInfo.h"
#include "utils/FileUtils.h"
#include "TaskLoop.h"
#include "WaitingPermitRequest.h"
#include "fbs/common_generated.h"
#include "report/ReportCollector.h"

namespace DataBus {
namespace Resource {

const std::string LOG_PATH = DataBus::Common::FileUtils::GetDataBusDirectory() + "logs/malloc_log.bin";

class ResourceManager {
public:
    void Init();
    explicit ResourceManager(const Runtime::Config& config, std::shared_ptr<Task::TaskLoop>& taskLoopPtr);
    ~ResourceManager();
    std::tuple<int32_t, Common::ErrorType> HandleApplyMemory(int32_t socketFd, const std::string& objectKey,
                                                             uint64_t memorySize);
    ApplyPermissionResponse HandleApplyPermission(const ApplyPermissionRequest& request);
    bool HandleReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                 int32_t sharedMemoryId);
    std::vector<ApplyPermissionResponse> ProcessWaitingPermitRequests(int32_t sharedMemoryId);
    bool HandleReleaseMemory(int32_t sharedMemoryId);
    bool ProcessPendingReleaseMemory(int32_t sharedMemoryId);
    std::vector<PermissionHeld> GetPermissionsHeld(int32_t socketFd);
    void RemoveClientFromWaitingQueue(int32_t socketFd);
    void CleanupExpiredMemory();

    uint64_t GetCurMallocSize() const;
    int32_t GetMemoryId(const std::string& objectKey);
    bool IsZeroMemory(const std::string& objectKey);
    void AddZeroMemoryUserData(const std::string& objectKey, const std::shared_ptr<UserData>& userDataPtr);
    const std::shared_ptr<UserData>& GetZeroMemoryUserData(const std::string& objectKey);
    void RemoveZeroMemoryUserData(const std::string& objectKey);

    // SharedMemoryInfo属性获取方法集合
    int32_t GetMemoryApplicant(int32_t sharedMemoryId);
    uint64_t GetMemorySize(int32_t sharedMemoryId);
    int32_t GetReadingRefCnt(int32_t sharedMemoryId);
    int32_t GetWritingRefCnt(int32_t sharedMemoryId);
    std::chrono::system_clock::time_point GetLastUsedTime(int32_t sharedMemoryId);
    const std::shared_ptr<UserData>& GetUserData(int32_t sharedMemoryId);
    bool IsPendingRelease(int32_t sharedMemoryId);
    std::chrono::system_clock::time_point GetExpiryTime(int32_t sharedMemoryId);
    const std::unordered_set<int32_t>& GetReadingMemoryBlocks(int32_t socketFd);
    const std::unordered_set<int32_t>& GetWritingMemoryBlocks(int32_t socketFd);
    int32_t GetPermissionStatus(int32_t sharedMemoryId);
    const std::deque<WaitingPermitRequest>& GetWaitingPermitRequests(int32_t sharedMemoryId);
    const std::unordered_set<int32_t>& GetWaitingPermitMemoryBlocks(int32_t socketFd);

    MemoryMetadata GetMemoryMetadata(int32_t sharedMemoryId);

    void GenerateReport(std::stringstream& reportStream) const;
private:
    Runtime::ReportCollector<ResourceManager> reportCollector_{"ResourceManager", *this};

    // 0666: 允许所有者、组成员和其他用户拥有读写权限。
    static constexpr int32_t SHARED_MEMORY_ACCESS_PERMISSION = 0666;

    void AppendLog(int32_t sharedMemoryId, bool released);
    static void CleanupMemory();
    static int32_t RecreateSharedMemoryBlock(key_t sharedMemoryKey, uint64_t memorySize);
    Common::ErrorType PreCheckPermissionCommon(DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    Common::ErrorType CheckApplyPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                              int32_t sharedMemoryId);
    void GrantPermission(const ApplyPermissionRequest& request);
    Common::PermissionType CheckPermissionOwnership(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                                    int32_t sharedMemoryId);
    void ReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    void MarkPendingRelease(int32_t sharedMemoryId);
    bool ReleaseMemory(int32_t sharedMemoryId);
    void CleanupWaitingPermitRequests(int32_t sharedMemoryId);
    void RemoveObjectKey(int32_t sharedMemoryId);
    void StartMemorySweep(int32_t scheduleInterval);
    void AddMemoryCleanupTask();

    int32_t IncrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t IncrementWritingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementWritingRefCnt(int32_t sharedMemoryId);
    void UpdateLastUsedTime(int32_t sharedMemoryId);
    void UpdateExpiryTime(int32_t sharedMemoryId);

    const Runtime::Config& config_;
    std::ofstream logStream_;
    uint64_t curMallocSize_; // 当前已分配内存大小
    // TD: 把过期内存清扫线程抽离到资源管理模块之外
    std::atomic<bool> stopMemorySweepThread_; // 是否停止过期内存清扫线程
    std::thread memorySweepThread_; // 过期内存扫描线程
    std::unordered_map<int32_t, std::unique_ptr<SharedMemoryInfo>> sharedMemoryIdToInfo_; // 内存块信息记录
    std::unordered_map<int32_t, std::unordered_set<int32_t>> readingMemoryBlocks_; // 客户端正在读取的内存块
    std::unordered_map<int32_t, std::unordered_set<int32_t>> writingMemoryBlocks_; // 客户端正在写入的内存块
    std::unordered_map<std::string, int32_t> keyToSharedMemoryId_; // 客户端自定义key

    /*
     * 大小为0的内存块的用户自定义元数据
     * 键：客户端自定义key
     * 值：用户自定义元数据
     */
    std::unordered_map<std::string, std::shared_ptr<UserData>> zeroMemoryUserData_;
    /*
     * 内存块当前读写状态。
     * 如果值等于0: 当前没有任何读写操作。
     * 如果值大于0：当前仅存在读操作。值代表多读操作的计数。
     * 如果值等于-1：当前仅存在唯一的写操作。
     */
    std::unordered_map<int32_t, int32_t> permissionStatus_;
    /*
     * 权限申请等待队列
     * 键：共享内存块ID
     * 值：内存块等待的授权请求
     */
    std::unordered_map<int32_t, std::deque<WaitingPermitRequest>> waitingPermitRequestQueues_;
    /*
     * 客户端正在等待授权的共享内存块
     * 键：客户端ID
     * 值：客户端等待授权的共享内存块ID集合
     */
    std::unordered_map<int32_t, std::unordered_set<int32_t>> waitingPermitMemoryBlocks_;
    std::shared_ptr<Task::TaskLoop> taskLoopPtr_;
};
}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_RESOURCE_MANAGER_H
