/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table factory
 * Author       : songyongtan
 * Create       : 2023-11-23
 * Notes:       :
 */

#include <fit/stl/memory.hpp>
#include "table_heartbeat.hpp"
#include "table_scene_subscribe.hpp"
#include "connection_pool.hpp"

using namespace Fit;
using namespace Fit::Pg;

fit_heartbeat_repository_ptr fit_heartbeat_repository_factory::create()
{
    return make_shared<TableHeartbeat>(&ConnectionPool::Instance());
}

fit_scene_subscribe_repository_ptr fit_scene_subscribe_repository_factory::create()
{
    return make_shared<TableSceneSubscribe>(&ConnectionPool::Instance());
}
