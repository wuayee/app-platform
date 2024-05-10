/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/9/2 16:19
 */
#ifndef KEYVALUE_CONFIG_SERVICE_HPP
#define KEYVALUE_CONFIG_SERVICE_HPP

#include <fit/stl/string.hpp>
#include <memory>
#include <functional>

namespace Fit {
namespace Configuration {
class KeyValueConfigService {
public:
    using SubscribeCallBack = std::function<void(Fit::string, Fit::string)>;
    virtual ~KeyValueConfigService() = default;

    // 根据key获取value
    virtual int32_t Get(const Fit::string &key, Fit::string &value) = 0;
    // Set 一组kv到缓存
    virtual void Set(const Fit::string &key, const Fit::string &value) = 0;
    // // 支持key值订阅
    virtual void Subscribe(const Fit::string &key, SubscribeCallBack callback) = 0;
};

using KeyValueConfigServicePtr = std::shared_ptr<KeyValueConfigService>;
}
}

#endif // NORMAL_CONFIG_SERVICE_HPP
