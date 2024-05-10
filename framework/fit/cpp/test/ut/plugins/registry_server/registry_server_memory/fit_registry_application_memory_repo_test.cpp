/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for registry application memory repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include <registry_server/registry_server_memory/fitable/fit_registry_application_memory_repo.h>
#include <gtest/gtest.h>

using namespace ::testing;
using namespace Fit;

class FitRegistryApplicationMemoryRepoTest : public Test {
public:
    void SetUp()
    {
        meta1_.id.name = "name";
        meta1_.id.nameVersion = "nameVersion";
        meta1_.extensions = {{"key", "value"}, {"key1", "value1"}};
        meta2_.id.name = "name2";
        meta2_.id.nameVersion = "nameVersion2";
        meta2_.extensions = {{"key1", "value1"}, {"key1", "value1"}};
    }

protected:
    RegistryApplicationRepo::ApplicationMeta meta1_;
    RegistryApplicationRepo::ApplicationMeta meta2_;
    RegistryApplicationMemoryRepo target_;
};

TEST_F(FitRegistryApplicationMemoryRepoTest, should_return_app_meta_when_Query_given_exist_app_id)
{
    auto expectMeta = meta1_;

    target_.Save(meta1_);
    target_.Save(meta2_);
    RegistryApplicationRepo::ApplicationMeta result;
    auto queryRet = target_.Query(expectMeta.id, result);

    ASSERT_EQ(queryRet, FIT_OK);
    EXPECT_TRUE(result.Equals(expectMeta));
}

TEST_F(FitRegistryApplicationMemoryRepoTest, should_return_all_app_meta_when_QueryAll)
{
    target_.Save(meta1_);
    target_.Save(meta2_);
    auto result = target_.QueryAll();

    ASSERT_EQ(result.size(), 2);
    EXPECT_TRUE(result[0].Equals(meta1_) || result[0].Equals(meta2_));
    EXPECT_TRUE(result[1].Equals(meta1_) || result[1].Equals(meta2_));
}

TEST_F(FitRegistryApplicationMemoryRepoTest, should_return_remain_app_meta_when_QueryAll_given_delete_some_meta)
{
    auto expectMeta = meta2_;

    target_.Save(meta1_);
    target_.Save(meta2_);
    target_.Delete(meta1_.id);
    auto result = target_.QueryAll();

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(expectMeta));
}

TEST_F(FitRegistryApplicationMemoryRepoTest, should_return_app_metas_when_Query_given_exist_name)
{
    auto samedNameMeta = meta1_;
    samedNameMeta.id.nameVersion = "otherNameVersion";

    target_.Save(meta1_);
    target_.Save(samedNameMeta);
    target_.Save(meta2_);
    auto result = target_.Query(samedNameMeta.id.name);

    ASSERT_EQ(result.size(), 2);
    EXPECT_TRUE(result[0].Equals(samedNameMeta) || result[0].Equals(meta1_));
    EXPECT_TRUE(result[1].Equals(samedNameMeta) || result[1].Equals(meta1_));
}

TEST_F(FitRegistryApplicationMemoryRepoTest,
    should_return_app_metas_when_Query_given_exist_name_and_remove_some_meta)
{
    auto samedNameMeta = meta1_;
    samedNameMeta.id.nameVersion = "otherNameVersion";

    target_.Save(meta1_);
    target_.Save(samedNameMeta);
    target_.Save(meta2_);
    target_.Delete(meta1_.id);
    auto result = target_.Query(samedNameMeta.id.name);

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(samedNameMeta));
}