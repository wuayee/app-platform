/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/25
 */
#ifndef UTIL_BY_PG_HPP
#define UTIL_BY_PG_HPP
#include <fit/internal/registry/repository/util_by_repo.h>
#include "connection_pool.hpp"
namespace Fit {
namespace Pg{
using namespace Repository::Pg;
class UtilByPg : public UtilByRepo {
public:
    UtilByPg(ConnectionPool* connectionPool);
    FitCode GetCurrentTimeMs(uint64_t& result) override;
private:
    ConnectionPool* connectionPool_ {};
};
}
}
#endif