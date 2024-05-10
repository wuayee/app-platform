/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#ifndef SYSTEM_CONFIG_INTERNAL_HPP
#define SYSTEM_CONFIG_INTERNAL_HPP

#include <rapidjson/document.h>
#include "system_config.hpp"

namespace Fit {
namespace Config {
class SystemConfigInternal : public SystemConfig {
public:
    virtual FitCode LoadFromFile(const char *configFile) = 0;
    virtual FitCode LoadFromString(const char *jsonString) = 0;
    /**
     *
     * @param key 可以使用的形式
     * 多层级key: a.b.c
     * 单层级key: a
     * @param value
     * @return 找到时返回FIT_OK, 否则返回FIT_ERR_NOT_FOUND
     */
    Value &GetValue(const char *key) const override = 0;
    /**
     *
     * @param key 可以使用的形式
     * 多层级key: a.b.c
     * 单层级key: a
     * @param value
     *          string -> "\"string\""
     *          int -> "123"
     *          double -> "123.33"
     *          bool -> "true/false"
     *          null -> "null"
     * @return 成功返回FIT_OK
     */
    virtual FitCode SetValue(const char *key, const char *value) = 0;
};
}
}
#endif // SYSTEM_CONFIG_INTERNAL_HPP
