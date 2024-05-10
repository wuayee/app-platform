/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : Provides registry v3 interface implementation for application instance SPI.
 * Author       : w00561424
 * Date         : 2023/09/11
 */

#ifndef SUPPORT_APPLICATION_INSTANCE_SPI_IMPL_HPP
#define SUPPORT_APPLICATION_INSTANCE_SPI_IMPL_HPP

#include <support/registry_listener_spi.hpp>
namespace Fit {
namespace Registry {
namespace Listener {
class ApplicationInstanceSpiImpl : public ApplicationInstanceSpi {
public:
    Fit::vector<ApplicationInstance> Query(const Fit::vector<ApplicationInfo>& apps) override;
    Fit::vector<ApplicationInstance> Subscribe(const Fit::vector<ApplicationInfo>& apps) override;
};
}
}
}
#endif // SUPPORT_APPLICATION_INSTANCE_SPI_IMPL_HPP