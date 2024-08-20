/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef TOKEN_ROLE_PERMISSION_REPO_BY_PG_H
#define TOKEN_ROLE_PERMISSION_REPO_BY_PG_H
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/internal/secure_access/token_role_repo.h>
#include "connection_pool.hpp"
#include "sql_wrapper/sql_builder.hpp"
namespace Fit {
namespace Pg {
using namespace Repository::Pg;
class TokenRoleRepoByPg : public TokenRoleRepo {
public:
    using SqlBuilderT = SqlBuilder<AuthTokenRole>;
    explicit TokenRoleRepoByPg(ConnectionPool* connectionPool);
    int32_t Save(const vector<AuthTokenRole>& authTokenRoles) override;
    int32_t Remove(const vector<string>& tokens) override;
    vector<AuthTokenRole> Query(const vector<string>& tokens) override;
    vector<AuthTokenRole> QueryAll() override;
private:
    static vector<SqlBuilderT::ColumnDescT> GetWhere();
    static vector<SqlBuilderT::ColumnDescT> GetAllColumns();
    static vector<AuthTokenRole> Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
        AbstractSqlExecResult& recordSet);

    ConnectionPool* connectionPool_{};
};
}
}
#endif