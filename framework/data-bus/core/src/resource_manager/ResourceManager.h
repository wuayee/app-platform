/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_RESOURCE_MANAGER_H
#define DATABUS_RESOURCE_MANAGER_H

#include <deque>
#include <unordered_map>
#include <unordered_set>
#include <memory>

#include "ApplyPermissionResponse.h"
#include "SharedMemoryInfo.h"
#include "WaitingPermitRequest.h"
#include "fbs/common_generated.h"
#include "report/ReportCollector.h"

namespace DataBus {
namespace Resource {

class ResourceManager {
public:
    static void Init();
    ResourceManager();
    ~ResourceManager() = default;
    std::tuple<int32_t, Common::ErrorType> HandleApplyMemory(int32_t socketFd, const std::string& objectKey,
                                                             uint64_t memorySize);
    ApplyPermissionResponse HandleApplyPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                                  int32_t sharedMemoryId);
    bool HandleReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                 int32_t sharedMemoryId);
    std::vector<ApplyPermissionResponse> ProcessWaitingPermitRequests(int32_t sharedMemoryId);
    bool HandleReleaseMemory(int32_t sharedMemoryId);
    bool ProcessPendingReleaseMemory(int32_t sharedMemoryId);

    int32_t GetMemoryId(const std::string& objectKey);

    // SharedMemoryInfo属性获取方法集合
    int32_t GetMemoryApplicant(int32_t sharedMemoryId);
    uint64_t GetMemorySize(int32_t sharedMemoryId);
    int32_t GetReadingRefCnt(int32_t sharedMemoryId);
    int32_t GetWritingRefCnt(int32_t sharedMemoryId);
    time_t GetLastUsedTime(int32_t  sharedMemoryId);
    bool IsPendingRelease(int32_t sharedMemoryId);
    const std::unordered_set<int32_t>& GetReadingMemoryBlocks(int32_t socketFd);
    const std::unordered_set<int32_t>& GetWritingMemoryBlocks(int32_t socketFd);
    int32_t GetPermissionStatus(int32_t sharedMemoryId);
    const std::deque<WaitingPermitRequest>& GetWaitingPermitRequests(int32_t sharedMemoryId);

    void GenerateReport(std::stringstream& reportStream) const;
private:
    Runtime::ReportCollector<ResourceManager> reportCollector_{"ResourceManager", *this};

    // 0666: 允许所有者、组成员和其他用户拥有读写权限。
    static constexpr int32_t SHARED_MEMORY_ACCESS_PERMISSION = 0666;

    static bool RemoveDirectory(const std::string& directory);
    static void CreateDirectory(const std::string& directory);
    static int32_t RecreateSharedMemoryBlock(key_t sharedMemoryKey, uint64_t memorySize);
    Common::ErrorType PreCheckPermissionCommon(DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    Common::ErrorType CheckApplyPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                              int32_t sharedMemoryId);
    void GrantPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    Common::PermissionType CheckPermissionOwnership(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                                    int32_t sharedMemoryId);
    void ReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    void MarkPendingRelease(int32_t sharedMemoryId);
    bool ReleaseMemory(int32_t sharedMemoryId);
    void RemoveObjectKey(int32_t sharedMemoryId);

    int32_t IncrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t IncrementWritingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementWritingRefCnt(int32_t sharedMemoryId);
    void UpdateLastUsedTime(int32_t sharedMemoryId);

    std::unordered_map<int32_t, std::unique_ptr<SharedMemoryInfo>> sharedMemoryIdToInfo_; // 内存块信息记录
    std::unordered_map<int32_t, std::unordered_set<int32_t>> readingMemoryBlocks_; // 客户端正在读取的内存块
    std::unordered_map<int32_t, std::unordered_set<int32_t>> writingMemoryBlocks_; // 客户端正在写入的内存块
    std::unordered_map<std::string, int32_t> keyToSharedMemoryId_; // 客户端自定义key

    /* 内存块当前读写状态。
     * 如果值等于0: 当前没有任何读写操作。
     * 如果值大于0：当前仅存在读操作。值代表多读操作的计数。
     * 如果值等于-1：当前仅存在唯一的写操作。
    */
    std::unordered_map<int32_t, int32_t> permissionStatus_;
    std::unordered_map<int32_t, std::deque<WaitingPermitRequest>> waitingPermitRequestQueues_; // 权限申请等待队列
};
}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_RESOURCE_MANAGER_H
