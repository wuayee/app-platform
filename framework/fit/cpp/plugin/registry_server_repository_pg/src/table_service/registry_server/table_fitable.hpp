/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_fitable table in registry server
 * Author       : x00649642
 * Create       : 2023-11-22
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_HPP

#include "fit/fit_code.h"
#include "fit/stl/string.hpp"
#include "fit/stl/vector.hpp"
#include "fit/internal/registry/repository/fit_fitable_table_operation.h"

#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class TableFitable : public FitFitableTableOperation {
public:
    TableFitable() = default;
    ~TableFitable() override = default;

    bool Init() override;

    FitCode Save(const Fit::RegistryInfo::FitableMeta& fitableMeta) override;

    FitCode Delete(const Fit::RegistryInfo::Application& application) override;
    FitCode Delete(const Fit::RegistryInfo::FitableMeta& fitableMeta) override;

    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::string& genericableId) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::Application& application) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::Fitable& fitable) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::FitableMeta& fitableMeta) override;
    Fit::vector<Fit::RegistryInfo::FitableMeta> QueryAll() override;

private:
    FitCode DeleteHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc) const;
    Fit::vector<Fit::RegistryInfo::FitableMeta> QueryHandler(const Fit::Pg::SqlCmd& sqlCmd,
                                                             const std::function<void(const char*)>& logFunc) const;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_HPP
