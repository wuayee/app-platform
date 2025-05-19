/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: test for common/utils/FileUtils
 */

#include <gtest/gtest.h>

#include "utils/FileUtils.h"

using namespace DataBus::Common;

namespace DataBus {
namespace Test {

class FileUtilsTest : public testing::Test {
};

TEST_F(FileUtilsTest, CreateDirectory)
{
    std::string testDir = "test_dir/sub_dir";
    FileUtils::CreateDirectory(testDir);
    struct stat info{};
    EXPECT_EQ(0, stat("test_dir", &info));
    EXPECT_TRUE(S_ISDIR(info.st_mode));
    EXPECT_EQ(0, stat("test_dir/sub_dir", &info));
    EXPECT_TRUE(S_ISDIR(info.st_mode));
    FileUtils::RemoveDirectory("test_dir");
}

TEST_F(FileUtilsTest, RemoveDirectory)
{
    std::string testDir = "test_dir";
    FileUtils::CreateDirectory(testDir);
    // 创建一个文件以测试递归删除
    std::ofstream(testDir + "/test_file.txt").close();
    FileUtils::CreateDirectory(testDir + "/sub_dir");
    std::ofstream(testDir + "/sub_dir/test_file2.txt").close();
    EXPECT_TRUE(FileUtils::RemoveDirectory(testDir));
    struct stat info{};
    EXPECT_NE(0, stat(testDir.c_str(), &info));
}

TEST_F(FileUtilsTest, CreateFileIfNotExists)
{
    std::string testFilePath = "test_dir/test_file.txt";
    FileUtils::CreateFileIfNotExists(testFilePath);
    std::ifstream fileCheck(testFilePath);
    EXPECT_TRUE(fileCheck.good());
    fileCheck.close();
    FileUtils::RemoveDirectory("test_dir");
}

TEST_F(FileUtilsTest, GetDataBusDirectory)
{
    std::string dataBusDir = FileUtils::GetDataBusDirectory();
    EXPECT_FALSE(dataBusDir.empty());
}
} // namespace Test
} // namespace DataBus
