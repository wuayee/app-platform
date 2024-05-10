/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance factory.
 * Author       : w00561424
 * Date         : 2023/09/07
 * Notes:       :
 */
#include <v3/fit_application_instance/include/fit_application_instance_factory.h>
#include <v3/fit_application_instance/include/fit_application_instance_service_for_repo.h>
namespace Fit {
namespace Registry {
FitApplicationInstanceServicePtr FitApplicationInstanceFactory::CreateServiceForRepo(
    Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo, Fit::shared_ptr<FitMemoryAddressOperation> addressRepo)
{
    return Fit::make_shared<FitApplicationInstanceServiceForRepo>(workerRepo, addressRepo);
}
Fit::shared_ptr<FitApplicationInstanceFactory> FitApplicationInstanceFactory::Instance()
{
    static auto fitApplicationInstance = Fit::make_shared<FitApplicationInstanceFactory>();
    return fitApplicationInstance;
}
}
}