/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/9/2 16:19
 */

#include "key_value_config_service_impl.hpp"
#include <fit/fit_log.h>

namespace Fit {
namespace Configuration {
KeyValueConfigServiceImpl::KeyValueConfigServiceImpl(ConfigurationClientPtr client)
    : client_(std::move(client)) {}

int32_t KeyValueConfigServiceImpl::Get(const Fit::string &key, Fit::string &value)
{
    auto res = GetInner(key, value);
    if (res == FIT_OK) {
        return res;
    }

    if (Download(key) == FIT_OK) {
        return GetInner(key, value);
    }

    return FIT_ERR_NOT_FOUND;
}

int32_t KeyValueConfigServiceImpl::GetInner(const Fit::string &key, Fit::string &value)
{
    Fit::shared_lock<Fit::shared_mutex> lock(mtx_);
    auto it = data_.find(key);
    if (it != data_.end()) {
        value = it->second;
        return FIT_OK;
    }

    return FIT_ERR_NOT_FOUND;
}

void KeyValueConfigServiceImpl::Notify(const Fit::string &key, const Fit::string &value)
{
    Fit::shared_lock<Fit::shared_mutex> lock(mtx_);
    auto it = subscribes_.find(key);
    if (it == subscribes_.end()) {
        return;
    }
    FIT_LOG_INFO("Notify change, key:%s, value:%s.", key.c_str(), value.c_str());
    it->second(key, value);
}

void KeyValueConfigServiceImpl::Set(const Fit::string &key, const Fit::string &value)
{
    {
        Fit::unique_lock<Fit::shared_mutex> lock(mtx_);
        data_[key] = value;
    }
    Notify(key, value);
}

void KeyValueConfigServiceImpl::Subscribe(const Fit::string &key, SubscribeCallBack callback)
{
    unique_lock<Fit::shared_mutex> lock(mtx_);
    subscribes_[key] = std::move(callback);
}

int32_t KeyValueConfigServiceImpl::Download(const Fit::string &key)
{
    if (!client_) {
        return FIT_ERR_NOT_FOUND;
    }

    if (!client_->IsSubscribed(key)) {
        client_->Subscribe(key,
            [this](const Fit::string &key, const Fit::string &value) {
                Set(key, value);
            });
    }

    Fit::string value;
    auto res = client_->Get(key, value);
    if (res != FIT_OK) {
        return res;
    }

    Set(key, value);
    return FIT_OK;
}
}
}