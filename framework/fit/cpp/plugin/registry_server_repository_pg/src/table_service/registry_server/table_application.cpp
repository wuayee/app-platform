/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_application table in registry server
 * Author       : x00649642
 * Create       : 2023-11-23
 * Notes:       :
 */
#include "table_application.hpp"

#include "fit/fit_log.h"
#include "fit/external/util/string_utils.hpp"
#include "fit/internal/util/json_converter_util.hpp"

#include "connection_pool.hpp"
#include "table_service/utils/entity_convertor.hpp"
#include "table_service/utils/field_declare_helper.hpp"

using namespace Fit::Repository::Pg;
namespace {
constexpr const char* TABLE_NAME = "registry_application";
using ApplicationMeta = Fit::RegistryInfo::ApplicationMeta;
using SqlBuilderT = Fit::Pg::SqlBuilder<ApplicationMeta>;
using ColDescT = SqlBuilderT::ColumnDescT;
namespace Column {
constexpr const char* APPLICATION_NAME = "application_name";
constexpr const char* APPLICATION_VERSION = "application_version";
constexpr const char* EXTENSIONS = "extensions";
constexpr const char* CONSTRAINT_INDEX = "registry_application_index";

ColDescT nameDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_NAME, ApplicationMeta, id.name);
ColDescT versionDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_VERSION, ApplicationMeta, id.nameVersion);
ColDescT extensionsDesc = PQ_HELPER_JSON_FIELD_DECLARE(EXTENSIONS, ApplicationMeta, extensions);
}  // namespace Column

struct ApplicationMetaConvertor : public EntityConvertor<ApplicationMeta> {
    Fit::vector<ColDescT> GetAllColumns() override
    {
        return {Column::nameDesc, Column::versionDesc, Column::extensionsDesc};
    }

    Fit::vector<ColDescT> GetAppColumns()
    {
        return {Column::nameDesc, Column::versionDesc};
    }
};
ApplicationMetaConvertor g_convertor;
}  // namespace

FitCode TableApplication::Save(Fit::RegistryInfo::ApplicationMeta applicationMeta)
{
    RegistryInfo::ApplicationMeta current {};
    if (Query(applicationMeta.id, current) == FIT_OK && current.Equals(applicationMeta)) {
        return FIT_OK;
    }
    auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(TABLE_NAME, g_convertor.GetAllColumns(), Column::CONSTRAINT_INDEX,
                                                   {Column::extensionsDesc}, applicationMeta);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Save failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(applicationMeta).c_str());
        return FIT_ERR_FAIL;
    }
    return (result->CountAffected() == 1) ? FIT_ERR_SUCCESS : FIT_ERR_FAIL;
}

FitCode TableApplication::Delete(const Fit::RegistryInfo::Application& application)
{
    ApplicationMeta appMeta;
    appMeta.id = application;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, g_convertor.GetAppColumns(), appMeta);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Delete failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(appMeta).c_str());
        return FIT_ERR_FAIL;
    }
    return FIT_ERR_SUCCESS;
}

Fit::vector<ApplicationMeta> TableApplication::Query(const string& appName) const
{
    vector<ApplicationMeta> resultCollector;
    ApplicationMeta entity;
    entity.id.name = appName;
    (void)QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {Column::nameDesc}, entity),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by application name failed, error: %s params: %s.", errorMsg, appName.c_str());
        },
        resultCollector);
    return resultCollector;
}

FitCode TableApplication::Query(const Fit::RegistryInfo::Application& application, ApplicationMeta& result) const
{
    vector<ApplicationMeta> resultCollector;
    ApplicationMeta entity;
    entity.id = application;
    auto returnCode = QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), g_convertor.GetAppColumns(), entity),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by application failed, error: %s params: %s.", errorMsg,
                          g_convertor.ToLogString(entity).c_str());
        },
        resultCollector);
    if (returnCode == FIT_ERR_SUCCESS && resultCollector.size() == 1) {
        result = resultCollector.back();
        return FIT_ERR_SUCCESS;
    }
    return FIT_ERR_FAIL;
}

Fit::vector<ApplicationMeta> TableApplication::QueryAll() const
{
    vector<ApplicationMeta> result;
    (void)QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {}, {}),
        [](const char* errorMsg) { FIT_LOG_ERROR("Query all failed, error: %s.", errorMsg); }, result);
    return result;
}

FitCode TableApplication::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                                       Fit::vector<Fit::RegistryInfo::ApplicationMeta>& resultCollector) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    resultCollector = g_convertor.Parse(result);
    return FIT_ERR_SUCCESS;
}
