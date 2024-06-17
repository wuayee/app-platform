/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: test for resource_manager/ResourceManager
 */

#include <gtest/gtest.h>
#include <fstream>

#include "TaskLoop.h"
#include "MockTaskLoop.h"
#include "ResourceManager.h"
#include "FtokArgsGenerator.h"
#include "utils/FileUtils.h"

using namespace std;
using namespace DataBus::Resource;
using namespace DataBus::Common;
using ::testing::_;


namespace DataBus {
namespace Test {

const static std::string TEST_OBJECT_KEY = "8e20a991c4bbb0fab743ee02604e5ad3";

// 内存分配日志中的记录
struct MallocRecord {
    int32_t sharedMemoryId;
    bool released;
};

class ResourceManagerTest : public testing::Test {
public:
    Runtime::Config* config{};
    std::unique_ptr<ResourceManager> resourceManager;
    std::shared_ptr<MockTaskLoop> mockTaskLoopPtr;
    std::shared_ptr<Task::TaskLoop> taskLoopPtr;
protected:
    void SetUp() override
    {
        FtokArgsGenerator::Instance().Reset();
        int port = 1234;
        uint64_t mallocSizeLimit = 200U;
        // 内存块存活时长30分钟
        int32_t memoryTtlDuration = 30 * 60 * 1000;
        int32_t memorySweepInterval = 100;
        config = new Runtime::Config(port, mallocSizeLimit, memoryTtlDuration, memorySweepInterval);
        mockTaskLoopPtr = std::make_shared<MockTaskLoop>();
        taskLoopPtr = std::static_pointer_cast<Task::TaskLoop>(mockTaskLoopPtr);
        resourceManager = std::make_unique<ResourceManager>(*config, taskLoopPtr);
    }

    void TearDown() override
    {
        resourceManager.reset();
        delete config;
    }

    static bool IsFolderExist(const string& folderPath)
    {
        struct stat info{};
        if (stat(folderPath.c_str(), &info) != 0) {
            return false;
        }
        return (info.st_mode & S_IFDIR);
    }

    int32_t AllocateMemory(const std::string& objectKey) const
    {
        int32_t clientId = 1;
        uint64_t memorySize = 100U;
        const tuple<int32_t, ErrorType> applyMemoryRes =
                resourceManager->HandleApplyMemory(clientId, objectKey, memorySize);
        return get<0>(applyMemoryRes);
    }

    void CreateApplyPermissionBatch(PermissionType permissionType, int32_t memoryId, int32_t startId,
                                    int32_t count) const
    {
        for (int32_t permitApplicant = startId; permitApplicant < startId + count; permitApplicant++) {
            resourceManager->HandleApplyPermission({permitApplicant, permissionType, memoryId, false,
                                                    make_shared<UserData>()});
        }
    }

    static vector<MallocRecord> ReadMallocLog()
    {
        vector<MallocRecord> mallocRecords;
        ifstream logStream(LOG_PATH, std::ios::binary);
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

                mallocRecords.push_back({sharedMemoryId, released});
            }
            logStream.close();
        }
        return mallocRecords;
    }
};

TEST_F(ResourceManagerTest, should_clean_up_ftok_file_path_and_shm_log_file_when_init)
{
    resourceManager.reset();
    // 预先创建测试路径
    std::string testDir = FILE_PATH_PREFIX + "test";
    FileUtils::CreateDirectory(testDir);
    EXPECT_TRUE(IsFolderExist(testDir));
    // 重新构建ResourceManager，验证路径和日志文件被清理
    ResourceManagerTest::SetUp();
    EXPECT_FALSE(IsFolderExist(testDir));
    std::ifstream fileCheck(LOG_PATH, std::ios::binary | std::ios::ate);
    EXPECT_TRUE(fileCheck.good() && fileCheck.tellg() == 0);
}

TEST_F(ResourceManagerTest, should_not_malloc_when_key_already_exists)
{
    int32_t clientId = 1;
    uint64_t memorySize = 100U;
    // 首次申请内存分配
    resourceManager->HandleApplyMemory(clientId, TEST_OBJECT_KEY, memorySize);
    // 二次申请内存分配
    tuple<int32_t, ErrorType> applyMemoryRes = resourceManager->HandleApplyMemory(clientId, TEST_OBJECT_KEY,
                                                                                  memorySize);
    EXPECT_EQ(-1, get<0>(applyMemoryRes));
    EXPECT_EQ(ErrorType::KeyAlreadyExists, get<1>(applyMemoryRes));
}

TEST_F(ResourceManagerTest, should_not_malloc_when_malloc_size_limit_exceeded)
{
    int32_t clientId = 1;
    uint64_t memorySize = 300U;
    tuple<int32_t, ErrorType> applyMemoryRes = resourceManager->HandleApplyMemory(clientId, TEST_OBJECT_KEY,
                                                                                  memorySize);
    EXPECT_EQ(-1, get<0>(applyMemoryRes));
    EXPECT_EQ(ErrorType::OutOfMemory, get<1>(applyMemoryRes));
}

TEST_F(ResourceManagerTest, should_malloc_and_save_memory_info_when_handle_apply_memory_succeeds)
{
    int32_t clientId = 1;
    uint64_t memorySize = 100U;
    const tuple<int32_t, ErrorType> applyMemoryRes = resourceManager->HandleApplyMemory(clientId, TEST_OBJECT_KEY,
                                                                                        memorySize);
    int32_t memoryId = get<0>(applyMemoryRes);
    ErrorType errorType = get<1>(applyMemoryRes);
    EXPECT_EQ(ErrorType::None, errorType);
    EXPECT_EQ(100U, resourceManager->GetCurMallocSize());
    EXPECT_EQ(clientId, resourceManager->GetMemoryApplicant(memoryId));
    EXPECT_EQ(memorySize, resourceManager->GetMemorySize(memoryId));
    EXPECT_EQ(0, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetLastUsedTime(memoryId) < chrono::system_clock::now());
    EXPECT_TRUE(resourceManager->GetExpiryTime(memoryId) > chrono::system_clock::now());
}

TEST_F(ResourceManagerTest, should_log_malloc_when_handle_apply_memory_succeeds)
{
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    vector<MallocRecord> mallocRecords = ReadMallocLog();
    EXPECT_EQ(1, mallocRecords.size());
    EXPECT_EQ(memoryId, mallocRecords[0].sharedMemoryId);
    EXPECT_FALSE(mallocRecords[0].released);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_permission_type_unknown)
{
    int32_t permissionApplicantId = 1;
    const ApplyPermissionResponse applyPermitRes = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                          PermissionType::None,
                                                                                          0,
                                                                                          false,
                                                                                          make_shared<UserData>()});
    EXPECT_EQ(false, applyPermitRes.granted_);
    EXPECT_EQ(ErrorType::UnknownPermissionType, applyPermitRes.errorType_);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_memory_not_found)
{
    int32_t permissionApplicantId = 1;
    const ApplyPermissionResponse applyPermitRes = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                          PermissionType::Write,
                                                                                          0,
                                                                                          false,
                                                                                          make_shared<UserData>()});
    EXPECT_EQ(false, applyPermitRes.granted_);
    EXPECT_EQ(ErrorType::MemoryNotFound, applyPermitRes.errorType_);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_memory_is_pending_release)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);

    // 首次申请权限
    int32_t permissionApplicantId1 = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId1, PermissionType::Write,
                                            memoryId, false, make_shared<UserData>()});

    // 释放内存，请求进入队列
    resourceManager->HandleReleaseMemory(memoryId);
    EXPECT_TRUE(resourceManager->IsPendingRelease(memoryId));

    // 再次申请权限
    int32_t permissionApplicantId2 = 3;
    const ApplyPermissionResponse applyPermitRes = resourceManager->HandleApplyPermission({permissionApplicantId2,
                                                                                          PermissionType::Write,
                                                                                          memoryId,
                                                                                          false,
                                                                                          make_shared<UserData>()});
    EXPECT_EQ(false, applyPermitRes.granted_);
    EXPECT_EQ(ErrorType::IllegalStateForPermitApplication, applyPermitRes.errorType_);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_duplicate_applying_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);

    // 首次申请权限
    int32_t permissionApplicantId = 2;
    const ApplyPermissionResponse applyPermitRes1 = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                           PermissionType::Write,
                                                                                           memoryId,
                                                                                           false,
                                                                                           make_shared<UserData>()});
    // 断言首次申请成功
    EXPECT_EQ(true, applyPermitRes1.granted_);
    EXPECT_EQ(ErrorType::None, applyPermitRes1.errorType_);

    // 二次申请权限
    const ApplyPermissionResponse applyPermitRes2 = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                           PermissionType::Write,
                                                                                           memoryId,
                                                                                           false,
                                                                                           make_shared<UserData>()});
    // 断言二次申请失败
    EXPECT_EQ(false, applyPermitRes2.granted_);
    EXPECT_EQ(ErrorType::DuplicatePermitApplication, applyPermitRes2.errorType_);
}

TEST_F(ResourceManagerTest, should_not_release_permission_when_permission_type_unknown)
{
    int32_t permissionReleaserId = 1;
    resourceManager->HandleReleasePermission(permissionReleaserId, PermissionType::None, 0);
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(0));
}

TEST_F(ResourceManagerTest, should_not_release_permission_when_memory_not_found)
{
    int32_t permissionReleaserId = 1;
    resourceManager->HandleReleasePermission(permissionReleaserId, PermissionType::Write, 0);
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(0));
}

TEST_F(ResourceManagerTest, should_not_release_permission_when_client_not_holding_any_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // Client 2 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // Client 3 释放权限
    int32_t permissionReleaserId = 3;
    resourceManager->HandleReleasePermission(permissionReleaserId, PermissionType::Write, memoryId);
    // 断言释放失败
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    const std::unordered_set<int32_t>& writingMemoryBlocks =
            resourceManager->GetWritingMemoryBlocks(permissionApplicantId);
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) != writingMemoryBlocks.end());
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_release_permission_when_permission_type_mismatched)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 申请写权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放读权限
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Read, memoryId);
    // 断言释放写权限成功
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(permissionApplicantId).empty());
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_update_memory_status_when_handle_apply_and_release_permission_succeed)
{
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);

    int32_t permissionApplicantId = 2;
    const ApplyPermissionResponse applyPermitRes = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                          PermissionType::Write,
                                                                                          memoryId,
                                                                                          false,
                                                                                          make_shared<UserData>()});
    // 断言权限申请成功
    EXPECT_EQ(true, applyPermitRes.granted_);
    EXPECT_EQ(ErrorType::None, applyPermitRes.errorType_);
    // 断言内存管理状态修改成功
    EXPECT_TRUE(resourceManager->GetExpiryTime(memoryId) == chrono::system_clock::time_point::max());
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    const std::unordered_set<int32_t>& writingMemoryBlocks =
            resourceManager->GetWritingMemoryBlocks(permissionApplicantId);
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) != writingMemoryBlocks.end());
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_TRUE(resourceManager->GetWaitingPermitRequests(memoryId).empty());
    EXPECT_TRUE(resourceManager->GetWaitingPermitMemoryBlocks(permissionApplicantId).empty());

    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 断言内存管理状态修改成功
    EXPECT_TRUE(resourceManager->GetExpiryTime(memoryId) < chrono::system_clock::time_point::max());
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) == writingMemoryBlocks.end());
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_grant_read_permissions_after_release_write_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 抢先申请写权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission({firstApplicant, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 批量申请读权限
    int32_t readStartId = 3;
    int32_t readCount = 10;
    CreateApplyPermissionBatch(PermissionType::Read, memoryId, readStartId, readCount);
    // 最后再申请写权限
    int32_t lastApplicant = readStartId + readCount;
    resourceManager->HandleApplyPermission({lastApplicant, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放写权限
    resourceManager->HandleReleasePermission(firstApplicant, PermissionType::Write, memoryId);
    vector<ApplyPermissionResponse> notificationQueue = resourceManager->ProcessWaitingPermitRequests(memoryId);
    // 所有等待的读权限请求得到处理，获取许可；最后加入的写权限请求进入等待队列
    EXPECT_EQ(readCount, notificationQueue.size());
    EXPECT_EQ(readCount, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(firstApplicant).empty());
    for (int32_t permitApplicant = readStartId; permitApplicant < readStartId + readCount; permitApplicant++) {
        const std::unordered_set<int32_t>& memoryBlocks = resourceManager->GetReadingMemoryBlocks(permitApplicant);
        EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
    }
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(lastApplicant).empty());
    EXPECT_EQ(readCount, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_EQ(1, resourceManager->GetWaitingPermitRequests(memoryId).size());
    EXPECT_EQ(1, resourceManager->GetWaitingPermitMemoryBlocks(lastApplicant).size());
}

TEST_F(ResourceManagerTest, should_grant_write_permission_after_release_read_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 抢先申请读权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission({firstApplicant, PermissionType::Read, memoryId, false,
                                            make_shared<UserData>()});
    // 批量申请写权限
    int32_t writeStartId = 3;
    int32_t writeCount = 10;
    CreateApplyPermissionBatch(PermissionType::Write, memoryId, writeStartId, writeCount);
    // 释放读权限
    resourceManager->HandleReleasePermission(firstApplicant, PermissionType::Read, memoryId);
    vector<ApplyPermissionResponse> notificationQueue = resourceManager->ProcessWaitingPermitRequests(memoryId);
    // 只有第一个等待的写权限会被处理，获取许可；其他写权限继续在队列中等待
    EXPECT_EQ(1, notificationQueue.size());
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetReadingMemoryBlocks(firstApplicant).empty());
    const std::unordered_set<int32_t>& memoryBlocks = resourceManager->GetWritingMemoryBlocks(writeStartId);
    EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
    for (int32_t permitApplicant = writeStartId + 1;
    permitApplicant < writeStartId + writeCount; permitApplicant++) {
        EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(permitApplicant).empty());
    }
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_EQ(writeCount - 1, resourceManager->GetWaitingPermitRequests(memoryId).size());
}

TEST_F(ResourceManagerTest, should_get_user_data_when_apply_read_permission_after_apply_write_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);

    // 申请写权限，附带写入用户自定义元数据
    int32_t permissionApplicantId = 2;
    const size_t dataSize = 10;
    int8_t staticData[10] = {0};
    const int8_t* userData = staticData;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write,
                                            memoryId, true, make_shared<UserData>(userData, dataSize)});
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);

    EXPECT_TRUE(resourceManager->GetUserData(memoryId)->userDataPtr_);
    EXPECT_EQ(0, memcmp(userData,  resourceManager->GetUserData(memoryId)->userDataPtr_.get(), dataSize));
    EXPECT_EQ(dataSize, resourceManager->GetUserData(memoryId)->dataSize_);

    // 申请读权限,附带读取用户自定义元数据
    const ApplyPermissionResponse applyPermitRes = resourceManager->HandleApplyPermission({permissionApplicantId,
                                                                                           PermissionType::Read,
                                                                                           memoryId,
                                                                                           true,
                                                                                           make_shared<UserData>()});
    EXPECT_TRUE(applyPermitRes.userData_->userDataPtr_);
    EXPECT_EQ(0, memcmp(userData,  applyPermitRes.userData_->userDataPtr_.get(), dataSize));
    EXPECT_EQ(dataSize, applyPermitRes.userData_->dataSize_);
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Read, memoryId);
}

TEST_F(ResourceManagerTest, should_not_release_memory_when_memory_not_found)
{
    // 在未分配内存的情况下直接释放内存
    EXPECT_FALSE(resourceManager->HandleReleaseMemory(0));
}

TEST_F(ResourceManagerTest, should_keep_pending_when_memory_pending_release)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放内存，请求进入等待状态
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    EXPECT_EQ(memoryId, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
    // 再次释放内存失败
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    EXPECT_TRUE(resourceManager->IsPendingRelease(memoryId));
}

TEST_F(ResourceManagerTest, should_release_memory_when_memory_idles)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放权限
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 释放内存成功
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    EXPECT_EQ(0U, resourceManager->GetCurMallocSize());
    EXPECT_EQ(-1, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
}

TEST_F(ResourceManagerTest, should_update_malloc_log_when_relesae_memory_succeeds)
{
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    vector<MallocRecord> mallocRecords = ReadMallocLog();
    int32_t expectedLogCount = 2;
    EXPECT_EQ(expectedLogCount, mallocRecords.size());
    EXPECT_EQ(memoryId, mallocRecords[1].sharedMemoryId);
    EXPECT_TRUE(mallocRecords[1].released);
}

TEST_F(ResourceManagerTest, should_not_process_pending_release_memory_when_memory_not_pending_release)
{
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    EXPECT_TRUE(resourceManager->ProcessPendingReleaseMemory(memoryId));
    EXPECT_EQ(memoryId, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
}

TEST_F(ResourceManagerTest, should_not_process_pending_release_memory_when_memory_not_idles)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放内存，请求进入等待状态
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    EXPECT_TRUE(resourceManager->IsPendingRelease(memoryId));
    // 不处理待释放内存
    EXPECT_TRUE(resourceManager->ProcessPendingReleaseMemory(memoryId));
    EXPECT_EQ(memoryId, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
}

TEST_F(ResourceManagerTest, should_process_pending_release_memory_when_memory_pending_release_and_idles)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放内存，请求进入等待状态
    EXPECT_TRUE(resourceManager->HandleReleaseMemory(memoryId));
    EXPECT_TRUE(resourceManager->IsPendingRelease(memoryId));
    // 释放权限，恢复内存闲置状态
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 成功处理待释放内存
    EXPECT_TRUE(resourceManager->ProcessPendingReleaseMemory(memoryId));
    EXPECT_EQ(-1, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
}

TEST_F(ResourceManagerTest, should_return_permissions_held_before_client_releases_permission)
{
    // 分配内存
    std::string objectKey1 = "key1";
    int32_t memoryId1 = AllocateMemory(objectKey1);
    // 申请读权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Read, memoryId1, false,
                                            make_shared<UserData>()});
    // 分配新内存
    std::string objectKey2 = "key2";
    int32_t memoryId2 = AllocateMemory(objectKey2);
    // 对新内存申请写权限
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId2, false,
                                            make_shared<UserData>()});

    vector<PermissionHeld> permissionsHeld = resourceManager->GetPermissionsHeld(permissionApplicantId);
    int32_t expectedCount = 2;
    EXPECT_EQ(expectedCount, permissionsHeld.size());
    EXPECT_EQ(memoryId1, permissionsHeld[0].sharedMemoryId_);
    EXPECT_EQ(PermissionType::Read, permissionsHeld[0].permissionType_);
    EXPECT_EQ(memoryId2, permissionsHeld[1].sharedMemoryId_);
    EXPECT_EQ(PermissionType::Write, permissionsHeld[1].permissionType_);
}

TEST_F(ResourceManagerTest, should_remove_client_from_waiting_permit_request_queue_when_connection_closed)
{
    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 抢先申请写权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission({firstApplicant, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 批量申请读权限
    int32_t readStartId = 3;
    int32_t readCount = 10;
    CreateApplyPermissionBatch(PermissionType::Read, memoryId, readStartId, readCount);

    EXPECT_EQ(readCount, resourceManager->GetWaitingPermitRequests(memoryId).size());
    EXPECT_EQ(1, resourceManager->GetWaitingPermitMemoryBlocks(readStartId).size());

    // 将readStartId对应的客户端从权限等待队列中移除
    resourceManager->RemoveClientFromWaitingQueue(readStartId);

    EXPECT_EQ(readCount - 1, resourceManager->GetWaitingPermitRequests(memoryId).size());
    EXPECT_TRUE(resourceManager->GetWaitingPermitMemoryBlocks(readStartId).empty());
}

TEST_F(ResourceManagerTest, should_auto_recycle_memory_when_memory_expires)
{
    int port = 1234;
    uint64_t mallocSizeLimit = 100U;
    // 内存块存活时长1分钟
    int32_t memoryTtlDuration = 1000;
    int32_t memorySweepInterval = 100;
    Runtime::Config config(port, mallocSizeLimit, memoryTtlDuration, memorySweepInterval);
    // 自定义MockTaskLoop的行为
    EXPECT_CALL(*mockTaskLoopPtr, AddReadTask(_, _, _)).WillRepeatedly(testing::Invoke([&]() {
        if (resourceManager) {
            resourceManager->CleanupExpiredMemory();
        }
    }));
    resourceManager = std::make_unique<ResourceManager>(config, taskLoopPtr);

    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    EXPECT_EQ(memoryId, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
    // 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission({permissionApplicantId, PermissionType::Write, memoryId, false,
                                            make_shared<UserData>()});
    // 释放权限
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 等待自动回收
    int32_t sleepDuration = 1300;
    this_thread::sleep_for(chrono::milliseconds(sleepDuration));
    // 验证内存块已经被自动回收
    EXPECT_EQ(-1, resourceManager->GetMemoryId(TEST_OBJECT_KEY));
}

class ApplyPermissionParamTest : public ResourceManagerTest,
        public testing::WithParamInterface<tuple<PermissionType, PermissionType>> {
protected:
    void AssertApplyMultipleReadPermits(int32_t memoryId, int32_t firstApplicant, int32_t startId, int32_t count) const
    {
        // 多读场景，所有申请均可成功
        EXPECT_EQ(count + 1, resourceManager->GetReadingRefCnt(memoryId));
        const std::unordered_set<int32_t>& firstApplicantMemoryBlocks =
                resourceManager->GetReadingMemoryBlocks(firstApplicant);
        EXPECT_TRUE(firstApplicantMemoryBlocks.find(memoryId) != firstApplicantMemoryBlocks.end());
        for (int32_t permitApplicant = startId; permitApplicant < startId + count; permitApplicant++) {
            const std::unordered_set<int32_t>& memoryBlocks = resourceManager->GetReadingMemoryBlocks(permitApplicant);
            EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
        }
        EXPECT_EQ(count + 1, resourceManager->GetPermissionStatus(memoryId));
        EXPECT_TRUE(resourceManager->GetWaitingPermitRequests(memoryId).empty());
    }

    void AssertApplyMutexPermits(int32_t memoryId, int32_t firstApplicant, PermissionType firstPermission,
                                 int32_t count) const
    {
        // 读写互斥场景，后续申请全部进入等待队列
        EXPECT_EQ(1, firstPermission == PermissionType::Read ?
                     resourceManager->GetReadingRefCnt(memoryId) :
                     resourceManager->GetWritingRefCnt(memoryId));
        EXPECT_EQ(0, firstPermission == PermissionType::Read ?
                     resourceManager->GetWritingRefCnt(memoryId) :
                     resourceManager->GetReadingRefCnt(memoryId));

        const std::unordered_set<int32_t>& memoryBlocks = firstPermission == PermissionType::Read ?
                                                    resourceManager->GetReadingMemoryBlocks(firstApplicant) :
                                                    resourceManager->GetWritingMemoryBlocks(firstApplicant);
        EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
        EXPECT_EQ(firstPermission == PermissionType::Read ? 1 : -1, resourceManager->GetPermissionStatus(memoryId));
        EXPECT_EQ(count, resourceManager->GetWaitingPermitRequests(memoryId).size());
    }
};

TEST_P(ApplyPermissionParamTest, should_handle_apply_permission_correctly_before_release_a_previous_permission)
{
    tuple<PermissionType, PermissionType> permissions = GetParam();
    PermissionType firstPermission = get<0>(permissions);
    PermissionType secondPermission = get<1>(permissions);

    // 分配内存
    int32_t memoryId = AllocateMemory(TEST_OBJECT_KEY);
    // 抢先申请权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission({firstApplicant, firstPermission, memoryId, false, make_shared<UserData>()});
    // 批量申请权限
    int32_t startId = 3;
    int32_t count = 10;
    CreateApplyPermissionBatch(secondPermission, memoryId, startId, count);
    if (firstPermission == PermissionType::Read && secondPermission == PermissionType::Read) {
        AssertApplyMultipleReadPermits(memoryId, firstApplicant, startId, count);
    } else {
        AssertApplyMutexPermits(memoryId, firstApplicant, firstPermission, count);
    }
}

INSTANTIATE_TEST_SUITE_P(ResourceManager, ApplyPermissionParamTest,
                         testing::Values(make_pair(PermissionType::Read, PermissionType::Read),
                                         make_pair(PermissionType::Read, PermissionType::Write),
                                         make_pair(PermissionType::Write, PermissionType::Read),
                                         make_pair(PermissionType::Write, PermissionType::Write)));
}  // namespace Test
}  // namespace DataBus
