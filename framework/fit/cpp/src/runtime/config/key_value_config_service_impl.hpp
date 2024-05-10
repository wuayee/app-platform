/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/9/2 16:19
 */
#ifndef KEY_VALUE_CONFIG_SERVICE_IMPL_HPP
#define KEY_VALUE_CONFIG_SERVICE_IMPL_HPP

#include "key_value_config_service.hpp"
#include "configuration_client.h"

#include <fit/fit_code.h>
#include <fit/stl/map.hpp>
#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Configuration {
class KeyValueConfigServiceImpl : public KeyValueConfigService {
public:
    explicit KeyValueConfigServiceImpl(ConfigurationClientPtr client);
    ~KeyValueConfigServiceImpl() override = default;

    int32_t Get(const Fit::string &key, Fit::string &value) override;
    void Set(const Fit::string &key, const Fit::string &value) override;
    void Subscribe(const Fit::string &key, SubscribeCallBack callback) override;
private:
    FitCode Download(const Fit::string &genericId);
    int32_t GetInner(const Fit::string &key, Fit::string &value);
    void Notify(const Fit::string &key, const Fit::string &value);

    Fit::shared_mutex mtx_;
    ConfigurationClientPtr client_ {nullptr};
    Fit::map<Fit::string, Fit::string> data_ {};
    Fit::map<Fit::string, SubscribeCallBack> subscribes_ {};
};
}
}

#endif // NORMAL_CONFIG_SERVICE_HPP
