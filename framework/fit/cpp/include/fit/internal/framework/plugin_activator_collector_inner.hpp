/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/06/01
 * Notes:       :
 */

#ifndef PLUGIN_ACTIVATOR_COLLECTOR_INNER_HPP
#define PLUGIN_ACTIVATOR_COLLECTOR_INNER_HPP

#include <fit/external/framework/plugin_activator.hpp>
#include <functional>

namespace Fit {
namespace Framework {
class PluginActivatorReceiver {
public:
    std::function<void(const PluginActivatorPtrList&)> Register;
    std::function<void(const PluginActivatorPtrList&)> UnRegister;
};

__attribute__ ((visibility ("default"))) PluginActivatorPtrList PopPluginActivatorCache();
/**
 * change the receiver
 * @param target : new receiver
 * @return old receiver
 */
__attribute__ ((visibility ("default"))) PluginActivatorReceiver* PluginActivatorFlowTo(
    PluginActivatorReceiver* target);
}
}
#endif // PLUGIN_ACTIVATOR_COLLECTOR_INNER_HPP
