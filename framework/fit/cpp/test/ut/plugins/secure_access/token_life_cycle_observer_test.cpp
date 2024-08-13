/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <fit/external/plugin/plugin_config.hpp>
#include <secure_access/include/secure_access.h>
#include <secure_access/include/token_life_cycle_observer.h>
#include <mock/token_role_repo_mock.hpp>
#include <mock/time_util_by_repo_mock.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class TokenLifeCycleObserverTest : public ::testing::Test {
public:
    void SetUp() override
    {
        tokenLifeCycleObserver_ = new Fit::TokenLifeCycleObserver(&Fit::SecureAccess::Instance());
        tokenRoleRepo_ = make_shared<TokenRoleRepoMock>();
        timeUtilByRepoMock_ = make_shared<TimeUtilByRepoMock>();
        uint64_t timeoutIn = 100;
        accessTokenRole_ = AuthTokenRole("accessToken", ACCESS_TOKEN_TYPE, timeoutIn,
            600 + timeoutIn, "provider");
    }

    void TearDown() override
    {
        delete tokenLifeCycleObserver_;
        tokenLifeCycleObserver_ = nullptr;
    }
public:
    TokenLifeCycleObserver* tokenLifeCycleObserver_ {};
    Fit::shared_ptr<TokenRoleRepoMock> tokenRoleRepo_ {};
    Fit::shared_ptr<TimeUtilByRepoMock> timeUtilByRepoMock_ {};
    AuthTokenRole accessTokenRole_ {};
};

TEST_F(TokenLifeCycleObserverTest, should_return_error_when_init_given_null_secure_access)
{
    // given
    TokenLifeCycleObserver tokenLifeCycleObserver(nullptr);

    // when
     int32_t ret = tokenLifeCycleObserver.Init();

    // then
    EXPECT_EQ(ret, FIT_ERR_FAIL);
}

TEST_F(TokenLifeCycleObserverTest, should_return_ok_when_init_and_uninit_given_secure_access)
{
    // given
    // when
    int32_t initRet = tokenLifeCycleObserver_->Init();
    int32_t uninitRet = tokenLifeCycleObserver_->Uninit();

    // then
    EXPECT_EQ(initRet, FIT_OK);
    EXPECT_EQ(uninitRet, FIT_OK);
}

TEST_F(TokenLifeCycleObserverTest, should_return_ok_when_exec_given_secure_access)
{
    // given
    SecureAccess* secureAccess = new SecureAccess(nullptr, tokenRoleRepo_, nullptr, nullptr,
        timeUtilByRepoMock_.get());
    TokenLifeCycleObserver tokenLifeCycleObserver(secureAccess);

    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepo_, QueryAll())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));
    EXPECT_CALL(*tokenRoleRepo_, Remove(_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));

    uint64_t curTime = 800;
    EXPECT_CALL(*timeUtilByRepoMock_, GetCurrentTimeMs(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<0>(curTime), testing::Return(FIT_OK)));

    // when
    int32_t ret = tokenLifeCycleObserver.Exec();

    // then
    EXPECT_EQ(ret, FIT_OK);
    delete secureAccess;
    secureAccess = nullptr;
}

TEST_F(TokenLifeCycleObserverTest, should_return_error_when_exec_given_token_repo_null)
{
    // given
    SecureAccess* secureAccess = new SecureAccess(nullptr, nullptr, nullptr, nullptr,
        timeUtilByRepoMock_.get());
    TokenLifeCycleObserver tokenLifeCycleObserver(secureAccess);

    // when
    int32_t ret = tokenLifeCycleObserver.Exec();

    // then
    EXPECT_EQ(ret, FIT_ERR_FAIL);
    delete secureAccess;
    secureAccess = nullptr;
}

TEST_F(TokenLifeCycleObserverTest, should_return_error_when_exec_given_time_util_by_repo_null)
{
    // given
    SecureAccess* secureAccess = new SecureAccess(nullptr, tokenRoleRepo_, nullptr, nullptr, nullptr);
    TokenLifeCycleObserver tokenLifeCycleObserver(secureAccess);

    // when
    int32_t ret = tokenLifeCycleObserver.Exec();

    // then
    EXPECT_EQ(ret, FIT_ERR_FAIL);
    delete secureAccess;
    secureAccess = nullptr;
}

TEST_F(TokenLifeCycleObserverTest, should_return_error_when_exec_given_mock_get_cur_time_failed)
{
    // given
    SecureAccess* secureAccess = new SecureAccess(nullptr, tokenRoleRepo_, nullptr, nullptr,
        timeUtilByRepoMock_.get());
    TokenLifeCycleObserver tokenLifeCycleObserver(secureAccess);

    uint64_t curTime = 800;
    EXPECT_CALL(*timeUtilByRepoMock_, GetCurrentTimeMs(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<0>(curTime), testing::Return(FIT_ERR_FAIL)));

    // when
    int32_t ret = tokenLifeCycleObserver.Exec();

    // then
    EXPECT_EQ(ret, FIT_ERR_FAIL);
    delete secureAccess;
    secureAccess = nullptr;
}