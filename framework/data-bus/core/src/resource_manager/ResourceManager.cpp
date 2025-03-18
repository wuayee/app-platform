/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#include <memory>
#include <sys/shm.h>
#include <cstring>

#include "fbs/apply_permission_message_response_generated.h"
#include "FtokArgsGenerator.h"
#include "log/Logger.h"
#include "utils/DataBusUtils.h"
#include "ResourceManager.h"

using namespace std;
using namespace DataBus::Common;
using namespace DataBus::Common::Utils;

namespace DataBus {
namespace Resource {

ResourceManager::ResourceManager(const Runtime::Config& config, shared_ptr<Task::TaskLoop>& taskLoopPtr)
    : config_(config), curMallocSize_(0), stopMemorySweepThread_(false), taskLoopPtr_(taskLoopPtr)
{
    Init();
    // 打开内存分配日志文件。
    logStream_.open(LOG_PATH, std::ios::binary | std::ios::app);
    if (!logStream_.is_open()) {
        logger.Error("[ResourceManager] Failed to open the malloc log file {}", LOG_PATH);
    }
}

ResourceManager::~ResourceManager()
{
    // 关闭内存分配日志文件。
    if (logStream_.is_open()) {
        logStream_.close();
    }
    // 停止过期内存清扫线程。
    stopMemorySweepThread_.store(true);
    if (memorySweepThread_.joinable()) {
        memorySweepThread_.join();
    }
}

void ResourceManager::Init()
{
    if (!FileUtils::RemoveDirectory(FILE_PATH_PREFIX)) {
        logger.Debug("[ResourceManager] Failed to remove the ftok directory during the ResourceManager init "
                     "phase");
    }

    FileUtils::CreateFileIfNotExists(LOG_PATH);
    // 释放之前申请的共享内存。
    CleanupMemory();
    // 清空内存分配日志文件。
    std::ofstream(LOG_PATH, std::ofstream::out | std::ofstream::trunc).close();

    // 启动过期内存清扫线程。
    StartMemorySweep(config_.GetMemorySweepInterval());
}

tuple <int32_t, ErrorType> ResourceManager::HandleApplyMemory(int32_t socketFd, const std::string& objectKey,
                                                              uint64_t memorySize)
{
    // 检查objectKey是否已经绑定已有内存
    if (keyToSharedMemoryId_.find(objectKey) != keyToSharedMemoryId_.end()) {
        logger.Error("[ResourceManager] The object key {} already exists", objectKey);
        return make_tuple(-1, ErrorType::KeyAlreadyExists);
    }
    // 检查剩余内存分配空间是否充足
    if (curMallocSize_ + memorySize > config_.GetMallocSizeLimit()) {
        logger.Error("[ResourceManager] Failed to allocate the memory block for {} due to insufficient memory "
                     "space. requested: {}, remaining: {}", objectKey, memorySize,
                     config_.GetMallocSizeLimit() - curMallocSize_);
        return make_tuple(-1, ErrorType::OutOfMemory);
    }
    // 获取ftok函数参数生成器单例
    auto& ftokArgsGenerator = FtokArgsGenerator::Instance();
    const std::string& pathName = ftokArgsGenerator.GetFilePath();
    const int32_t projId = ftokArgsGenerator.GetProjId();
    if (projId == 0) {
        FileUtils::CreateDirectory(pathName);
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
        sharedMemoryId = RecreateSharedMemoryBlock(sharedMemoryKey, memorySize);
        if (sharedMemoryId == -1) {
            logger.Error("[ResourceManager] Failed to recreate a shared memory block: {}", strerror(errno));
            return make_tuple(-1, ErrorType::MallocFailed);
        }
        logger.Info("[ResourceManager] Recreating a new sharedMemoryId associated with sharedMemoryKey {} "
                    "succeeded", sharedMemoryKey);
    }
    // 更新内存申请日志
    AppendLog(sharedMemoryId, false);

    // 记录共享内存块ID和共享内存块信息的对应关系
    const auto& now = std::chrono::system_clock::now();
    sharedMemoryIdToInfo_[sharedMemoryId] =
            std::make_unique<SharedMemoryInfo>(socketFd, memorySize, now,
                                               now + chrono::milliseconds(config_.GetMemoryTtlDuration()));
    if (!objectKey.empty()) {
        keyToSharedMemoryId_[objectKey] = sharedMemoryId;
    }
    curMallocSize_ += memorySize;

    return make_tuple(sharedMemoryId, ErrorType::None);
}

ApplyPermissionResponse ResourceManager::HandleApplyPermission(const ApplyPermissionRequest& request)
{
    const int32_t socketFd = request.socketFd;
    const uint32_t seq = request.seq;
    const PermissionType& permissionType = request.permissionType;
    const int32_t sharedMemoryId = request.sharedMemoryId;

    ErrorType preCheckRes = CheckApplyPermission(socketFd, permissionType, sharedMemoryId);
    if (preCheckRes != ErrorType::None) {
        logger.Error("[ResourceManager] Checking for applying the permission failed");
        std::shared_ptr<UserData> userDataPtr = std::make_shared<UserData>();
        return {false, seq, {socketFd, -1, 0, userDataPtr}, preCheckRes};
    }
    bool shouldGrant = permissionType == PermissionType::Read ? permissionStatus_[sharedMemoryId] >= 0 :
                       permissionStatus_[sharedMemoryId] == 0;
    uint64_t memorySize = GetMemorySize(sharedMemoryId);
    // 当且仅当读请求且设定操作用户自定义数据时，返回用户自定义元数据
    const shared_ptr<UserData>& userDataPtr =
            permissionType == PermissionType::Read && request.isOperatingUserData ?
            GetUserData(sharedMemoryId) : make_shared<UserData>();
    if (shouldGrant) {
        // 当前内存块没有任何阻塞读写操作, 无需等待，直接授权
        GrantPermission(request);
        return {true, seq, {socketFd, sharedMemoryId, memorySize, userDataPtr}, ErrorType::None};
    }
    // 否则，将权限请求加入等待队列
    logger.Info("[ResourceManager] Client {}'s ApplyPermission request is being queued for "
                "the shared memory block {}", socketFd, sharedMemoryId);
    waitingPermitRequestQueues_[sharedMemoryId].emplace_back(socketFd, seq, permissionType,
                                                             request.isOperatingUserData, request.userData);
    waitingPermitMemoryBlocks_[socketFd].insert(sharedMemoryId);
    return {false, seq, {socketFd, sharedMemoryId, memorySize, userDataPtr}, ErrorType::None};
}

bool ResourceManager::HandleReleasePermission(int32_t socketFd, PermissionType permissionType, int32_t sharedMemoryId)
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

vector <ApplyPermissionResponse> ResourceManager::ProcessWaitingPermitRequests(int32_t sharedMemoryId)
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
    vector<ApplyPermissionResponse> notificationQueue;
    uint64_t memorySize = GetMemorySize(sharedMemoryId);
    // 依次为等待队列中的多个读权限或位于队头的单个写权限申请颁发许可,并加入通知队列
    while (!waitingPermitRequestQueues_[sharedMemoryId].empty()) {
        const WaitingPermitRequest& request = waitingPermitRequestQueues_[sharedMemoryId].front();
        const int32_t applicant = request.applicant;
        const uint32_t seq = request.seq;
        const PermissionType& permissionType = request.permissionType;
        // 当前内存块已经有互斥读写操作存在，停止颁发新的许可。
        if ((permissionType == PermissionType::Read && permissionStatus_[sharedMemoryId] == -1) ||
            (permissionType == PermissionType::Write && permissionStatus_[sharedMemoryId] != 0)) {
            break;
        }
        logger.Info("[ResourceManager] Granting a permission for the waiting Client {} for "
                    "the shared memory block {}", request.applicant, sharedMemoryId);
        GrantPermission({applicant, seq, permissionType, sharedMemoryId,
                         {request.isOperatingUserData, request.userData}});
        // 当且仅当读请求且设定操作用户自定义数据时，返回用户自定义元数据
        const shared_ptr<UserData>& userDataPtr =
                permissionType == PermissionType::Read && request.isOperatingUserData ?
                GetUserData(sharedMemoryId) : make_shared<UserData>();
        ApplyPermissionMemoryInfo memoryInfo(applicant, sharedMemoryId, memorySize, userDataPtr);
        notificationQueue.emplace_back(true, seq, memoryInfo, ErrorType::None);
        waitingPermitRequestQueues_[sharedMemoryId].pop_front();
        waitingPermitMemoryBlocks_[request.applicant].erase(sharedMemoryId);
    }
    return notificationQueue;
}

bool ResourceManager::HandleReleaseMemory(int32_t sharedMemoryId)
{
    if (sharedMemoryIdToInfo_.find(sharedMemoryId) == sharedMemoryIdToInfo_.end()) {
        logger.Error("[ResourceManager] Shared memory block {} is not found", sharedMemoryId);
        return false;
    }
    if (IsPendingRelease(sharedMemoryId)) {
        logger.Warn("[ResourceManager] Shared memory block {} is already pending release", sharedMemoryId);
        return true;
    }
    // 如果内存块当前没有任何引用，直接释放
    if (permissionStatus_[sharedMemoryId] == 0) {
        return ReleaseMemory(sharedMemoryId);
    }
    logger.Info("[ResourceManager] ReleaseMemory request for the shared memory block {} is pending",
                sharedMemoryId);
    MarkPendingRelease(sharedMemoryId);
    return true;
}

bool ResourceManager::ProcessPendingReleaseMemory(int32_t sharedMemoryId)
{
    if (sharedMemoryIdToInfo_.find(sharedMemoryId) == sharedMemoryIdToInfo_.end()) {
        logger.Error("[ResourceManager] Shared memory block {} is not found", sharedMemoryId);
        return false;
    }
    if (!IsPendingRelease(sharedMemoryId) || GetPermissionStatus(sharedMemoryId) != 0) {
        return true;
    }
    return ReleaseMemory(sharedMemoryId);
}

vector<PermissionHeld> ResourceManager::GetPermissionsHeld(int32_t socketFd)
{
    vector<PermissionHeld> permissionsHeld;
    for (int32_t sharedMemoryId : readingMemoryBlocks_[socketFd]) {
        permissionsHeld.emplace_back(sharedMemoryId, PermissionType::Read);
    }
    for (int32_t sharedMemoryId : writingMemoryBlocks_[socketFd]) {
        permissionsHeld.emplace_back(sharedMemoryId, PermissionType::Write);
    }
    return permissionsHeld;
}

void ResourceManager::RemoveClientFromWaitingQueue(int32_t socketFd)
{
    for (int32_t sharedMemoryId : waitingPermitMemoryBlocks_[socketFd]) {
        if (waitingPermitRequestQueues_.find(sharedMemoryId) == waitingPermitRequestQueues_.end()) {
            logger.Error("[ResourceManager] SharedMemoryId {} is not found in the waitingPermitRequestQueues");
            continue;
        }
        auto& requestQueue = waitingPermitRequestQueues_[sharedMemoryId];
        // 将指定客户端的权限申请请求从等待队列中移除
        auto matchFn = [socketFd](const WaitingPermitRequest& request) {
            return request.applicant == socketFd;
        };
        requestQueue.erase(std::remove_if(requestQueue.begin(), requestQueue.end(), matchFn), requestQueue.end());
    }
    waitingPermitMemoryBlocks_.erase(socketFd);
}

void ResourceManager::CleanupExpiredMemory()
{
    const auto& now = std::chrono::system_clock::now();
    vector<int32_t> memoryBlocksToRelease;
    for (const auto& pair : sharedMemoryIdToInfo_) {
        if (pair.second->expiryTime < now) {
            memoryBlocksToRelease.push_back(pair.first);
        }
    }
    for (int32_t sharedMemoryId : memoryBlocksToRelease) {
        ReleaseMemory(sharedMemoryId);
    }
}

void ResourceManager::MarkPendingRelease(int32_t sharedMemoryId)
{
    sharedMemoryIdToInfo_[sharedMemoryId]->pendingRelease = true;
}

bool ResourceManager::ReleaseMemory(int32_t sharedMemoryId)
{
    // IPC_RMID: 移除共享内存ID
    if (shmctl(sharedMemoryId, IPC_RMID, nullptr) == -1) {
        logger.Error("[ResourceManager] Failed to release the shared memory block {}: {}",
                     sharedMemoryId, strerror(errno));
        return false;
    }
    // 清理资源状态
    curMallocSize_ -= GetMemorySize(sharedMemoryId);
    sharedMemoryIdToInfo_.erase(sharedMemoryId);
    permissionStatus_.erase(sharedMemoryId);
    CleanupWaitingPermitRequests(sharedMemoryId);
    RemoveObjectKey(sharedMemoryId);
    // 更新内存申请日志
    AppendLog(sharedMemoryId, true);
    logger.Info("[ResourceManager] Releasing the shared memory block {} succeeded", sharedMemoryId);
    return true;
}

void ResourceManager::CleanupWaitingPermitRequests(int32_t sharedMemoryId)
{
    for (const auto& request : waitingPermitRequestQueues_[sharedMemoryId]) {
        waitingPermitMemoryBlocks_[request.applicant].erase(sharedMemoryId);
    }
    waitingPermitRequestQueues_.erase(sharedMemoryId);
}

void ResourceManager::RemoveObjectKey(int32_t sharedMemoryId)
{
    for (auto it = keyToSharedMemoryId_.begin(); it != keyToSharedMemoryId_.end();) {
        if (it->second == sharedMemoryId) {
            it = keyToSharedMemoryId_.erase(it);
        } else {
            ++it;
        }
    }
}

void ResourceManager::StartMemorySweep(int32_t scheduleInterval)
{
    memorySweepThread_ = thread([this, scheduleInterval]() {
        while (!stopMemorySweepThread_.load()) {
            this_thread::sleep_for(chrono::milliseconds(scheduleInterval));
            this->AddMemoryCleanupTask();
        }
    });
}

void ResourceManager::AddMemoryCleanupTask()
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    auto sender = [this](const uint8_t* buf, ssize_t s) {
        taskLoopPtr_->AddReadTask(Task::DATABUS_SOCKET_FD, reinterpret_cast<const char*>(buf), s);
    };
    logger.Info("[ResourceManager] Adding a memory cleanup task to the task loop");
    Utils::SendMessage(bodyBuilder, Common::MessageType::CleanupExpiredMemory,
                       Task::DATABUS_INTERNAL_SEQ, sender);
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
    // 不允许对待释放的内存块发起权限申请
    if (IsPendingRelease(sharedMemoryId)) {
        logger.Error("[ResourceManager] Cannot apply permissions for the pending release memory block {}",
                     sharedMemoryId);
        return ErrorType::IllegalStateForPermitApplication;
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

void ResourceManager::GrantPermission(const ApplyPermissionRequest& request)
{
    int32_t socketFd = request.socketFd;
    PermissionType permissionType = request.permissionType;
    int32_t sharedMemoryId = request.sharedMemoryId;
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
    // 当且仅当用户申请写权限且指定操作自定义元数据时，保存用户自定义元数据
    if (permissionType == PermissionType::Write && request.isOperatingUserData) {
        sharedMemoryIdToInfo_[sharedMemoryId]->userData = request.userData;
    }
    // 更新内存块过期时间
    UpdateExpiryTime(sharedMemoryId);
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
    // 更新内存块过期时间
    UpdateExpiryTime(sharedMemoryId);
}

void ResourceManager::AppendLog(int32_t sharedMemoryId, bool released)
{
    if (logStream_.is_open()) {
        logStream_.write(reinterpret_cast<const char*>(&sharedMemoryId), sizeof(sharedMemoryId));
        logStream_.write(reinterpret_cast<const char*>(&released), sizeof(released));
        logStream_.flush();
    }
}

void ResourceManager::CleanupMemory()
{
    std::ifstream logStream(LOG_PATH);
    // 用于记录未释放的共享内存ID
    std::unordered_set<int32_t> activeSharedMemoryIds;
    if (logStream.is_open()) {
        int32_t sharedMemoryId;
        bool released;
        std::vector<char> buffer(sizeof(sharedMemoryId) + sizeof(released));

        while (logStream) {
            buffer.resize(sizeof(sharedMemoryId));
            logStream.read(&buffer[0], sizeof(sharedMemoryId));
            if (!logStream) {
                break;
            }
            sharedMemoryId = *reinterpret_cast<int32_t*>(buffer.data());
            buffer.resize(sizeof(released));
            logStream.read(&buffer[0], sizeof(released));
            if (!logStream) {
                break;
            }
            released = *reinterpret_cast<bool*>(buffer.data());
            if (released) {
                // 如果是释放操作，移除共享内存ID
                activeSharedMemoryIds.erase(sharedMemoryId);
            } else {
                // 如果是分配操作，添加共享内存ID
                activeSharedMemoryIds.insert(sharedMemoryId);
            }
        }
        logStream.close();
    }

    // 清理未释放的共享内存
    for (int32_t sharedMemoryId : activeSharedMemoryIds) {
        if (shmctl(sharedMemoryId, IPC_RMID, nullptr) == -1) {
            logger.Error("[ResourceManager] Failed to clean up the shared memory block {}: {}",
                         sharedMemoryId, strerror(errno));
        }
    }
}

int32_t ResourceManager::RecreateSharedMemoryBlock(key_t sharedMemoryKey, uint64_t memorySize)
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

uint64_t ResourceManager::GetCurMallocSize() const
{
    return curMallocSize_;
}

int32_t ResourceManager::GetMemoryId(const std::string& objectKey)
{
    return keyToSharedMemoryId_.find(objectKey) == keyToSharedMemoryId_.end() ? -1 : keyToSharedMemoryId_[objectKey];
}

bool ResourceManager::IsZeroMemory(const std::string& objectKey)
{
    return zeroMemoryUserData_.find(objectKey) != zeroMemoryUserData_.end();
}

void ResourceManager::AddZeroMemoryUserData(const std::string& objectKey, const std::shared_ptr<UserData>& userDataPtr)
{
    zeroMemoryUserData_[objectKey] = userDataPtr;
}

const std::shared_ptr<UserData>& ResourceManager::GetZeroMemoryUserData(const std::string& objectKey)
{
    return zeroMemoryUserData_[objectKey];
}

void ResourceManager::RemoveZeroMemoryUserData(const std::string& objectKey)
{
    zeroMemoryUserData_.erase(objectKey);
}

int32_t ResourceManager::GetMemoryApplicant(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->applicant;
}

uint64_t ResourceManager::GetMemorySize(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->memorySize;
}

int32_t ResourceManager::GetReadingRefCnt(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt;
}

int32_t ResourceManager::GetWritingRefCnt(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt;
}

chrono::system_clock::time_point ResourceManager::GetLastUsedTime(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->lastUsedTime;
}

const shared_ptr<UserData>& ResourceManager::GetUserData(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->userData;
}

bool ResourceManager::IsPendingRelease(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->pendingRelease;
}

chrono::system_clock::time_point ResourceManager::GetExpiryTime(int32_t sharedMemoryId)
{
    return sharedMemoryIdToInfo_[sharedMemoryId]->expiryTime;
}

const std::unordered_set<int32_t>& ResourceManager::GetReadingMemoryBlocks(int32_t socketFd)
{
    return readingMemoryBlocks_[socketFd];
}

const std::unordered_set<int32_t>& ResourceManager::GetWritingMemoryBlocks(int32_t socketFd)
{
    return writingMemoryBlocks_[socketFd];
}

int32_t ResourceManager::GetPermissionStatus(int32_t sharedMemoryId)
{
    return permissionStatus_[sharedMemoryId];
}

const deque <WaitingPermitRequest>& ResourceManager::GetWaitingPermitRequests(int32_t sharedMemoryId)
{
    return waitingPermitRequestQueues_[sharedMemoryId];
}

const unordered_set<int32_t>& ResourceManager::GetWaitingPermitMemoryBlocks(int32_t socketFd)
{
    return waitingPermitMemoryBlocks_[socketFd];
}

MemoryMetadata ResourceManager::GetMemoryMetadata(int32_t sharedMemoryId)
{
    return {sharedMemoryId, GetMemorySize(sharedMemoryId), GetUserData(sharedMemoryId)};
}

int32_t ResourceManager::IncrementReadingRefCnt(int32_t sharedMemoryId)
{
    return ++sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt;
}

int32_t ResourceManager::DecrementReadingRefCnt(int32_t sharedMemoryId)
{
    return --sharedMemoryIdToInfo_[sharedMemoryId]->readingRefCnt;
}

int32_t ResourceManager::IncrementWritingRefCnt(int32_t sharedMemoryId)
{
    return ++sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt;
}

int32_t ResourceManager::DecrementWritingRefCnt(int32_t sharedMemoryId)
{
    return --sharedMemoryIdToInfo_[sharedMemoryId]->writingRefCnt;
}

void ResourceManager::UpdateLastUsedTime(int32_t sharedMemoryId)
{
    sharedMemoryIdToInfo_[sharedMemoryId]->lastUsedTime = std::chrono::system_clock::now();
}

void ResourceManager::UpdateExpiryTime(int32_t sharedMemoryId)
{
    if (permissionStatus_[sharedMemoryId] == 0) {
        // 内存块引用计数归零，生命周期进入倒计时
        const auto& now = std::chrono::system_clock::now();
        sharedMemoryIdToInfo_[sharedMemoryId]->expiryTime =
                now + chrono::milliseconds(config_.GetMemoryTtlDuration());
    } else {
        // 内存块被引用，永不过期
        sharedMemoryIdToInfo_[sharedMemoryId]->expiryTime = chrono::system_clock::time_point::max();
    }
}

void ResourceManager::GenerateReport(stringstream& reportStream) const
{
    reportStream << "\"MemoryBlocks\":[";
    for (auto memoryIter = sharedMemoryIdToInfo_.cbegin();
         memoryIter != sharedMemoryIdToInfo_.cend(); ++memoryIter) {
        if (memoryIter != sharedMemoryIdToInfo_.cbegin()) {
            reportStream << ",";
        }
        // 内存块基本状态
        const auto memoryId = memoryIter->first;
        const auto& memoryInfo = memoryIter->second;
        reportStream << "{" <<
                     "\"MemoryId\":" << memoryId << "," <<
                     "\"Applicant\":" << memoryInfo->applicant << "," <<
                     "\"Size\":" << memoryInfo->memorySize << "," <<
                     "\"ReadingRefCnt\":" << memoryInfo->readingRefCnt << "," <<
                     "\"WritingRefCnt\":" << memoryInfo->writingRefCnt << "," <<
                     "\"LastUsedTime\":" << chrono::system_clock::to_time_t(memoryInfo->lastUsedTime) << "," <<
                     "\"PendingRelease\":" << memoryInfo->pendingRelease << "," <<
                     "\"ExpiryTime\":" << chrono::system_clock::to_time_t(memoryInfo->expiryTime);
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
                    "{\"Applicant\":" << queueIter->applicant << "," <<
                    "\"PermissionType\":" << static_cast<int>(queueIter->permissionType) << "}";
            }
            reportStream << "]";
        }
        reportStream << "}";
    }
    reportStream << "],";
    reportStream << "\"MemoryBlockCount\":" << sharedMemoryIdToInfo_.size() << ",";
    reportStream << "\"MemoryBlockTotalSize\":" << curMallocSize_;
}

}  // namespace Resource
}  // namespace DataBus
