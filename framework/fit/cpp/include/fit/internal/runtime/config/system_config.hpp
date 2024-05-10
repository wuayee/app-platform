/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#ifndef SYSTEM_CONFIG_HPP
#define SYSTEM_CONFIG_HPP

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/memory.hpp>
#include <fit/external/runtime/config/config_value.hpp>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Config {
class SystemConfig {
public:
    SystemConfig() = default;
    virtual ~SystemConfig() = default;
    /**
     *
     * @param key 可以使用的形式
     * 多层级key: a.b.c
     * 单层级key: a
     * @param value
     * @return 找到时返回FIT_OK, 否则返回FIT_ERR_NOT_FOUND
     */
    virtual Value& GetValue(const char* key) const = 0;
    virtual const string& GetWorkerId() const = 0;
    virtual const string& GetEnvName() const = 0;
    virtual const string& GetAppName() const = 0;
    virtual string GetAppVersion() const = 0;
};
using SystemConfigPtr = Fit::shared_ptr<SystemConfig>;
}
}
#endif // SYSTEM_CONFIG_HPP
