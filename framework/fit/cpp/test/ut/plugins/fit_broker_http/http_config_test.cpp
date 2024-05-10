/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for fit http config
 * Author       : songyongtan
 * Create       : 2023-08-03
 * Notes:       :
 */

#include <gtest/gtest.h>

#include "fit_broker_http/http_config.hpp"

using namespace Fit;

TEST(FitBrokerHttpConfig, should_return_correct_config_when_get_given_path_and_protocol)
{
    string expectContextPath = "/contextPath";
    string expectPath = "/prefix";
    int32_t expectProtocol = 8;
    string expectCer = "cer";
    string expectKey = "key";
    string expectPwd = "pwd";
    string caCrtPath = "ca.crt";
    string keyPwdFilePath = "encryptedPwd.txt";
    string cryptoType = "scc";
    HttpConfig config(expectContextPath, expectPath, expectProtocol, true, expectCer, expectKey, expectPwd, caCrtPath,
        keyPwdFilePath, cryptoType);
    ASSERT_EQ(config.GetServerPath(), expectContextPath + expectPath);
    ASSERT_EQ(config.GetProtocol(), expectProtocol);
    ASSERT_EQ(config.IsEnableSSLVerify(), true);
    ASSERT_EQ(config.GetCerPath(), expectCer);
    ASSERT_EQ(config.GetPrivateKeyPath(), expectKey);
    ASSERT_EQ(config.GetPrivateKeyPwd(), expectPwd);
    ASSERT_EQ(config.GetCaCrtPath(), caCrtPath);
    ASSERT_EQ(config.GetPrivateKeyPwdFilePath(), keyPwdFilePath);
    ASSERT_EQ(config.GetCryptoType(), cryptoType);
}
TEST(FitBrokerHttpConfig, should_return_correct_path_when_GetClientPath_given_empty_context_path)
{
    string expectPath = "/prefix/gid/fid";
    HttpConfig config("/contextPath", "/prefix", 1);
    auto ret = config.GetClientPath("", "gid", "fid");
    ASSERT_EQ(ret, expectPath);
}
TEST(FitBrokerHttpConfig, should_return_correct_path_when_GetClientPath_given_valid_context_path)
{
    string expectContextPath = "/contextPath";
    string expectPath = "/prefix/gid/fid";
    HttpConfig config("/default", "/prefix", 1);
    auto ret = config.GetClientPath(expectContextPath, "gid", "fid");
    ASSERT_EQ(ret, expectContextPath + expectPath);
}