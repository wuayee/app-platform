/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_RESOURCE_MANAGER_H
#define DATABUS_RESOURCE_MANAGER_H

#include <unordered_map>
#include <memory>
#include <vector>
#include <mutex>

#include "SharedMemoryInfo.h"

namespace DataBus {
namespace Resource {

class ResourceManager {
public:
    ResourceManager();
    ~ResourceManager() = default;
    static void Init();
    int HandleApplyMemory(int32_t socketFd, uint32_t memorySize);

    // SharedMemoryInfo属性获取方法集合
    int32_t GetMemoryApplicant(int32_t sharedMemoryId);
    uint32_t GetMemorySize(int32_t sharedMemoryId);
    int32_t GetReadingRefCnt(int32_t sharedMemoryId);
    int32_t GetWritingRefCnt(int32_t sharedMemoryId);
    time_t GetLastUsedTime(int32_t  sharedMemoryId);

private:
    // 0644: 所有者有读写权限，所属组有只读权限，其他用户有只读权限。
    static constexpr int32_t SHARED_MEMORY_ACCESS_PERMISSION = 0644;
    static bool RemoveDirectory(const std::string& directory);
    static void CreateDirectory(const std::string& directory);
    static int32_t recreateSharedMemoryBlock(key_t sharedMemoryKey, uint32_t memorySize);

    int32_t IncrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementReadingRefCnt(int32_t sharedMemoryId);
    int32_t IncrementWritingRefCnt(int32_t sharedMemoryId);
    int32_t DecrementWritingRefCnt(int32_t sharedMemoryId);
    void UpdateLastUsedTime(int32_t sharedMemoryId);

    std::unordered_map<int32_t, std::unique_ptr<SharedMemoryInfo>> sharedMemoryIdToInfo_; // 内存块信息记录
    std::unordered_map<int32_t, std::unique_ptr<std::vector<int32_t>>> readingMemoryBlocks_; // 客户端正在读取的内存块
    std::unordered_map<int32_t, std::unique_ptr<std::vector<int32_t>>> writingMemoryBlocks_; // 客户端正在写入的内存块
    std::mutex mutex_; // 互斥锁
};
}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_RESOURCE_MANAGER_H
