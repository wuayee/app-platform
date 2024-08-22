/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide scc client.
 * Author       : w00561424
 * Date:        : 2024/03/18
 */

#include <fit/fit_code.h>
#include <fit/fit_log.h>
#include <fstream>
#include <include/sc_cryptoapi.h>
#include <include/sc_errcode.h>
#include <fit/securec.h>
#include "scc_client.h"
namespace Fit {
int32_t SccClient::Init()
{
    int ret = SEC_SUCCESS;
    if (configFile_.empty()) {
        FIT_LOG_ERROR("Configure file is empty.");
        return FIT_ERR_FAIL;
    }
    // 采用组件配置文件初始化组件
    char* cfgFileStr = new char[configFile_.length() + 1];
    memcpy_s(cfgFileStr, configFile_.length() + 1, configFile_.c_str(), configFile_.length());
    cfgFileStr[configFile_.length()] = '\0';
    ret = SCC_Initialize(cfgFileStr);
    if (ret != SEC_SUCCESS && ret != SEC_ERR_INIT_KMC_HARD_FAIL) {
        FIT_LOG_ERROR("Failed to initialize %s, ErrorCode=%d.", configFile_.c_str(), ret);
        delete cfgFileStr;
        cfgFileStr = nullptr;
        return FIT_ERR_FAIL;
    }
    delete cfgFileStr;
    cfgFileStr = nullptr;
    return FIT_OK;
}

int32_t SccClient::Encrypt(const Fit::string& src, Fit::string& dst)
{
    FIT_LOG_ERROR("Not support.");
    return FIT_ERR_NOT_SUPPORT;
}

int32_t SccClient::Decrypt(const Fit::string& src, Fit::string& dst)
{
    char *plainPassword = nullptr;
    int plainLen = 0;
    // 解密
    int ret = SCC_Decrypt(src.c_str(), src.length(), &plainPassword, &plainLen);
    // 释放加密结果内存
    if (ret == SEC_SUCCESS) {
        dst = Fit::string(plainPassword, plainLen);
        FIT_LOG_DEBUG("Decrypt success.");
        // 释放解密结果内存
        if (plainPassword != nullptr) {
            free(plainPassword);
            plainPassword = nullptr;
        }
    } else {
        FIT_LOG_ERROR("Failed to decrypt, ret =%d.", ret);
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}

Fit::shared_ptr<SccClient> SccClient::Create(const string& configFile)
{
    return Fit::make_shared<SccClient>(configFile);
}
}