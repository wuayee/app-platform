/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-11-23
 * Notes:       :
 */

#ifndef HEARTBEAT_SERVER_REPO_PG_TABLE_HEARTBEAT_HPP
#define HEARTBEAT_SERVER_REPO_PG_TABLE_HEARTBEAT_HPP

#include <fit/internal/heartbeat/fit_heartbeat_repository.h>

#include "connection_pool.hpp"
#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Pg {
using namespace Repository::Pg;
using namespace Heartbeat;

class TableHeartbeat : public fit_heartbeat_repository {
public:
    using SqlBuilderT = SqlBuilder<AddressStatusInfo>;
    explicit TableHeartbeat(ConnectionPool* connectionPool);
    FitCode add_beat(const AddressStatusInfo& info) override;
    FitCode modify_beat(const AddressStatusInfo& info) override;
    FitCode remove_beat(const AddressBeatInfo& info) override;
    FitCode query_beat(const AddressBeatInfo& info, AddressStatusInfo& result) override;
    Heartbeat::AddressStatusSet query_all_beat() override;
    FitCode get_current_time_ms(uint64_t& result) override;

private:
    static vector<SqlBuilderT::ColumnDescT> GetWhere();
    static vector<SqlBuilderT::ColumnDescT> GetAllColumns();
    static vector<AddressStatusInfo> Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
                                           AbstractSqlExecResult& recordSet);

    ConnectionPool* connectionPool_{};
};
}  // namespace Pg
}  // namespace Fit
#endif
