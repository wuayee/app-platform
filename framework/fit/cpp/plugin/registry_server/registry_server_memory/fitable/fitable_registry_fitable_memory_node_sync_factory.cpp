/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/08
 * Notes:       :
 */
#include "fitable_registry_fitable_memory_node_sync.h"
FitableRegistryFitableNodeSyncPtr FitableRegistryFitableNodeSyncPtrFacotry::Create()
{
    return std::make_shared<Fit::Registry::FitableRegistryFitableMemoryNodeSync>();
}