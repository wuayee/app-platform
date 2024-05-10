/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : start component
 * Author       : songyongtan
 * Date         : 2022/5/23
 * Notes:       :
 */

#ifndef FIT_COMPONENT_STARTER_ELEMENT_HPP
#define FIT_COMPONENT_STARTER_ELEMENT_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>
#include <fit/external/framework/plugin_activator.hpp>

namespace Fit {
class ComponentStarterElement : public RuntimeElementBase {
public:
    explicit ComponentStarterElement();
    ~ComponentStarterElement() override;

    bool Start() override;
    bool Stop() override;

protected:
    bool Load();
    FitCode StartGlobalActivators();
    void StopGlobalActivators();

private:
    vector<Framework::PluginActivatorPtr> activators_ {};
    Plugin::PluginContextPtr context_ {};
};
}

#endif // FIT_COMPONENT_STARTER_ELEMENT_HPP
