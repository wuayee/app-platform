/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : tracer config element
 * Author       : songyongtan
 * Date         : 2022/5/16
 * Notes:       :
 */

#ifndef FIT_TRACER_CONFIG_ELEMENT_HPP
#define FIT_TRACER_CONFIG_ELEMENT_HPP

#include <fit/stl/memory.hpp>
#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>
#include "config/key_value_config_service.hpp"

namespace Fit {
class TracerConfigElement : public RuntimeElementBase {
public:
    TracerConfigElement();
    ~TracerConfigElement() override;

    bool Start() override;
    bool Stop() override;

protected:
    void LoadConf();
    string GetAndSubscribeConfig(const string& key, const string& defaultValue,
        Configuration::KeyValueConfigService::SubscribeCallBack callback);
    void LoadConfigItem(const string& configKey, const string& subscribedKey, bool defaultValue,
        const std::function<void(bool)>& changedCallback);

private:
    unique_ptr<Configuration::KeyValueConfigService> kvConfigService_ {};
};
}

#endif // FIT_TRACER_CONFIG_ELEMENT_HPP
