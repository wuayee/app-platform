/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance factory.
 * Author       : w00561424
 * Date         : 2023/09/07
 * Notes:       :
 */
#ifndef FIT_APPLICATION_INTERFACE_FACTORY_H
#define FIT_APPLICATION_INTERFACE_FACTORY_H
#include <v3/fit_application_instance/include/fit_application_instance_service.h>
#include <registry_server_memory/fitable/fit_memory_worker_operation.h>
#include <registry_server_memory/fitable/fit_memory_address_operation.h>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitApplicationInstanceFactory {
public:
    FitApplicationInstanceServicePtr CreateServiceForRepo(
        Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo, Fit::shared_ptr<FitMemoryAddressOperation> addressRepo);
    static Fit::shared_ptr<FitApplicationInstanceFactory> Instance();
};
}
}
#endif