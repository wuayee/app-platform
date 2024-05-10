/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fitable meta factory.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */

#ifndef FIT_FITABLE_META_FACTORY_H
#define FIT_FITABLE_META_FACTORY_H

#include <v3/fit_fitable_meta/include/fit_fitable_meta_service.h>
#include <registry_server_memory/fitable/fit_memory_fitable_operation.h>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitFitableMetaFactory {
public:
    static FitFitableMetaServicePtr CreateFitFitableMetaServiceForRepo(
        Fit::shared_ptr<FitMemoryFitableOperation> fitableMetaRepo);
};
}
}
#endif // FIT_FITABLE_META_FACTORY_H