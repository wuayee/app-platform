/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: test for resource_manager/FtokArgsGenerator
 */

#include <sys/stat.h>

#include <gtest/gtest.h>

#include "ResourceManager.h"

using namespace std;
using namespace DataBus::Resource;

namespace DataBus {
namespace Test {
class ResourceManagerTest : public testing::Test {

public:
    std::unique_ptr<ResourceManager> resourceManager;

protected:
    void SetUp() override
    {
        resourceManager = std::make_unique<ResourceManager>();
    }

    void TearDown() override
    {
        resourceManager.reset();
    }

    static bool isFolderExist(const string& folderPath)
    {
        struct stat info{};
        if (stat(folderPath.c_str(), &info) != 0) {
            return false;
        }
        return (info.st_mode & S_IFDIR);
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
    EXPECT_TRUE(isFolderExist(filePath));
    resourceManager->Init();
    EXPECT_FALSE(isFolderExist(filePath));
}
}  // namespace Test
}  // namespace DataBus
