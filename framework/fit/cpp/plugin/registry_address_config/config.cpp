/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/07/31
 * Notes:       :
 */
#include <config.h>
namespace Fit {
static bool isRegistryServer = false;
void SetIsRegistryServer(bool isRegistryServerTemp)
{
    isRegistryServer = isRegistryServerTemp;
}
bool IsRegistryServer()
{
    return isRegistryServer;
}
}