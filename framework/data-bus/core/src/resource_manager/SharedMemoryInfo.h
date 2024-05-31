/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/

#ifndef DATABUS_SHARED_MEMORY_INFO_H
#define DATABUS_SHARED_MEMORY_INFO_H

#include "UserData.h"

namespace DataBus {
namespace Resource {

// 共享内存块信息结构体
struct SharedMemoryInfo {
    // 禁用默认构造器和运算符重载
    SharedMemoryInfo() = delete;
    SharedMemoryInfo(const SharedMemoryInfo&) = delete;
    SharedMemoryInfo& operator=(const SharedMemoryInfo&) = delete;

    SharedMemoryInfo(int32_t mApplicant, unsigned long mSize, std::chrono::system_clock::time_point initialUsedTime,
                     std::chrono::system_clock::time_point initialExpiryTime)
        : applicant_(mApplicant), memorySize_(mSize), readingRefCnt_(0), writingRefCnt_(0),
        lastUsedTime_(initialUsedTime), userData_(), pendingRelease_(false), expiryTime_(initialExpiryTime) {}

    int32_t applicant_; // 内存块申请客户端
    uint32_t memorySize_; // 内存块大小
    int32_t readingRefCnt_; // 内存块读取引用计数
    int32_t writingRefCnt_; // 内存块写入引用计数
    std::chrono::system_clock::time_point lastUsedTime_; // 内存块最后使用时间戳
    std::shared_ptr<UserData> userData_; // 用户自定义元数据
    bool pendingRelease_; // 内存块待释放状态
    std::chrono::system_clock::time_point expiryTime_; // 内存块过期时间
};

}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_SHARED_MEMORY_INFO_H
