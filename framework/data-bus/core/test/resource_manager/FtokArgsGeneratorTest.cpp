/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
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
        EXPECT_EQ(generator->GetFilePath(), "./tmp/0");
        EXPECT_EQ(generator->GetProjId(), i);
    }
    EXPECT_EQ(generator->GetFilePath(), "./tmp/1");
    EXPECT_EQ(generator->GetProjId(), 0);
}
}  // namespace Test
}  // namespace DataBus
