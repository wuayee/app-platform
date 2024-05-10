/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for encrypted value
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#include "config_encryptedable_value.hpp"

#include <regex>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>

namespace Fit {
namespace Config {
EncryptedableValue::EncryptedableValue(string value) : StringValue(move(value)) {}

string EncryptedableValue::AsString() const
{
    string result;
    if (!GetValue(result)) {
        FIT_THROW_EXCEPTION1(std::runtime_error, "can not get the value");
    }
    return result;
}
string EncryptedableValue::AsString(const char* defaultValue) const
{
    string result;
    if (!GetValue(result)) {
        return defaultValue;
    }
    return result;
}
string EncryptedableValue::AsString(const string& defaultValue) const
{
    string result;
    if (!GetValue(result)) {
        return defaultValue;
    }
    return result;
}
EncryptedableValue::ValueInfo EncryptedableValue::Parse(const string& v)
{
    ValueInfo result {false};
    const std::regex rule("fit-encrypted\\((.*)\\((.*)\\)\\)$");
    std::smatch matches;
    std::regex_match(v, matches, rule);
    // if matched, mathces[0] is the whole s, mathces[1] is the type, mathces[2] is the content,
    constexpr uint32_t expectResult = 3;
    if (matches.size() != expectResult) {
        return result;
    }
    result.isEncrypted = true;
    result.cryptoName = to_fit_string(matches[1].str()); // 1 is the type
    result.content = to_fit_string(matches[2].str());    // 2 is content
    return result;
}
bool EncryptedableValue::GetValue(string& v) const
{
    auto data = StringValue::AsString();
    auto encryptInfo = Parse(data);
    if (!encryptInfo.isEncrypted) {
        v = move(data);
        return true;
    }
    auto crypto = CryptoManager::Instance().Get(encryptInfo.cryptoName.c_str());
    if (!crypto) {
        FIT_LOG_ERROR("Can not find the crypto, name=%s.", encryptInfo.cryptoName.c_str());
        return false;
    }
    FIT_LOG_DEBUG("Find the crypto, name=%s.", encryptInfo.cryptoName.c_str());
    string decryptedValue;
    auto ret = crypto->Decrypt(encryptInfo.content.data(), encryptInfo.content.size(), decryptedValue);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to decrypt, ret=%x, name=%s.", ret, encryptInfo.cryptoName.c_str());
        return false;
    }
    v = move(decryptedValue);
    return true;
}
}
} // LCOV_EXCL_LINE