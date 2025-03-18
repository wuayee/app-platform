/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: test for resource_manager/FtokArgsGenerator
 */

#include <gtest/gtest.h>

#include "FtokArgsGenerator.h"

using namespace DataBus::Resource;

namespace DataBus {
namespace Test {
class FtokArgsGeneratorTest : public testing::Test {
public:
    std::unique_ptr<FtokArgsGenerator> generator;

protected:
    void SetUp() override
    {
        generator = std::make_unique<FtokArgsGenerator>();
    }

    void TearDown() override
    {
        generator.reset();
    }
};

TEST_F(FtokArgsGeneratorTest, should_return_correct_file_path_and_proj_id)
{
    for (int32_t i = MIN_PROJ_ID; i <= MAX_PROJ_ID; i++) {
        EXPECT_EQ(FILE_PATH_PREFIX + "0", generator->GetFilePath());
        EXPECT_EQ(i, generator->GetProjId());
    }
    EXPECT_EQ(FILE_PATH_PREFIX + "1", generator->GetFilePath());
    EXPECT_EQ(0, generator->GetProjId());
}
}  // namespace Test
}  // namespace DataBus
