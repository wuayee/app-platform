/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date       : 2022/05/16 11:31
 * Notes:       :
 */
#include <memory>
#include "fit/internal/registry/repository/fit_worker_table_operation.h"
#include "fit/internal/registry/repository/fit_address_table_operation.h"
#include "fit/internal/registry/repository/fit_fitable_table_operation.h"


FitWorkerTableOperationPtr FitWorkerTableOperationFactory::Create()
{
    return nullptr;
}

FitAddressTableOperationPtr FitAddressTableOperationFactory::Create()
{
    return nullptr;
}

FitFitableTableOperationPtr FitFitableTableOperationFactory::Create()
{
    return nullptr;
}
