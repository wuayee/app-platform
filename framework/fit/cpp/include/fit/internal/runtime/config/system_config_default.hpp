/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#ifndef SYSTEM_CONFIG_DEFAULT_HPP
#define SYSTEM_CONFIG_DEFAULT_HPP

#include <rapidjson/document.h>
#include <fit/stl/map.hpp>
#include <fit/stl/vector.hpp>
#include <functional>
#include <fit/internal/runtime/runtime_element.hpp>
#include "config_writable_value.hpp"
#include "system_config_internal.hpp"
#include "config_value_rapidjson.hpp"

namespace Fit {
namespace Config {
class SystemConfigDefault : public SystemConfigInternal, public RuntimeElementBase {
public:
    SystemConfigDefault();
    SystemConfigDefault(const string& configFile, const map<string, string> &options);
    ~SystemConfigDefault() override = default;
    bool Start() override;

    FitCode LoadFromFile(const char *configFile) final;
    FitCode PutItems(const Fit::map<Fit::string, Fit::string> &items);
    FitCode LoadFromString(const char *jsonString) final;
    /**
     *
     * @param key
     * key1对应json格式
     * {
     *   "key1" : "value"
     * }
     * key1.key2.key3对应json格式
     * {
     *   "key1" : {
     *     "key2" : {
     *       "key3" : "value"
     *   }
     * }
     * @param value
     * @return
     */
    Value &GetValue(const char *key) const override;
    const string& GetWorkerId() const override;
    const string& GetEnvName() const override;
    const string& GetAppName() const override;
    string GetAppVersion() const override;
    /**
     *
     * @param key
     * key1对应json格式
     * {
     *   "key1" : "value"
     * }
     * key1.key2.key3对应json格式
     * {
     *   "key1" : {
     *     "key2" : {
     *       "key3" : "value"
     *   }
     * }
     * @param value
     *          string -> "\"string\""
     *          int -> "123"
     *          double -> "123.33"
     *          bool -> "true/false"
     * @return
     */
    FitCode SetValue(const char *key, const char *value) override;

protected:
    static bool IsStringValue(const Fit::string &value) noexcept;
    static bool TrySetBoolValue(WritableValue *container, const Fit::string &value);
    static bool TrySetIntValue(WritableValue *container, const Fit::string &value);
    static bool TrySetDoubleValue(WritableValue *container, const Fit::string &value);
    static bool TrySetStringValue(WritableValue *container, const Fit::string &value);
    static bool TrySetDefaultStringValue(WritableValue *container, const Fit::string &value);
    static bool TrySetNullValue(WritableValue *container, const Fit::string &value);

    static bool TrySetValue(WritableValue *container, const Fit::string &value);

private:
    std::unique_ptr<WritableValue> config_;
    mutable string workerId_;
    mutable string envName_;
    mutable string appName_;
};
}
}

#endif // SYSTEM_CONFIG_DEFAULT_HPP
