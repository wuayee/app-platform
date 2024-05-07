/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_RESOURCE_MANAGER_H
#define DATABUS_RESOURCE_MANAGER_H

#include <deque>
#include <unordered_map>
#include <unordered_set>
#include <memory>

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
    std::tuple<int32_t, Common::ErrorType> HandleApplyMemory(int32_t socketFd, uint64_t memorySize);
    std::tuple<bool, uint64_t, Common::ErrorType> HandleApplyPermission(int32_t socketFd,
                                                                        DataBus::Common::PermissionType permissionType,
                                                                        int32_t sharedMemoryId);
    bool HandleReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                 int32_t sharedMemoryId);
    std::vector<std::tuple<int32_t, uint64_t>> ProcessWaitingPermitRequests(int32_t sharedMemoryId);

    // SharedMemoryInfo属性获取方法集合
    int32_t GetMemoryApplicant(int32_t sharedMemoryId);
    uint64_t GetMemorySize(int32_t sharedMemoryId);
    int32_t GetReadingRefCnt(int32_t sharedMemoryId);
    int32_t GetWritingRefCnt(int32_t sharedMemoryId);
    time_t GetLastUsedTime(int32_t  sharedMemoryId);
    std::unordered_set<int32_t>& GetReadingMemoryBlocks(int32_t socketFd);
    std::unordered_set<int32_t>& GetWritingMemoryBlocks(int32_t socketFd);
    int32_t GetPermissionStatus(int32_t sharedMemoryId);
    std::deque<WaitingPermitRequest>& GetWaitingPermitRequests(int32_t sharedMemoryId);

    void GenerateReport(std::stringstream& reportStream) const;
private:
    Runtime::ReportCollector<ResourceManager> reportCollector_{"ResourceManager", *this};

    // 0644: 所有者有读写权限，所属组有只读权限，其他用户有只读权限。
    static constexpr int32_t SHARED_MEMORY_ACCESS_PERMISSION = 0644;

    static bool RemoveDirectory(const std::string& directory);
    static void CreateDirectory(const std::string& directory);
    static int32_t recreateSharedMemoryBlock(key_t sharedMemoryKey, uint64_t memorySize);
    Common::ErrorType PreCheckPermissionCommon(DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    Common::ErrorType CheckApplyPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                              int32_t sharedMemoryId);
    void GrantPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);
    Common::PermissionType CheckPermissionOwnership(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                                    int32_t sharedMemoryId);
    void ReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType, int32_t sharedMemoryId);

    int32_t IncrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t IncrementWritingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementWritingRefCnt(int32_t sharedMemoryId);
    void UpdateLastUsedTime(int32_t sharedMemoryId);

    std::unordered_map<int32_t, std::unique_ptr<SharedMemoryInfo>> sharedMemoryIdToInfo_; // 内存块信息记录
    std::unordered_map<int32_t, std::unordered_set<int32_t>> readingMemoryBlocks_; // 客户端正在读取的内存块
    std::unordered_map<int32_t, std::unordered_set<int32_t>> writingMemoryBlocks_; // 客户端正在写入的内存块

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
