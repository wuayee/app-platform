/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/07/31
 * Notes:       :
 */
#ifndef REGISTRY_ADDRESS_CONFIG_H
#define REGISTRY_ADDRESS_CONFIG_H
#include <fit/stl/string.hpp>
namespace Fit {
void SetIsRegistryServer(bool isRegistryServerTemp);
bool IsRegistryServer();
}
#endif