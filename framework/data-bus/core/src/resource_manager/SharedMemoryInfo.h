/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#ifndef DATABUS_SHARED_MEMORY_INFO_H
#define DATABUS_SHARED_MEMORY_INFO_H

#include <chrono>
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
        : applicant(mApplicant), memorySize(mSize), readingRefCnt(0), writingRefCnt(0),
        lastUsedTime(initialUsedTime), userData(), pendingRelease(false), expiryTime(initialExpiryTime) {}

    int32_t applicant; // 内存块申请客户端
    uint32_t memorySize; // 内存块大小
    int32_t readingRefCnt; // 内存块读取引用计数
    int32_t writingRefCnt; // 内存块写入引用计数
    std::chrono::system_clock::time_point lastUsedTime; // 内存块最后使用时间戳
    std::shared_ptr<UserData> userData; // 用户自定义元数据
    bool pendingRelease; // 内存块待释放状态
    std::chrono::system_clock::time_point expiryTime; // 内存块过期时间
};

}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_SHARED_MEMORY_INFO_H
