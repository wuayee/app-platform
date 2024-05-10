/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : Provides registry v3 interface implementation for application instance SPI.
 * Author       : w00561424
 * Date         : 2023/09/11
 */
#include <support/application_instance_spi_impl.hpp>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_application_instances/1.0.0/cplusplus/query_application_instances.hpp>
namespace Fit {
namespace Registry {
namespace Listener {
Fit::vector<ApplicationInstance> ApplicationInstanceSpiImpl::Query(const Fit::vector<ApplicationInfo>& apps)
{
    Fit::vector<ApplicationInstance> applicationInstancesRet {};
    Fit::vector<ApplicationInstance> *result {nullptr};
    fit::hakuna::kernel::registry::server::queryApplicationInstances queryApplicationInstancesInvoker;
    int32_t ret = queryApplicationInstancesInvoker(&apps, &result);
    if (ret != FIT_ERR_SUCCESS || result == nullptr) {
        FIT_LOG_ERROR("Query application instance failed : %d.", ret);
        return applicationInstancesRet;
    }
    for (const auto& applicationInstance : *result) {
        ApplicationInstance applicationInstanceTemp;
        applicationInstanceTemp.application = new ApplicationInfo();
        *applicationInstanceTemp.application = *applicationInstance.application;
        applicationInstanceTemp.workers = applicationInstance.workers;
        applicationInstancesRet.emplace_back(applicationInstanceTemp);
    }

    return applicationInstancesRet;
}

Fit::vector<ApplicationInstance> ApplicationInstanceSpiImpl::Subscribe(const Fit::vector<ApplicationInfo>& apps)
{
    return Fit::vector<ApplicationInstance> {};
}
}
}
}