/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/03
 * Notes:       :
 */
#include <memory>
#include <fit_code.h>
#include <fit/fit_log.h>
#include <plugin/db_info_manager_for_kms/db_info_manager_for_kms.h>
#include "mock/encryption_client_mock.hpp"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class DbInfoManagerForKmsTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitDbInfoManager_ = std::make_shared<DbInfoManagerForKMS>();

        dbInfo_.module = "module1";
        dbInfo_.userName = "user_name";
        dbInfo_.password = "encrypt_pass_word";

        dbInfo2_.module = "module2";
        dbInfo2_.userName = "user_name2";
        dbInfo2_.password = "encrypt_pass_word2";

        configInfo_.dbInfos.emplace_back(dbInfo_);
        configInfo_.dbInfos.emplace_back(dbInfo2_);
        configInfo_.authIp = "test_auth";
        configInfo_.hisIp = "test_his";
        configInfo_.appId = "test_app_id";
        configInfo_.staticToken = "test_static_token";
    }

    void TearDown() override
    {
    }
public:
    DbInfo dbInfo_;
    DbInfo dbInfo2_;
    ConfigInfo configInfo_;
    std::shared_ptr<EncryptionClientMock> encryptionClient_ {nullptr};
    Fit::shared_ptr<FitDbInfoManager> fitDbInfoManager_ {nullptr};
};

TEST_F(DbInfoManagerForKmsTest, should_return_same_config_when_set_and_get_given_config)
{
    // given
    // when
    DbInfoManagerForKMS::SetConfigInfo(configInfo_);
    ConfigInfo actualConfigInfo = DbInfoManagerForKMS::GetConfigInfo();
    // then
    EXPECT_EQ(actualConfigInfo.authIp, configInfo_.authIp);
    EXPECT_EQ(actualConfigInfo.hisIp, configInfo_.hisIp);
    EXPECT_EQ(actualConfigInfo.dbInfos[0].module, configInfo_.dbInfos[0].module);
    EXPECT_EQ(actualConfigInfo.dbInfos[0].userName, configInfo_.dbInfos[0].userName);
    EXPECT_EQ(actualConfigInfo.dbInfos[0].password, configInfo_.dbInfos[0].password);
    EXPECT_EQ(actualConfigInfo.appId, configInfo_.appId);
    EXPECT_EQ(actualConfigInfo.staticToken, configInfo_.staticToken);
}

TEST_F(DbInfoManagerForKmsTest, should_fit_db_instance_when_instance_given_empty)
{
    // given
    // when
    Fit::shared_ptr<FitDbInfoManager> fitDbInfoInstance = FitDbInfoManager::Instance();
    // then
    EXPECT_NE(fitDbInfoInstance, nullptr);
}