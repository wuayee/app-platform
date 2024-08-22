/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <secure_access/include/repo/default_auth_key_repo.h>
#include <fit/fit_code.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class DefaultAuthKeyRepoTest : public ::testing::Test {
public:
    void SetUp() override
    {
        authKeyRepo_ = make_shared<DefaultAuthKeyRepo>();
        ak_ = "test_ak";
        authKey_.ak = ak_;
        authKey_.sk = "test_sk";
        authKey_.role = "provider";
        authKey2_.ak = ak_;
        authKey2_.sk = "test_sk2";
        authKey2_.role = "provider";
    }

    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<DefaultAuthKeyRepo> authKeyRepo_ {};
    Fit::string ak_ {};
    Fit::string sk_ {};
    AuthKey authKey_ {};
    AuthKey authKey2_ {};
};

TEST_F(DefaultAuthKeyRepoTest, should_return_auth_keys_when_save_and_query_given_param)
{
    // given
    vector<AuthKey> authKeys {authKey_};

    // when
    int32_t saveRet = authKeyRepo_->Save(authKeys);
    vector<AuthKey> actualAuthKeys = authKeyRepo_->Query({authKey_.ak});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthKeys.front(), authKey_);
}

TEST_F(DefaultAuthKeyRepoTest, should_return_empty_when_save_remove_and_query_given_param)
{
    // given
    vector<AuthKey> authKeys {authKey_};

    // when
    int32_t saveRet = authKeyRepo_->Save(authKeys);
    vector<AuthKey> actualAuthKeys = authKeyRepo_->Query({authKey_.ak});
    int32_t removeRet = authKeyRepo_->Remove({authKey_.ak});
    vector<AuthKey> actualAuthKeysAfterRemove = authKeyRepo_->Query({authKey_.ak});

    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthKeys.front(), authKey_);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualAuthKeysAfterRemove.empty(), true);
}

TEST_F(DefaultAuthKeyRepoTest, should_return_auth_key2_when_save_and_update_and_query_given_param)
{
    // given
    vector<AuthKey> authKeys {authKey_};
    vector<AuthKey> authKeys2 {authKey2_};

    // when
    int32_t saveRet = authKeyRepo_->Save(authKeys);
    vector<AuthKey> actualAuthKeys = authKeyRepo_->Query({authKey_.ak});
    int32_t saveRet2 = authKeyRepo_->Save(authKeys2);
    vector<AuthKey> actualAuthKeysAfterUpdate = authKeyRepo_->Query({authKey_.ak});

    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthKeys.front(), authKey_);
    EXPECT_EQ(saveRet2, FIT_OK);
    EXPECT_EQ(actualAuthKeysAfterUpdate.front(), authKey2_);
}