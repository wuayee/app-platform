/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_worker table in registry server
 * Author       : x00649642
 * Create       : 2023-11-24
 * Notes:       :
 */
#include "table_worker.hpp"

#include "fit/fit_log.h"
#include "fit/external/util/string_utils.hpp"
#include "fit/internal/util/json_converter_util.hpp"

#include "connection_pool.hpp"
#include "table_service/utils/entity_convertor.hpp"
#include "table_service/utils/field_declare_helper.hpp"

using namespace Fit::Repository::Pg;
namespace {
constexpr const char* TABLE_NAME = "registry_worker";
using Worker = Fit::RegistryInfo::Worker;
using SqlBuilderT = Fit::Pg::SqlBuilder<Worker>;
using ColDescT = SqlBuilderT::ColumnDescT;

namespace Column {
constexpr const char* WORKER_ID = "worker_id";
constexpr const char* APPLICATION_NAME = "application_name";
constexpr const char* APPLICATION_VERSION = "application_version";
constexpr const char* EXPIRE = "expire";
constexpr const char* ENVIRONMENT = "environment";
constexpr const char* WORKER_VERSION = "worker_version";
constexpr const char* EXTENSIONS = "extensions";
constexpr const char* CONSTRAINT_INDEX = "registry_worker_index";

ColDescT workerIdDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(WORKER_ID, Worker, workerId);
ColDescT appNameDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_NAME, Worker, application.name);
ColDescT appVersionDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_VERSION, Worker, application.nameVersion);
ColDescT expireDesc = PQ_HELPER_TYPE_INT_DECLARE(EXPIRE, Worker, expire);
ColDescT environmentDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(ENVIRONMENT, Worker, environment);
ColDescT workerVersionDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(WORKER_VERSION, Worker, version);
ColDescT extensionsDesc = PQ_HELPER_JSON_FIELD_DECLARE(EXTENSIONS, Worker, extensions);
}  // namespace Column

struct WorkerConvertor : public EntityConvertor<Worker> {
    Fit::vector<ColDescT> GetAllColumns() override
    {
        return {Column::workerIdDesc,    Column::appNameDesc,       Column::appVersionDesc, Column::expireDesc,
                Column::environmentDesc, Column::workerVersionDesc, Column::extensionsDesc};
    }
};
WorkerConvertor g_convertor;
}  // namespace

bool TableWorker::Init()
{
    return true;
}

FitCode TableWorker::Save(const Fit::RegistryInfo::Worker& worker)
{
    auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(
        TABLE_NAME, g_convertor.GetAllColumns(), Column::CONSTRAINT_INDEX,
        {Column::environmentDesc, Column::expireDesc, Column::extensionsDesc, Column::workerVersionDesc}, worker);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Save failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(worker).c_str());
        return FIT_ERR_FAIL;
    }
    return (result->CountAffected() == 1) ? FIT_ERR_SUCCESS : FIT_ERR_FAIL;
}

FitCode TableWorker::Delete(const Fit::string& workerId)
{
    Fit::RegistryInfo::Worker worker;
    worker.workerId = workerId;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, {Column::workerIdDesc}, worker);
    return DeleteHandler(sqlCmd, [&](const char* errorMsg) {
        FIT_LOG_ERROR("Delete by workerId failed, error message: %s params: %s.", errorMsg, workerId.c_str());
    });
}

FitCode TableWorker::Delete(const Fit::string& workerId, const Fit::RegistryInfo::Application& application)
{
    Fit::RegistryInfo::Worker worker;
    worker.workerId = workerId;
    worker.application = application;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME,
                                           {Column::workerIdDesc, Column::appNameDesc, Column::appVersionDesc}, worker);
    return DeleteHandler(sqlCmd, [&](const char* errorMsg) {
        FIT_LOG_ERROR("Delete by workerId and application failed, error message: %s params: worker=%s, "
                      "app.name=%s, app.ver=%s.",
                      errorMsg, workerId.c_str(), application.name.c_str(), application.nameVersion.c_str());
    });
}

FitCode TableWorker::DeleteHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    return FIT_ERR_SUCCESS;
}

Fit::vector<Fit::RegistryInfo::Worker> TableWorker::Query(const Fit::string& workerId)
{
    Fit::vector<Fit::RegistryInfo::Worker> workers;
    Fit::RegistryInfo::Worker inParam;
    inParam.workerId = workerId;
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {Column::workerIdDesc}, inParam);
    (void)QueryHandler(
        sqlCmd,
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by workerId failed. error message: %s params: worker=%s.", errorMsg, workerId.c_str());
        },
        workers);
    return workers;
}

Fit::vector<Fit::RegistryInfo::Worker> TableWorker::Query(const Fit::RegistryInfo::Application& application)
{
    Fit::vector<Fit::RegistryInfo::Worker> workers;
    Fit::RegistryInfo::Worker inParam;
    inParam.application = application;
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(),
                                           {Column::appNameDesc, Column::appVersionDesc}, inParam);
    (void)QueryHandler(
        sqlCmd,
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by app failed, error message: %s params: app.name=%s, app.ver=%s.", errorMsg,
                          application.name.c_str(), application.nameVersion.c_str());
        },
        workers);
    return workers;
}

Fit::vector<Fit::RegistryInfo::Worker> TableWorker::QueryAll()
{
    Fit::vector<Fit::RegistryInfo::Worker> workers;
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {}, {});
    (void)QueryHandler(
        sqlCmd, [&](const char* errorMsg) { FIT_LOG_ERROR("Query all failed, error message: %s.", errorMsg); },
        workers);
    return workers;
}

FitCode TableWorker::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                                  Fit::vector<Fit::RegistryInfo::Worker>& resultCollector) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    resultCollector = g_convertor.Parse(result);
    return FIT_ERR_SUCCESS;
}
