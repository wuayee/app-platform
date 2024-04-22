/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/

#include "ResourceManager.h"

#include <memory>

#include <sys/shm.h>
#include <sys/stat.h>
#include <cstdio>
#include <cstring>
#include <dirent.h>
#include <unistd.h>

#include "FtokArgsGenerator.h"
#include "log/Logger.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Resource {

ResourceManager::ResourceManager()
{
    Init();
}

tuple<int32_t, ErrorType> ResourceManager::HandleApplyMemory(int32_t socketFd, uint64_t memorySize)
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
        logger.Error("[ResourceManager] Failed to generate a shared memory key: {}", strerror(errno));
        return make_tuple(-1, ErrorType::MallocFailed);
    }

    // IPC_CREAT | IPC_EXCL: 仅创建新的共享内存区域。当sharedMemoryKey存在时，获得共享内存区域会失败。
    int32_t sharedMemoryId = shmget(sharedMemoryKey, memorySize,
                                    SHARED_MEMORY_ACCESS_PERMISSION | IPC_CREAT | IPC_EXCL);
    if (sharedMemoryId == -1) {
        if (errno != EEXIST) {
            logger.Error("[ResourceManager] Failed to get the shared memory Id: {}", strerror(errno));
            return make_tuple(-1, ErrorType::MallocFailed);
        }
        logger.Info("[ResourceManager] Start recreating a new sharedMemoryId associated with "
                    "sharedMemoryKey {}", sharedMemoryKey);
        sharedMemoryId = recreateSharedMemoryBlock(sharedMemoryKey, memorySize);
        if (sharedMemoryId == -1) {
            logger.Error("[ResourceManager] Failed to recreate a shared memory block: {}",
                         strerror(errno));
            return make_tuple(-1, ErrorType::MallocFailed);
        }
        logger.Info("[ResourceManager] Recreating a new sharedMemoryId associated with sharedMemoryKey {} "
                    "succeeded", sharedMemoryKey);
    }
    // 记录共享内存块ID和共享内存块信息的对应关系
    const auto& now = std::chrono::system_clock::now();
    time_t curTime = std::chrono::system_clock::to_time_t(now);
    sharedMemoryIdToInfo_[sharedMemoryId] = std::make_unique<SharedMemoryInfo>(socketFd, memorySize, curTime);

    return make_tuple(sharedMemoryId, ErrorType::None);
}

void ResourceManager::Init()
{
    if (!RemoveDirectory(FILE_PATH_PREFIX)) {
        logger.Debug("[ResourceManager] Failed to remove the ftok directory during the ResourceManager init "
                    "phase");
    }
}

bool ResourceManager::RemoveDirectory(const std::string &directory)
{
    DIR* dp;
    if ((dp = opendir(directory.data())) != nullptr) {
        struct dirent* dirp;
        while ((dirp = readdir(dp)) != nullptr) {
            // 忽略 "." 和 ".." 目录。
            if (strcmp(".", dirp->d_name) == 0 || strcmp("..", dirp->d_name) == 0) {
                continue;
            }
            // 如果是目录，则递归调用; 否则，删除文件。
            if (dirp->d_type == DT_DIR) {
                const std::string subDirectories = (directory + "/" + dirp->d_name);
                RemoveDirectory(subDirectories);
            } else if (remove((directory + "/" + dirp->d_name).data()) != 0) {
                logger.Debug("[ResourceManager] Failed to remove the file {}: {}",
                             directory + "/" + dirp->d_name, strerror(errno));
                return false;
            }
        }
        closedir(dp);
    }
    if (rmdir(directory.data()) == -1) {
        logger.Debug("[ResourceManager] Failed to remove the directory {}: {}", directory,
                     strerror(errno));
        return false;
    }
    return true;
}

void ResourceManager::CreateDirectory(const std::string &directory)
{
    size_t pos = 0;
    std::string dir = directory;
    if (dir[dir.size() - 1] != '/') {
        dir += "/";
    }
    while ((pos = dir.find_first_of('/', pos + 1)) != std::string::npos) {
        /* S_IRWXU: 允许文件路径所有者阅读、编写、执行它。
         * S_IRWXG: 允许文件路径所属组阅读、编写、执行它。
         * S_IROTH: 允许其他所有用户阅读它。
         * S_IXOTH: 允许其他所有用户执行它。
        */
        if (mkdir(dir.substr(0, pos).c_str(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH) == -1) {
            logger.Debug("[ResourceManager] Failed to create the directory {}: {}",
                         dir.substr(0, pos), strerror(errno));
        }
    }
}

int32_t ResourceManager::recreateSharedMemoryBlock(key_t sharedMemoryKey, uint64_t memorySize)
{
    // 获取之前创建的共享内存块ID。
    int32_t sharedMemoryId = shmget(sharedMemoryKey, 0, SHARED_MEMORY_ACCESS_PERMISSION);
    if (sharedMemoryId == -1) {
        logger.Error("[ResourceManager] Failed to get the preexisting sharedMemoryId: {}",
                     strerror(errno));
        return -1;
    }
    // 删除之前创建的共享内存块。
    if (shmctl(sharedMemoryId, IPC_RMID, nullptr) == -1) {
        logger.Error("[ResourceManager] Failed to remove the preexisting shared memory block: {}",
                     strerror(errno));
        return -1;
    }
    return shmget(sharedMemoryKey, memorySize, SHARED_MEMORY_ACCESS_PERMISSION | IPC_CREAT | IPC_EXCL);
}

int32_t ResourceManager::GetMemoryApplicant(int sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->applicant_;
}

uint64_t ResourceManager::GetMemorySize(int sharedMemoryId)
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
