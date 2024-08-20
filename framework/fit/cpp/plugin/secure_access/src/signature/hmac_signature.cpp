/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#include <include/signature/hmac_signature.h>
#include <fit/fit_log.h>
#include <openssl/hmac.h>
#include <openssl/evp.h>
#include <include/secure_access_config.h>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>
#include <iomanip>
#include <sstream>
namespace Fit {
constexpr const uint64_t HASH_VALUE_WIDTH = 2;
HmacSignature::HmacSignature(AuthKeyRepoPtr authKeyRepo) : authKeyRepo_(std::move(authKeyRepo))
{
}

string HmacSignature::Sign(const string& ak, const string& timestamp)
{
    if (authKeyRepo_ == nullptr) {
        FIT_LOG_ERROR("Authorization repo is null.");
        return "";
    }
    vector<AuthKey> authKeys = authKeyRepo_->Query({ak});
    if (authKeys.empty()) {
        FIT_LOG_ERROR("No authorization, ak is %s.", ak.c_str());
        return "";
    }
    Fit::string sk = authKeys.front().sk;
    // 解密sk
    auto crypto = CryptoManager::Instance().Get(SecureAccessConfig::Instance().CryptoType());
    if (crypto != nullptr) {
        crypto->Decrypt(sk.c_str(), sk.length(), sk);
    } else {
        FIT_LOG_ERROR("Invalid crypto type %s.", SecureAccessConfig::Instance().CryptoType().c_str());
    }

    if (sk.empty()) {
        sk = authKeys.front().sk;
    }

    std::string message = Fit::to_std_string(ak + timestamp);
    unsigned char hash[EVP_MAX_MD_SIZE];
    unsigned int hashLen;
    HMAC_CTX* ctx = HMAC_CTX_new();
    HMAC_Init_ex(ctx, sk.c_str(), sk.length(), EVP_sha256(), NULL);
    HMAC_Update(ctx, reinterpret_cast<const unsigned char*>(message.c_str()), message.length());
    HMAC_Final(ctx, hash, &hashLen);
    HMAC_CTX_free(ctx);

    // 将二进制哈希值转换为十六进制字符串
    std::stringstream ss;
    for (unsigned int i = 0; i < hashLen; ++i) {
        ss << std::hex << std::setw(HASH_VALUE_WIDTH) << std::setfill('0') << static_cast<int>(hash[i]);
    }
    return Fit::to_fit_string(ss.str());
}

bool HmacSignature::Verify(const string& ak, const string& timestamp, const string& signature)
{
    return Sign(ak, timestamp) == signature;
}

SignaturePtr SignatureFactory::Create(AuthKeyRepoPtr authKeyRepo)
{
    return Fit::make_unique<HmacSignature>(authKeyRepo);
}
}