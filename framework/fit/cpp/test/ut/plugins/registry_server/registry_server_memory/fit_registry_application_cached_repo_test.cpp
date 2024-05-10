/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for registry application cached repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include <registry_server/registry_server_memory/fitable/fit_registry_application_cached_repo.h>
#include <gtest/gtest.h>
#include "mock/registry_application_repo_mock.hpp"
#include "registry/repository/fit_registry_application_repo.h"
#include "stl/memory.hpp"

using namespace ::testing;
using namespace Fit;

class FitRegistryApplicationCachedRepoTest : public Test {
public:
    void SetUp()
    {
        auto cache = make_unique<RegistryApplicationRepoMock>();
        auto backend = make_unique<RegistryApplicationRepoMock>();
        cacheRepo_ = cache.get();
        backendRepo_ = backend.get();
        vector<unique_ptr<RegistryApplicationRepo>> repos;
        repos.reserve(2);
        repos.emplace_back(move(cache));
        repos.emplace_back(move(backend));
        target_ = make_unique<RegistryApplicationCachedRepo>(move(repos));
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
    unique_ptr<RegistryApplicationCachedRepo> target_;
    RegistryApplicationRepoMock* cacheRepo_;
    RegistryApplicationRepoMock* backendRepo_;
};

TEST_F(FitRegistryApplicationCachedRepoTest,
    should_return_app_meta_and_save_when_Query_given_app_id_not_in_cache_but_in_backend)
{
    auto expectMeta = meta1_;

    auto metaMatcher =
        Truly([&expectMeta](const RegistryApplicationRepo::ApplicationMeta& v) { return expectMeta.Equals(v); });
    auto appMathcer =
        Truly([&expectMeta](const RegistryApplicationRepo::Application& v) { return expectMeta.id.Equals(v); });
    EXPECT_CALL(*cacheRepo_, Query(appMathcer, _)).WillOnce(Return(FIT_ERR_NOT_FOUND));
    EXPECT_CALL(*cacheRepo_, Save(metaMatcher)).Times(1);
    EXPECT_CALL(*backendRepo_, Query(appMathcer, _)).WillOnce(DoAll(SetArgReferee<1>(expectMeta), Return(FIT_OK)));
    RegistryApplicationRepo::ApplicationMeta result;
    auto queryRet = target_->Query(expectMeta.id, result);

    ASSERT_EQ(queryRet, FIT_OK);
    EXPECT_TRUE(result.Equals(expectMeta));
}

TEST_F(FitRegistryApplicationCachedRepoTest, should_return_app_meta_when_Query_given_app_id_in_cache)
{
    auto expectMeta = meta1_;

    EXPECT_CALL(*cacheRepo_, Query(_, _)).WillOnce(DoAll(SetArgReferee<1>(expectMeta), Return(FIT_OK)));
    EXPECT_CALL(*backendRepo_, Query(_, _)).Times(0);
    RegistryApplicationRepo::ApplicationMeta result;
    auto queryRet = target_->Query(expectMeta.id, result);

    ASSERT_EQ(queryRet, FIT_OK);
    EXPECT_TRUE(result.Equals(expectMeta));
}

TEST_F(FitRegistryApplicationCachedRepoTest,
    should_return_app_meta_and_save_when_Query_given_app_name_not_in_cache_but_in_backend)
{
    auto expectMeta = meta1_;
    vector<RegistryApplicationRepo::ApplicationMeta> emptyMetas;
    vector<RegistryApplicationRepo::ApplicationMeta> expectMetas {expectMeta};

    auto metaMatcher =
        Truly([&expectMeta](const RegistryApplicationRepo::ApplicationMeta& v) { return expectMeta.Equals(v); });
    EXPECT_CALL(*cacheRepo_, Query(Eq(expectMeta.id.name))).WillOnce(Return(emptyMetas));
    EXPECT_CALL(*cacheRepo_, Save(metaMatcher)).Times(1);
    EXPECT_CALL(*backendRepo_, Query(Eq(expectMeta.id.name))).WillOnce(Return(expectMetas));
    auto result = target_->Query(expectMeta.id.name);

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(expectMeta));
}

TEST_F(FitRegistryApplicationCachedRepoTest, should_return_app_meta_when_Query_given_app_name_in_cache)
{
    auto expectMeta = meta1_;
    vector<RegistryApplicationRepo::ApplicationMeta> expectMetas {expectMeta};

    EXPECT_CALL(*cacheRepo_, Query(_)).WillOnce(Return(expectMetas));
    EXPECT_CALL(*backendRepo_, Query(_)).Times(0);
    auto result = target_->Query(expectMeta.id.name);

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(expectMeta));
}

TEST_F(FitRegistryApplicationCachedRepoTest,
    should_save_from_all_repo_when_Save_given_app_and_cache_and_backend_return_ok)
{
    auto expectMeta = meta1_;

    auto metaMatcher =
        Truly([&expectMeta](const RegistryApplicationRepo::ApplicationMeta& v) { return expectMeta.Equals(v); });
    EXPECT_CALL(*cacheRepo_, Save(metaMatcher)).WillOnce(Return(FIT_OK));
    EXPECT_CALL(*backendRepo_, Save(metaMatcher)).WillOnce(Return(FIT_OK));
    auto ret = target_->Save(expectMeta);

    ASSERT_EQ(ret, FIT_OK);
}

TEST_F(FitRegistryApplicationCachedRepoTest,
    should_delete_from_all_repo_when_Delete_given_app_and_cache_and_backend_return_ok)
{
    auto expectMeta = meta1_;

    auto appMathcer =
        Truly([&expectMeta](const RegistryApplicationRepo::Application& v) { return expectMeta.id.Equals(v); });
    EXPECT_CALL(*cacheRepo_, Delete(appMathcer)).WillOnce(Return(FIT_OK));
    EXPECT_CALL(*backendRepo_, Delete(appMathcer)).WillOnce(Return(FIT_OK));
    RegistryApplicationRepo::ApplicationMeta result;
    auto ret = target_->Delete(expectMeta.id);

    ASSERT_EQ(ret, FIT_OK);
}

TEST_F(FitRegistryApplicationCachedRepoTest,
    should_return_app_meta_and_save_when_QueryAll_given_cache_return_empty_and_backend_not_empty)
{
    auto expectMeta = meta1_;
    vector<RegistryApplicationRepo::ApplicationMeta> emptyMetas;
    vector<RegistryApplicationRepo::ApplicationMeta> expectMetas {expectMeta};

    auto metaMatcher =
        Truly([&expectMeta](const RegistryApplicationRepo::ApplicationMeta& v) { return expectMeta.Equals(v); });
    EXPECT_CALL(*cacheRepo_, QueryAll()).WillOnce(Return(emptyMetas));
    EXPECT_CALL(*cacheRepo_, Save(metaMatcher)).Times(1);
    EXPECT_CALL(*backendRepo_, QueryAll()).WillOnce(Return(expectMetas));
    auto result = target_->QueryAll();

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(expectMeta));
}

TEST_F(FitRegistryApplicationCachedRepoTest, should_return_app_meta_when_QueryAll_given_cache_not_empty)
{
    auto expectMeta = meta1_;
    vector<RegistryApplicationRepo::ApplicationMeta> expectMetas {expectMeta};

    EXPECT_CALL(*cacheRepo_, QueryAll()).WillOnce(Return(expectMetas));
    EXPECT_CALL(*backendRepo_, QueryAll()).Times(0);
    auto result = target_->QueryAll();

    ASSERT_EQ(result.size(), 1);
    EXPECT_TRUE(result[0].Equals(expectMeta));
}