/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : scene subscribe table
 * Author       : songyongtan
 * Create       : 2023-11-24
 * Notes:       :
 */

#ifndef HEARTBEAT_SERVER_REPO_PG_TABLE_SCENE_SUBSCRIBE_HPP
#define HEARTBEAT_SERVER_REPO_PG_TABLE_SCENE_SUBSCRIBE_HPP

#include <fit/internal/heartbeat/fit_scene_subscribe_repository.h>

#include "connection_pool.hpp"
#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Pg {
using namespace Repository::Pg;
using namespace Heartbeat;

class TableSceneSubscribe : public fit_scene_subscribe_repository {
public:
    using SqlBuilderT = SqlBuilder<SubscribeBeatInfo>;
    explicit TableSceneSubscribe(ConnectionPool* connectionPool);
    FitCode add(const fit_scene_subscriber& info) override;
    FitCode remove(const fit_scene_subscriber& info) override;
    FitCode remove(const Fit::string& id) override;
    fit_scene_subscriber_set query(const SceneType& sceneType) override;

private:
    static vector<SqlBuilderT::ColumnDescT> GetWhere();
    static vector<SqlBuilderT::ColumnDescT> GetAllColumns();
    static vector<SubscribeBeatInfo> Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
                                           AbstractSqlExecResult& recordSet);

    ConnectionPool* connectionPool_{};
};
}  // namespace Pg
}  // namespace Fit
#endif
