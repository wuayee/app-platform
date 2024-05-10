/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : start broker client
 * Author       : songyongtan
 * Date         : 2022/5/19
 * Notes:       :
 */

#ifndef FIT_BROKER_CLIENT_STARTER_ELEMENT_HPP
#define FIT_BROKER_CLIENT_STARTER_ELEMENT_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>

namespace Fit {
class BrokerClientStarterElement : public RuntimeElementBase {
public:
    BrokerClientStarterElement();
    ~BrokerClientStarterElement() override;

    bool Start() override;
    bool Stop() override;

protected:
    FitCode LoadSupportedTransportClient();
};
}

#endif // FIT_BROKER_CLIENT_STARTER_ELEMENT_HPP
