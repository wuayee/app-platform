/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_address table in registry server
 * Author       : x00649642
 * Create       : 2023-11-22
 * Notes:       :
 */
#include "table_address.hpp"

#include "fit/stl/string.hpp"
#include "fit/stl/vector.hpp"
#include "fit/fit_log.h"
#include "fit/external/util/string_utils.hpp"

#include "connection_pool.hpp"
#include "sql_wrapper/sql_connection.hpp"
#include "sql_wrapper/sql_exec_result.hpp"
#include "table_service/utils/entity_convertor.hpp"
#include "table_service/utils/field_declare_helper.hpp"

using namespace Fit::Repository::Pg;
namespace {
constexpr const char* TABLE_NAME = "registry_address";
using Address = Fit::RegistryInfo::Address;
using SqlBuilderT = Fit::Pg::SqlBuilder<Address>;
using ColDescT = SqlBuilderT::ColumnDescT;

namespace Column {
constexpr const char* HOST = "host";
constexpr const char* PORT = "port";
constexpr const char* PROTOCOL = "protocol";
constexpr const char* WORKER_ID = "worker_id";
constexpr const char* CONSTRAINT_INDEX = "registry_address_index";

ColDescT hostDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(HOST, Address, host);
ColDescT portDesc = PQ_HELPER_TYPE_INT_DECLARE(PORT, Address, port);
ColDescT workerIdDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(WORKER_ID, Address, workerId);
ColDescT protocolDesc{
    PROTOCOL, Fit::Pg::TYPE_INT,
    [](const Address& object, Fit::vector<Fit::string>& holder) {
        holder.emplace_back(Fit::to_string(static_cast<uint32_t>(object.protocol)));
        return holder.back().c_str();
    },
    [](Fit::string value, Address& object) { object.protocol = static_cast<Fit::fit_protocol_type>(stoull(value)); }};
}  // namespace Column

struct AddressConvertor : public EntityConvertor<Address> {
    Fit::vector<ColDescT> GetAllColumns() override
    {
        return {Column::hostDesc, Column::portDesc, Column::protocolDesc, Column::workerIdDesc};
    }
};
AddressConvertor g_convertor;
}  // namespace

bool TableAddress::Init()
{
    return true;
}

FitCode TableAddress::Save(const Fit::RegistryInfo::Address& address)
{
    return Save(Fit::vector<Fit::RegistryInfo::Address>{address});
}

FitCode TableAddress::Save(const Fit::vector<Fit::RegistryInfo::Address>& addresses)
{
    if (addresses.empty()) {
        FIT_LOG_INFO("Empty addresses");
        return FIT_ERR_SUCCESS;
    }
    uint32_t affectedRows = 0;
    auto sqlExecution = [&](SqlConnectionPtr& connection) -> SqlExecResultPtr {
        SqlExecResultPtr response;
        for (const auto& address : addresses) {
            auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(TABLE_NAME, g_convertor.GetAllColumns(),
                                                           Column::CONSTRAINT_INDEX, {Column::protocolDesc}, address);
            response = connection->ExecParam(sqlCmd.sql.c_str(), sqlCmd.params);
            if (response == nullptr || !response->IsOk()) {
                FIT_LOG_ERROR("Save failed, error message: %s params: %s.",
                              response ? response->GetErrorMessage() : "response is nullptr",
                              g_convertor.ToLogString(address).c_str());
                continue;
            }
            affectedRows += response->CountAffected();
        }
        return response;
    };
    auto result = ConnectionPool::Instance().Submit(sqlExecution);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Save failed, error message: %s address size: %lu.",
                      result ? result->GetErrorMessage() : "result is nullptr", addresses.size());
        return FIT_ERR_FAIL;
    }
    return (affectedRows == static_cast<uint32_t>(addresses.size())) ? FIT_ERR_SUCCESS : FIT_ERR_FAIL;
}

FitCode TableAddress::Delete(const Fit::string& workerId)
{
    Fit::RegistryInfo::Address address;
    address.workerId = workerId;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, {Column::workerIdDesc}, address);

    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Delete failed, error message: %s workerId: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr", workerId.c_str());
        return FIT_ERR_FAIL;
    }

    return FIT_ERR_SUCCESS;
}

FitCode TableAddress::Query(const Fit::string& workerId, Fit::vector<Fit::RegistryInfo::Address>& addresses)
{
    Fit::RegistryInfo::Address entity;
    entity.workerId = workerId;
    return QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {Column::workerIdDesc}, entity),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by workerId failed, error message: %s, workerId: %s.", errorMsg, workerId.c_str());
        },
        addresses);
}

FitCode TableAddress::QueryAll(Fit::vector<Fit::RegistryInfo::Address>& addresses)
{
    return QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {}, {}),
        [](const char* errorMsg) { FIT_LOG_ERROR("Query all failed, error message: %s.", errorMsg); }, addresses);
}

FitCode TableAddress::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                                   Fit::vector<Fit::RegistryInfo::Address>& resultCollector) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    resultCollector = g_convertor.Parse(result);
    return FIT_ERR_SUCCESS;
}
