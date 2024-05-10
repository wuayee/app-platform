/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_worker table in registry server
 * Author       : x00649642
 * Create       : 2023-11-24
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_TABLE_WORKER_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_TABLE_WORKER_HPP

#include "fit/fit_code.h"
#include "fit/stl/string.hpp"
#include "fit/stl/vector.hpp"
#include "fit/internal/registry/repository/fit_worker_table_operation.h"

#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class TableWorker : public FitWorkerTableOperation {
public:
    TableWorker() = default;
    ~TableWorker() override = default;

    bool Init() override;

    FitCode Save(const Fit::RegistryInfo::Worker& worker) override;

    FitCode Delete(const Fit::string& workerId) override;
    FitCode Delete(const Fit::string& workerId, const Fit::RegistryInfo::Application& application) override;

    Fit::vector<Fit::RegistryInfo::Worker> Query(const Fit::string& workerId) override;
    Fit::vector<Fit::RegistryInfo::Worker> Query(const Fit::RegistryInfo::Application& application) override;
    Fit::vector<Fit::RegistryInfo::Worker> QueryAll() override;

private:
    FitCode DeleteHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc) const;
    FitCode QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                         Fit::vector<Fit::RegistryInfo::Worker>& resultCollector) const;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_TABLE_WORKER_HPP
