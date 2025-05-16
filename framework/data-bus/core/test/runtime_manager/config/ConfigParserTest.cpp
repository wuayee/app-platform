/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: test for runtime_manager/config/ConfigParser
 */

#include <gtest/gtest.h>

#include "config/ConfigParser.h"
#include "utils/FileUtils.h"
#include "Constants.h"

using namespace DataBus::Common;
using namespace DataBus::Runtime;

namespace DataBus {
namespace Test {

class ConfigParserTest : public testing::Test {
};

TEST_F(ConfigParserTest, should_return_default_config_when_config_file_not_exists)
{
    std::string nonexistentTestFile = "./testConfig.json";
    Config config = ConfigParser::Parse(nonexistentTestFile);

    EXPECT_EQ(DataBus::Common::DEFAULT_PORT, config.GetPort());
    EXPECT_EQ(DataBus::Common::DEFAULT_MALLOC_SIZE_LIMIT, config.GetMallocSizeLimit());
    EXPECT_EQ(DataBus::Common::DEFAULT_MEMORY_TTL_DURATION, config.GetMemoryTtlDuration());
    EXPECT_EQ(DataBus::Common::DEFAULT_MEMORY_SWEEP_INTERVAL, config.GetMemorySweepInterval());
}

TEST_F(ConfigParserTest, should)
{
    // 手动创建测试配置文件
    std::string testFile = "./testConfig.json";
    FileUtils::CreateFileIfNotExists(testFile);
    std::ofstream configFile(testFile);
    std::string configStr = R"({
        "server": {
                "port": 1234
        },
        "memory": {
                "sizeLimit": 200,
                "ttlDuration": 60000,
                "sweepInterval": 30000
        }
    })";
    configFile << configStr;
    configFile.close();
    // 解析配置文件
    Config config = ConfigParser::Parse(testFile);

    const int expectedPort = 1234;
    const uint64_t expectedMallocSizeLimit = 200;
    const int32_t expectedMemoryTtlDuration = 60 * 1000;
    const int32_t expectedMemorySweepInterval = 30 * 1000;
    EXPECT_EQ(expectedPort, config.GetPort());
    EXPECT_EQ(expectedMallocSizeLimit, config.GetMallocSizeLimit());
    EXPECT_EQ(expectedMemoryTtlDuration, config.GetMemoryTtlDuration());
    EXPECT_EQ(expectedMemorySweepInterval, config.GetMemorySweepInterval());
    // 删除测试配置文件
    remove(testFile.c_str());
}
} // namespace Test
} // namespace DataBus
