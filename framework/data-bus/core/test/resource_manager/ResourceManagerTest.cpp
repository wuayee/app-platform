/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: test for resource_manager/ResourceManager
 */

#include <gtest/gtest.h>

#include "ResourceManager.h"
#include "FtokArgsGenerator.h"

using namespace std;
using namespace DataBus::Resource;
using namespace DataBus::Common;


namespace DataBus {
namespace Test {
class ResourceManagerTest : public testing::Test {

public:
    std::unique_ptr<ResourceManager> resourceManager;
protected:
    void SetUp() override
    {
        FtokArgsGenerator::Instance().Reset();
        resourceManager = std::make_unique<ResourceManager>();
    }

    void TearDown() override
    {
        resourceManager.reset();
    }

    static bool IsFolderExist(const string& folderPath)
    {
        struct stat info{};
        if (stat(folderPath.c_str(), &info) != 0) {
            return false;
        }
        return (info.st_mode & S_IFDIR);
    }

    int32_t AllocateMemory() const
    {
        int32_t clientId = 1;
        uint64_t memorySize = 100U;
        const tuple<int32_t, ErrorType> applyMemoryRes =
                resourceManager->HandleApplyMemory(clientId, memorySize);
        return get<0>(applyMemoryRes);
    }

    void CreateApplyPermissionBatch(PermissionType permissionType, int32_t memoryId, int32_t startId,
                                    int32_t count) const
    {
        for (int32_t permitApplicant = startId; permitApplicant < startId + count; permitApplicant++) {
            resourceManager->HandleApplyPermission(permitApplicant, permissionType, memoryId);
        }
    }
};

TEST_F(ResourceManagerTest, should_clean_up_ftok_file_path_when_init)
{
    std::string rootDir = "./tmp/";
    std::string filePath = rootDir + "test";
    if (mkdir(rootDir.data(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH) == -1) {
        perror("failed to create the tmp root directory");
    }
    if (mkdir(filePath.data(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH) == -1) {
        perror("failed to create the test subdirectory");
    }
    EXPECT_TRUE(IsFolderExist(filePath));
    resourceManager->Init();
    EXPECT_FALSE(IsFolderExist(filePath));
}

TEST_F(ResourceManagerTest, should_malloc_and_save_memory_info_when_handle_apply_memory_succeeds)
{
    int32_t clientId = 1;
    uint64_t memorySize = 100U;
    const tuple<int32_t, ErrorType> applyMemoryRes = resourceManager->HandleApplyMemory(clientId, memorySize);
    int32_t memoryId = get<0>(applyMemoryRes);
    ErrorType errorType = get<1>(applyMemoryRes);
    EXPECT_EQ(ErrorType::None, errorType);
    EXPECT_EQ(clientId, resourceManager->GetMemoryApplicant(memoryId));
    EXPECT_EQ(memorySize, resourceManager->GetMemorySize(memoryId));
    EXPECT_EQ(0, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetLastUsedTime(memoryId) > 0);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_permission_type_unknown)
{
    int32_t permissionApplicantId = 1;
    tuple<bool, uint64_t, ErrorType> applyPermitRes = resourceManager->HandleApplyPermission(permissionApplicantId,
                                                                                             PermissionType::None,
                                                                                             0);
    bool granted = get<0>(applyPermitRes);
    EXPECT_EQ(false, granted);
    ErrorType errorType = get<2>(applyPermitRes);
    EXPECT_EQ(ErrorType::UnknownPermissionType, errorType);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_memory_not_found)
{
    int32_t permissionApplicantId = 1;
    tuple<bool, uint64_t, ErrorType> applyPermitRes = resourceManager->HandleApplyPermission(permissionApplicantId,
                                                                                             PermissionType::Write,
                                                                                             0);
    bool granted = get<0>(applyPermitRes);
    EXPECT_EQ(false, granted);
    ErrorType errorType = get<2>(applyPermitRes);
    EXPECT_EQ(ErrorType::MemoryNotFound, errorType);
}

TEST_F(ResourceManagerTest, should_send_error_reponse_for_applying_permission_when_duplicate_applying_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory();

    // 首次申请权限
    int32_t permissionApplicantId = 2;
    tuple<bool, uint64_t, ErrorType> applyPermitRes = resourceManager->HandleApplyPermission(permissionApplicantId,
                                                                                             PermissionType::Write,
                                                                                             memoryId);
    // 断言首次申请成功
    bool granted1 = get<0>(applyPermitRes);
    EXPECT_EQ(true, granted1);
    ErrorType errorType1 = get<2>(applyPermitRes);
    EXPECT_EQ(ErrorType::None, errorType1);

    // 二次申请权限
    applyPermitRes = resourceManager->HandleApplyPermission(permissionApplicantId,
                                                            PermissionType::Write,
                                                            memoryId);
    // 断言二次申请失败
    bool granted2 = get<0>(applyPermitRes);
    EXPECT_EQ(false, granted2);
    ErrorType errorType2 = get<2>(applyPermitRes);
    EXPECT_EQ(ErrorType::DuplicatePermitApplication, errorType2);
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
    int32_t memoryId = AllocateMemory();
    // Client 2 申请权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission(permissionApplicantId, PermissionType::Write, memoryId);
    // Client 3 释放权限
    int32_t permissionReleaserId = 3;
    resourceManager->HandleReleasePermission(permissionReleaserId, PermissionType::Write, memoryId);
    // 断言释放失败
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    std::unordered_set<int32_t>& writingMemoryBlocks =
            resourceManager->GetWritingMemoryBlocks(permissionApplicantId);
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) != writingMemoryBlocks.end());
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_release_permission_when_permission_type_mismatched)
{
    // 分配内存
    int32_t memoryId = AllocateMemory();
    // 申请写权限
    int32_t permissionApplicantId = 2;
    resourceManager->HandleApplyPermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 释放读权限
    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Read, memoryId);
    // 断言释放写权限成功
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(permissionApplicantId).empty());
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_update_memory_status_when_handle_apply_and_release_permission_succeed)
{
    int32_t memoryApplicantId = 1;
    uint64_t memorySize = 100U;
    const tuple<int32_t, ErrorType> applyMemoryRes = resourceManager->HandleApplyMemory(memoryApplicantId, memorySize);
    int32_t memoryId = get<0>(applyMemoryRes);

    int32_t permissionApplicantId = 2;
    tuple<bool, uint64_t, ErrorType> applyPermitRes = resourceManager->HandleApplyPermission(permissionApplicantId,
                                                                                             PermissionType::Write,
                                                                                             memoryId);
    // 断言权限申请成功
    bool granted = get<0>(applyPermitRes);
    EXPECT_EQ(true, granted);
    ErrorType errorType = get<2>(applyPermitRes);
    EXPECT_EQ(ErrorType::None, errorType);
    // 断言内存管理状态修改成功
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    std::unordered_set<int32_t>& writingMemoryBlocks =
            resourceManager->GetWritingMemoryBlocks(permissionApplicantId);
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) != writingMemoryBlocks.end());
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_TRUE(resourceManager->GetWaitingPermitRequests(memoryId).empty());

    resourceManager->HandleReleasePermission(permissionApplicantId, PermissionType::Write, memoryId);
    // 断言内存管理状态修改成功
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(writingMemoryBlocks.find(memoryId) == writingMemoryBlocks.end());
    EXPECT_EQ(0, resourceManager->GetPermissionStatus(memoryId));
}

TEST_F(ResourceManagerTest, should_grant_read_permissions_after_release_write_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory();
    // 抢先申请写权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission(firstApplicant, PermissionType::Write, memoryId);
    // 批量申请读权限
    int32_t readStartId = 3;
    int32_t readCount = 10;
    CreateApplyPermissionBatch(PermissionType::Read, memoryId, readStartId, readCount);
    // 最后再申请写权限
    int32_t lastApplicant = readStartId + readCount;
    resourceManager->HandleApplyPermission(lastApplicant, PermissionType::Write, memoryId);
    // 释放写权限
    resourceManager->HandleReleasePermission(firstApplicant, PermissionType::Write, memoryId);
    vector<tuple<int32_t, uint64_t>> notificationQueue = resourceManager->ProcessWaitingPermitRequests(memoryId);
    // 所有等待的读权限请求得到处理，获取许可；最后加入的写权限请求进入等待队列
    EXPECT_EQ(readCount, notificationQueue.size());
    EXPECT_EQ(readCount, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(firstApplicant).empty());
    for (int32_t permitApplicant = readStartId; permitApplicant < readStartId + readCount; permitApplicant++) {
        std::unordered_set<int32_t>& memoryBlocks = resourceManager->GetReadingMemoryBlocks(permitApplicant);
        EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
    }
    EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(lastApplicant).empty());
    EXPECT_EQ(readCount, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_EQ(1, resourceManager->GetWaitingPermitRequests(memoryId).size());
}

TEST_F(ResourceManagerTest, should_grant_write_permission_after_release_read_permission)
{
    // 分配内存
    int32_t memoryId = AllocateMemory();
    // 抢先申请读权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission(firstApplicant, PermissionType::Read, memoryId);
    // 批量申请写权限
    int32_t writeStartId = 3;
    int32_t writeCount = 10;
    CreateApplyPermissionBatch(PermissionType::Write, memoryId, writeStartId, writeCount);
    // 释放读权限
    resourceManager->HandleReleasePermission(firstApplicant, PermissionType::Read, memoryId);
    vector<tuple<int32_t, uint64_t>> notificationQueue = resourceManager->ProcessWaitingPermitRequests(memoryId);
    // 只有第一个等待的写权限会被处理，获取许可；其他写权限继续在队列中等待
    EXPECT_EQ(1, notificationQueue.size());
    EXPECT_EQ(1, resourceManager->GetWritingRefCnt(memoryId));
    EXPECT_EQ(0, resourceManager->GetReadingRefCnt(memoryId));
    EXPECT_TRUE(resourceManager->GetReadingMemoryBlocks(firstApplicant).empty());
    std::unordered_set<int32_t> &memoryBlocks = resourceManager->GetWritingMemoryBlocks(writeStartId);
    EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
    for (int32_t permitApplicant = writeStartId + 1;
    permitApplicant < writeStartId + writeCount; permitApplicant++) {
        EXPECT_TRUE(resourceManager->GetWritingMemoryBlocks(permitApplicant).empty());
    }
    EXPECT_EQ(-1, resourceManager->GetPermissionStatus(memoryId));
    EXPECT_EQ(writeCount - 1, resourceManager->GetWaitingPermitRequests(memoryId).size());
}

class ApplyPermissionParamTest : public ResourceManagerTest,
        public testing::WithParamInterface<tuple<PermissionType, PermissionType>> {
protected:
    void AssertApplyMultipleReadPermits(int32_t memoryId, int32_t firstApplicant, int32_t startId, int32_t count) const
    {
        // 多读场景，所有申请均可成功
        EXPECT_EQ(count + 1, resourceManager->GetReadingRefCnt(memoryId));
        std::unordered_set<int32_t>& memoryBlocks =
                resourceManager->GetReadingMemoryBlocks(firstApplicant);
        EXPECT_TRUE(memoryBlocks.find(memoryId) != memoryBlocks.end());
        for (int32_t permitApplicant = startId; permitApplicant < startId + count; permitApplicant++) {
            memoryBlocks = resourceManager->GetReadingMemoryBlocks(permitApplicant);
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

        std::unordered_set<int32_t>& memoryBlocks = firstPermission == PermissionType::Read ?
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
    int32_t memoryId = AllocateMemory();
    // 抢先申请权限
    int32_t firstApplicant = 2;
    resourceManager->HandleApplyPermission(firstApplicant, firstPermission, memoryId);
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
