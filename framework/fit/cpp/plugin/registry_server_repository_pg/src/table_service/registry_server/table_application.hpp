/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_application table in registry server
 * Author       : x00649642
 * Create       : 2023-11-23
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_APPLICATION_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_APPLICATION_HPP

#include "fit/fit_code.h"
#include "fit/stl/string.hpp"
#include "fit/stl/vector.hpp"
#include "fit/internal/registry/repository/fit_registry_application_repo.h"

#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class TableApplication : public Fit::RegistryApplicationRepo {
public:
    TableApplication() = default;
    ~TableApplication() override = default;

    const char* GetName() const override
    {
        return "pgRepo";
    }

    FitCode Save(Fit::RegistryInfo::ApplicationMeta application) override;

    FitCode Delete(const Fit::RegistryInfo::Application& application) override;

    vector<Fit::RegistryInfo::ApplicationMeta> Query(const string& appName) const override;
    FitCode Query(const Fit::RegistryInfo::Application& application,
                  Fit::RegistryInfo::ApplicationMeta& result) const override;
    vector<Fit::RegistryInfo::ApplicationMeta> QueryAll() const override;

private:
    FitCode QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                         Fit::vector<Fit::RegistryInfo::ApplicationMeta>& resultCollector) const;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_APPLICATION_HPP
