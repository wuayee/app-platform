/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides composite for fitable address synchronizer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_COMPOSITE_HPP
#define FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_COMPOSITE_HPP

#include "../sync/address_synchronizer.hpp"

#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务地址同步程序提供组合模式的实现。
 */
class AddressSynchronizerComposite : public virtual AddressSynchronizer {
public:
    AddressSynchronizerComposite() = default;
    ~AddressSynchronizerComposite() override = default;
    void Start() override;
    void Stop() override;

    /**
     * 向组合中添加一个同步程序。
     *
     * @param synchronizer 表示指向待添加到组合中的同步程序的指针。
     */
    void Add(AddressSynchronizerPtr synchronizer);
private:
    Fit::vector<AddressSynchronizerPtr> synchronizers_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_COMPOSITE_HPP
