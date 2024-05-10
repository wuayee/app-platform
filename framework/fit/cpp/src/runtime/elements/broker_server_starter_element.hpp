/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : start broker server
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#ifndef FIT_BROKER_SERVER_STARTER_ELEMENT_HPP
#define FIT_BROKER_SERVER_STARTER_ELEMENT_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>

namespace Fit {
class BrokerServerStarterElement : public RuntimeElementBase {
public:
    BrokerServerStarterElement();
    ~BrokerServerStarterElement() override;

    bool Start() override;
    bool Stop() override;
};
}

#endif // FIT_BROKER_SERVER_STARTER_ELEMENT_HPP
