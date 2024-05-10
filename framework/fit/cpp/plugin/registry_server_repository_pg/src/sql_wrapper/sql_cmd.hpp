/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : sql command
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_CMD_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_CMD_HPP

namespace Fit {
namespace Pg {
struct SqlCmd {
    string sql;
    vector<const char*> params;
    vector<string> holder;
};
}  // namespace Pg
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_CMD_HPP
