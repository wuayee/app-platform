/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for registry listener element.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#ifndef FIT_REGISTRY_LISTENER_ELEMENT_HPP
#define FIT_REGISTRY_LISTENER_ELEMENT_HPP

#include "registry_listener_types.hpp"

#include <fit/stl/string.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为领域对象提供基类。
 */
class RegistryListenerElement {
public:
    RegistryListenerElement() = default;
    virtual ~RegistryListenerElement() = default;

    RegistryListenerElement(const RegistryListenerElement&) = delete;
    RegistryListenerElement(RegistryListenerElement&&) = delete;
    RegistryListenerElement& operator=(const RegistryListenerElement&) = delete;
    RegistryListenerElement& operator=(RegistryListenerElement&&) = delete;

    /**
     * 获取元素所属的注册中心监听程序。
     *
     * @return 表示指向所属注册中心监听程序的指针。
     */
    virtual RegistryListenerPtr GetRegistryListener() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_ELEMENT_HPP
