/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/23 20:05
 * Notes:       :
 */

#ifndef CONFIGURATION_CLIENT_H
#define CONFIGURATION_CLIENT_H

#include <cstdint>
#include <functional>
#include <memory>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

namespace Fit {
namespace Configuration {
struct ItemValue {
    Fit::string key;
    Fit::string value;
};

using ItemValueSet = Fit::vector<ItemValue>;

class ConfigurationClient : public Fit::RuntimeElementBase {
public:
    explicit ConfigurationClient() : Fit::RuntimeElementBase("configurationClient") {};
    ~ConfigurationClient() override = default;
    using ConfigSubscribePathCallback = std::function<void(const Fit::string &key, ItemValueSet &items)>;

    using ConfigSubscribeNodeCallback = std::function<void(const Fit::string &key, const Fit::string &value)>;

    // 获取当前key节点的值
    virtual int32_t Get(const Fit::string &key, Fit::string &value) = 0;
    // 获取当前key节点的所有子节点配置
    virtual int32_t Download(const Fit::string &key, ItemValueSet &out) = 0;
    // 是否订阅
    virtual bool IsSubscribed(const Fit::string &key) const = 0;
    // 订阅当前节点变化
    virtual int32_t Subscribe(const Fit::string &key, ConfigSubscribePathCallback cb) = 0;
    // 订阅当前节点值变化
    virtual int32_t Subscribe(const Fit::string &key, ConfigSubscribeNodeCallback cb) = 0;
};

using ConfigurationClientPtr = std::shared_ptr<ConfigurationClient>;
}
}
#endif // CONFIGURATION_CLIENT_H
