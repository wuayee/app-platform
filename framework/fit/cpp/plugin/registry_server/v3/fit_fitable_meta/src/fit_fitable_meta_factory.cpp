/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fitable meta factory.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#include <v3/fit_fitable_meta/include/fit_fitable_meta_factory.h>
#include <v3/fit_fitable_meta/include/fit_fitable_meta_service_for_repo.h>
namespace Fit {
namespace Registry {
FitFitableMetaServicePtr FitFitableMetaFactory::CreateFitFitableMetaServiceForRepo(
    Fit::shared_ptr<FitMemoryFitableOperation> fitableMetaRepo)
{
    return Fit::make_shared<FitFitableMetaServiceForRepo>(fitableMetaRepo);
}
}
}