/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/

#include "ResourceManager.h"

#include <memory>

#include <sys/shm.h>
#include <sys/stat.h>

#include "FtokArgsGenerator.h"
#include "exception/databus_exception.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Resource {

ResourceManager& ResourceManager::Instance()
{
    static ResourceManager instance;
    return instance;
}

int ResourceManager::HandleApplyMemory(int32_t socketFd, uint32_t memorySize)
{
    lock_guard<mutex> lock(mutex_);
    // 获取ftok函数参数生成器单例
    auto& ftokArgsGenerator = FtokArgsGenerator::Instance();
    const std::string& pathName = ftokArgsGenerator.GetFilePath();
    const int32_t projId = ftokArgsGenerator.GetProjId();
    if (projId == 0) {
        CreateDirectory(pathName);
    }
    const key_t sharedMemoryKey = ftok(pathName.data(), projId);
    if (sharedMemoryKey == -1) {
        perror("failed to generate a shared memory key");
        throw MemoryIOException("failed to generate a shared memory key");
    }

    /* IPC_CREAT | IPC_EXCL: 仅创建新的共享内存区域。当sharedMemoryKey存在时，获得共享内存区域会失败。
     * S_IRUSR: 允许所有者阅读它。
     * S_IWUSR：允许所有者编写它。
    */
    const int32_t sharedMemoryId = shmget(sharedMemoryKey, memorySize, IPC_CREAT | IPC_EXCL | S_IRUSR | S_IWUSR);
    if (sharedMemoryId == -1) {
        perror("failed to get the shared memory Id");
        throw MemoryIOException("failed to apply memory");
    }
    // 记录共享内存块ID和共享内存块信息的对应关系
    auto const now = std::chrono::system_clock::now();
    time_t curTime = std::chrono::system_clock::to_time_t(now);
    if (curTime == -1) {
        perror("failed to get the current time");
        throw MemoryIOException("failed to get the current time");
    }
    unique_ptr<SharedMemoryInfo> sharedMemoryInfo = std::make_unique<SharedMemoryInfo>(socketFd, memorySize, curTime);
    sharedMemoryIdToInfo_[sharedMemoryId] = std::move(sharedMemoryInfo);

    return sharedMemoryId;
}

void ResourceManager::CreateDirectory(const std::string &directory)
{
    size_t pos = 0;
    std::string dir = directory;
    if (dir[dir.size() - 1] != '/') {
        dir += "/";
    }
    while ((pos = dir.find_first_of('/', pos + 1)) != std::string::npos) {
        /* S_IRUSR: 允许所有者阅读它。
         * S_IWUSR：允许所有者编写它。
        */
        mkdir(dir.substr(0, pos).c_str(), S_IRUSR | S_IWUSR);
    }
    mkdir(directory.c_str(), S_IRUSR | S_IWUSR);
}

int32_t ResourceManager::GetMemoryApplicant(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->applicant_;
}

uint32_t ResourceManager::GetMemorySize(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->memorySize_;
}

int32_t ResourceManager::GetReadingRefCnt(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt_;
}

int32_t ResourceManager::GetWritingRefCnt(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt_;
}

time_t ResourceManager::GetLastUsedTime(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->lastUsedTime_;
}

int32_t ResourceManager::IncrementReadingRefCnt(int sharedMemoryId)
{
    return ++sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt_;
}

int32_t ResourceManager::DecrementReadingRefCnt(int sharedMemoryId)
{
    return --sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt_;
}

int32_t ResourceManager::IncrementWritingRefCnt(int sharedMemoryId)
{
    return ++sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt_;
}

int32_t ResourceManager::DecrementWritingRefCnt(int sharedMemoryId)
{
    return --sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt_;
}

void ResourceManager::UpdateLastUsedTime(int sharedMemoryId)
{
    sharedMemoryIdToInfo_[sharedMemoryId]->lastUsedTime_ = time(nullptr);
}

}  // namespace Resource
}  // namespace DataBus
