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
#include "fbs/apply_permission_message_response_generated.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Resource {

ResourceManager::ResourceManager()
{
    Init();
}

void ResourceManager::Init()
{
    if (!RemoveDirectory(FILE_PATH_PREFIX)) {
        logger.Debug("[ResourceManager] Failed to remove the ftok directory during the ResourceManager init "
                     "phase");
    }
}

tuple <int32_t, ErrorType> ResourceManager::HandleApplyMemory(int32_t socketFd, uint64_t memorySize)
{
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

tuple<bool, uint64_t, ErrorType> ResourceManager::HandleApplyPermission(int32_t socketFd,
                                                                        DataBus::Common::PermissionType permissionType,
                                                                        int32_t sharedMemoryId)
{
    ErrorType preCheckRes = CheckApplyPermission(socketFd, permissionType, sharedMemoryId);
    if (preCheckRes != ErrorType::None) {
        logger.Error("[ResourceManager] Checking for applying the permission failed");
        return make_tuple(false, 0, preCheckRes);
    }
    bool shouldGrant = permissionType == PermissionType::Read ? permissionStatus_[sharedMemoryId] >= 0 :
                       permissionStatus_[sharedMemoryId] == 0;
    if (shouldGrant) {
        // 当前内存块没有任何阻塞读写操作, 无需等待，直接授权
        GrantPermission(socketFd, permissionType, sharedMemoryId);
        return make_tuple(true, GetMemorySize(sharedMemoryId), ErrorType::None);
    }
    // 否则，将权限请求加入等待队列
    logger.Info("[ResourceManager] Client {}'s ApplyPermission request is being queued for "
                "the shared memory block {}", socketFd, sharedMemoryId);
    waitingPermitRequestQueues_[sharedMemoryId].emplace_back(socketFd, permissionType);
    return make_tuple(false, GetMemorySize(sharedMemoryId), ErrorType::None);
}

bool ResourceManager::HandleReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                              int32_t sharedMemoryId)
{
    if (PreCheckPermissionCommon(permissionType, sharedMemoryId) != ErrorType::None) {
        logger.Error("[ResourceManager] PreChecking for releasing the permission failed");
        return false;
    }
    PermissionType actualPermissionType = CheckPermissionOwnership(socketFd, permissionType, sharedMemoryId);
    if (actualPermissionType == PermissionType::None) {
        logger.Error("[ResourceManager] Checking the permission ownership for releasing the permission failed");
        return false;
    }
    ReleasePermission(socketFd, actualPermissionType, sharedMemoryId);
    return true;
}

vector <tuple<int32_t, uint64_t>> ResourceManager::ProcessWaitingPermitRequests(int32_t sharedMemoryId)
{
    // 如果当前内存块不存在，记录错误并返回空的通知队列
    if (sharedMemoryIdToInfo_.find(sharedMemoryId) == sharedMemoryIdToInfo_.end()) {
        logger.Error("[ResourceManager] Shared memory block {} is not found", sharedMemoryId);
        return {};
    }
    // 如果当前内存块处于读写状态，直接返回空的通知队列
    if (permissionStatus_[sharedMemoryId] != 0) {
        return {};
    }
    vector<tuple<int32_t, uint64_t>> notificationQueue;
    uint64_t memorySize = GetMemorySize(sharedMemoryId);
    // 依次为等待队列中的多个读权限或位于队头的单个写权限申请颁发许可,并加入通知队列
    while (!waitingPermitRequestQueues_[sharedMemoryId].empty()) {
        const WaitingPermitRequest& request = waitingPermitRequestQueues_[sharedMemoryId].front();
        // 当前内存块已经有互斥读写操作存在，停止颁发新的许可。
        if ((request.permissionType_ == PermissionType::Read && permissionStatus_[sharedMemoryId] == -1) ||
            (request.permissionType_ == PermissionType::Write && permissionStatus_[sharedMemoryId] != 0)) {
            break;
        }
        logger.Info("[ResourceManager] Granting a permission for the waiting Client {} for "
                    "the shared memory block {}", request.applicant_, sharedMemoryId);
        GrantPermission(request.applicant_, request.permissionType_, sharedMemoryId);
        notificationQueue.emplace_back(request.applicant_, memorySize);
        waitingPermitRequestQueues_[sharedMemoryId].pop_front();
    }
    return notificationQueue;
}

ErrorType ResourceManager::PreCheckPermissionCommon(DataBus::Common::PermissionType permissionType,
                                                    int32_t sharedMemoryId)
{
    if (permissionType != PermissionType::Read && permissionType != PermissionType::Write) {
        logger.Error("[ResourceManager] Unknown permission type");
        return ErrorType::UnknownPermissionType;
    }
    if (sharedMemoryIdToInfo_.find(sharedMemoryId) == sharedMemoryIdToInfo_.end()) {
        logger.Error("[ResourceManager] Shared memory block {} is not found", sharedMemoryId);
        return ErrorType::MemoryNotFound;
    }
    return ErrorType::None;
}

ErrorType ResourceManager::CheckApplyPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                                int32_t sharedMemoryId)
{
    ErrorType commonCheckRes = PreCheckPermissionCommon(permissionType, sharedMemoryId);
    if (commonCheckRes != ErrorType::None) {
        logger.Error("[ResourceManager] PreChecking for applying the permission failed");
        return commonCheckRes;
    }
    auto& memoryBlocks = permissionType == PermissionType::Read ? readingMemoryBlocks_ :
                         writingMemoryBlocks_;
    // 不允许重复申请许可
    if (memoryBlocks.find(socketFd) != memoryBlocks.end() &&
        memoryBlocks[socketFd].find(sharedMemoryId) != memoryBlocks[socketFd].end()) {
        logger.Error("[ResourceManager] There is a permission currently held by Client {} for "
                     "the shared memory block {}", socketFd, sharedMemoryId);
        return ErrorType::DuplicatePermitApplication;
    }
    return ErrorType::None;
}

void ResourceManager::GrantPermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                      int32_t sharedMemoryId)
{
    UpdateLastUsedTime(sharedMemoryId);
    if (permissionType == PermissionType::Read) {
        permissionStatus_[sharedMemoryId]++;
        IncrementReadingRefCnt(sharedMemoryId);
        readingMemoryBlocks_[socketFd].insert(sharedMemoryId);
    } else {
        permissionStatus_[sharedMemoryId]--;
        IncrementWritingRefCnt(sharedMemoryId);
        writingMemoryBlocks_[socketFd].insert(sharedMemoryId);
    }
}

PermissionType ResourceManager::CheckPermissionOwnership(int32_t socketFd,
                                                         DataBus::Common::PermissionType permissionType,
                                                         int32_t sharedMemoryId)
{
    bool holdReadPermission =
        readingMemoryBlocks_[socketFd].find(sharedMemoryId) != readingMemoryBlocks_[socketFd].end();
    bool holdWritePermission =
        writingMemoryBlocks_[socketFd].find(sharedMemoryId) != writingMemoryBlocks_[socketFd].end();
    if (!holdReadPermission && !holdWritePermission) {
        logger.Error("[ResourceManager] Client {} does not hold any permission for the shared memory block {}",
                     socketFd, sharedMemoryId);
        return PermissionType::None;
    }
    if (permissionType == PermissionType::Read && holdWritePermission) {
        logger.Warn("[ResourceManager] Client {} tries to release a read permission "
                    "while holding a write permission for the shared memory block {}", socketFd, sharedMemoryId);
        return PermissionType::Write;
    } else if (permissionType == PermissionType::Write && holdReadPermission) {
        logger.Warn("[ResourceManager] Client {} tries to release a write permission "
                    "while holding a read permission for the shared memory block {}", socketFd, sharedMemoryId);
        return PermissionType::Read;
    }
    return permissionType;
}

void ResourceManager::ReleasePermission(int32_t socketFd, DataBus::Common::PermissionType permissionType,
                                        int32_t sharedMemoryId)
{
    UpdateLastUsedTime(sharedMemoryId);
    if (permissionType == PermissionType::Read) {
        permissionStatus_[sharedMemoryId]--;
        DecrementReadingRefCnt(sharedMemoryId);
        readingMemoryBlocks_[socketFd].erase(sharedMemoryId);
    } else {
        permissionStatus_[sharedMemoryId]++;
        DecrementWritingRefCnt(sharedMemoryId);
        writingMemoryBlocks_[socketFd].erase(sharedMemoryId);
    }
}

bool ResourceManager::RemoveDirectory(const std::string& directory)
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

void ResourceManager::CreateDirectory(const std::string& directory)
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

std::unordered_set<int32_t>& ResourceManager::GetReadingMemoryBlocks(int32_t socketFd)
{
    return readingMemoryBlocks_[socketFd];
}

std::unordered_set<int32_t>& ResourceManager::GetWritingMemoryBlocks(int32_t socketFd)
{
    return writingMemoryBlocks_[socketFd];
}

int32_t ResourceManager::GetPermissionStatus(int32_t sharedMemoryId)
{
    return permissionStatus_[sharedMemoryId];
}

deque <WaitingPermitRequest>& ResourceManager::GetWaitingPermitRequests(int32_t sharedMemoryId)
{
    return waitingPermitRequestQueues_[sharedMemoryId];
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

void ResourceManager::GenerateReport(stringstream& reportStream) const
{
    uint32_t memoryTotalUsage = 0;
    reportStream << "\"MemoryBlocks\":[";
    for (auto memoryIter = sharedMemoryIdToInfo_.cbegin();
         memoryIter != sharedMemoryIdToInfo_.cend(); ++memoryIter) {
        if (memoryIter != sharedMemoryIdToInfo_.cbegin()) {
            reportStream << ",";
        }
        // 内存块基本状态
        const auto memoryId = memoryIter->first;
        const auto& memoryInfo = memoryIter->second;
        memoryTotalUsage += memoryInfo->memorySize_;
        reportStream << "{" <<
                     "\"MemoryId\":" << memoryId << "," <<
                     "\"Applicant\":" << memoryInfo->applicant_ << "," <<
                     "\"Size\":" << memoryInfo->memorySize_ << "," <<
                     "\"ReadingRefCnt\":" << memoryInfo->readingRefCnt_ << "," <<
                     "\"WritingRefCnt\":" << memoryInfo->writingRefCnt_ << "," <<
                     "\"LastUsedTime\":" << memoryInfo->lastUsedTime_;
        // 内存块当前读写状态
        if (permissionStatus_.find(memoryId) != permissionStatus_.cend()) {
            reportStream << ",\"PermissionStatus\":" << permissionStatus_.at(memoryId);
        }
        // 内存块当前读写队列
        if (waitingPermitRequestQueues_.find(memoryId) != waitingPermitRequestQueues_.cend()) {
            reportStream << ",\"PermissionWaitingQueue\":[";
            const auto& thisQueue = waitingPermitRequestQueues_.at(memoryId);
            for (auto queueIter = thisQueue.cbegin(); queueIter != thisQueue.cend(); ++queueIter) {
                (queueIter != thisQueue.cbegin() ? reportStream << "," : reportStream) <<
                    "{\"Applicant\":" << queueIter->applicant_ << "," <<
                    "\"PermissionType\":" << static_cast<int>(queueIter->permissionType_) << "}";
            }
            reportStream << "]";
        }
        reportStream << "}";
    }
    reportStream << "],";
    reportStream << "\"MemoryBlockCount\":" << sharedMemoryIdToInfo_.size() << ",";
    reportStream << "\"MemoryBlockTotalSize\":" << memoryTotalUsage;
}

}  // namespace Resource
}  // namespace DataBus
