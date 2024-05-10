/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : encryptedable value
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#ifndef FIT_CONFIG_ENCRYPTEDABLE_VALUE_HPP
#define FIT_CONFIG_ENCRYPTEDABLE_VALUE_HPP

#include <fit/value.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include "config_writable_value.hpp"

namespace Fit {
namespace Config {
class EncryptedableValue : public StringValue {
public:
    explicit EncryptedableValue(string value);

    string AsString() const override;
    string AsString(const char*) const override;
    string AsString(const string&) const override;

    struct ValueInfo {
        bool isEncrypted;
        string content;
        string cryptoName;
    };
    /**
     * @brief parse the value's format
     *
     * @param v encrypted value format: fit-encrypted({decrypt-component-name}({content}))
     * @return ValueInfo
     */
    static ValueInfo Parse(const string& v);

protected:
    bool GetValue(string& v) const;
};
}
}
#endif