/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_address table in registry server
 * Author       : x00649642
 * Create       : 2023-11-22
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_TABLE_ADDRESS_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_TABLE_ADDRESS_HPP

#include "fit/fit_code.h"
#include "fit/internal/registry/repository/fit_address_table_operation.h"
#include "fit/internal/registry/registry_util.h"

#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class TableAddress : public FitAddressTableOperation {
public:
    TableAddress() = default;
    ~TableAddress() override = default;

    bool Init() override;

    FitCode Save(const Fit::RegistryInfo::Address& address) override;
    FitCode Save(const Fit::vector<Fit::RegistryInfo::Address>& addresses) override;

    FitCode Delete(const Fit::string& workerId) override;

    FitCode Query(const Fit::string& workerId, Fit::vector<Fit::RegistryInfo::Address>& addresses) override;
    FitCode QueryAll(Fit::vector<Fit::RegistryInfo::Address>& addresses) override;

private:
    FitCode QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                         Fit::vector<Fit::RegistryInfo::Address>& resultCollector) const;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit

#endif  // REGISTRY_SERVER_REPOSITORY_PG_TABLE_ADDRESS_HPP
