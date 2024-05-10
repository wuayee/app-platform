/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for registry_fitable table in registry server
 * Author       : x00649642
 * Create       : 2023-11-22
 * Notes:       :
 */
#include "table_fitable.hpp"

#include "fit/fit_log.h"
#include "fit/internal/fit_string_util.h"
#include "fit/internal/fit_address_utils.h"
#include "fit/external/util/string_utils.hpp"
#include "fit/internal/util/json_converter_util.hpp"

#include "connection_pool.hpp"
#include "table_service/utils/entity_convertor.hpp"
#include "table_service/utils/field_declare_helper.hpp"

using namespace Fit::Repository::Pg;
namespace {
constexpr const char* TABLE_NAME = "registry_fitable";
using FitableMeta = Fit::RegistryInfo::FitableMeta;
using SqlBuilderT = Fit::Pg::SqlBuilder<FitableMeta>;
using ColDescT = SqlBuilderT::ColumnDescT;

namespace Column {
constexpr const char* GENERICABLE_ID = "genericable_id";
constexpr const char* GENERICABLE_VERSION = "genericable_version";
constexpr const char* FITABLE_ID = "fitable_id";
constexpr const char* FITABLE_VERSION = "fitable_version";
constexpr const char* FORMATS = "formats";
constexpr const char* APPLICATION_NAME = "application_name";
constexpr const char* APPLICATION_VERSION = "application_version";
constexpr const char* ALIASES = "aliases";
constexpr const char* TAGS = "tags";
constexpr const char* EXTENSIONS = "extensions";
constexpr const char* ENVIRONMENT = "environment";
constexpr const char* CONSTRAINT_INDEX = "registry_fitable_index";

ColDescT genericableIdDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(GENERICABLE_ID, FitableMeta, fitable.genericableId);
ColDescT genericableVersionDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(GENERICABLE_VERSION, FitableMeta, fitable.genericableVersion);
ColDescT fitableIdDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(FITABLE_ID, FitableMeta, fitable.fitableId);
ColDescT fitableVersionDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(FITABLE_VERSION, FitableMeta, fitable.fitableVersion);
ColDescT applicationNameDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_NAME, FitableMeta, application.name);
ColDescT applicationVersionDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(APPLICATION_VERSION, FitableMeta, application.nameVersion);
ColDescT aliasDesc = PQ_HELPER_JSON_FIELD_DECLARE(ALIASES, FitableMeta, aliases);
ColDescT tagsDesc = PQ_HELPER_JSON_FIELD_DECLARE(TAGS, FitableMeta, tags);
ColDescT extensionsDesc = PQ_HELPER_JSON_FIELD_DECLARE(EXTENSIONS, FitableMeta, extensions);
ColDescT environmentDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(ENVIRONMENT, FitableMeta, environment);
ColDescT formatsDesc{
    FORMATS, Fit::Pg::TYPE_VARCHAR,
    [](const FitableMeta& object, Fit::vector<Fit::string>& holder) {
        holder.emplace_back(join_to_string(object.formats, ","));
        return holder.back().c_str();
    },
    [](Fit::string value, FitableMeta& object) { object.formats = fit_address_utils::parse_formats(value); }};
}  // namespace Column

struct FitableMetaConvertor : public EntityConvertor<FitableMeta> {
    Fit::vector<ColDescT> GetAllColumns() override
    {
        return {Column::genericableIdDesc,
                Column::genericableVersionDesc,
                Column::fitableIdDesc,
                Column::fitableVersionDesc,
                Column::formatsDesc,
                Column::applicationNameDesc,
                Column::applicationVersionDesc,
                Column::aliasDesc,
                Column::tagsDesc,
                Column::extensionsDesc,
                Column::environmentDesc};
    }

    Fit::vector<ColDescT> GetFullWhereColumns()
    {
        // no format column
        return {Column::genericableIdDesc,   Column::genericableVersionDesc,
                Column::fitableIdDesc,       Column::fitableVersionDesc,
                Column::applicationNameDesc, Column::applicationVersionDesc,
                Column::aliasDesc,           Column::tagsDesc,
                Column::extensionsDesc,      Column::environmentDesc};
    }

    Fit::vector<ColDescT> GetAppWhereColumns()
    {
        return {Column::applicationNameDesc, Column::applicationVersionDesc};
    }

    Fit::vector<ColDescT> GetFitableWhereColumns()
    {
        return {Column::genericableIdDesc, Column::genericableVersionDesc, Column::fitableIdDesc,
                Column::fitableVersionDesc};
    }
};
FitableMetaConvertor g_convertor;
}  // namespace

bool TableFitable::Init()
{
    return true;
}

FitCode TableFitable::Save(const FitableMeta& fitableMeta)
{
    auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(
        TABLE_NAME, g_convertor.GetAllColumns(), Column::CONSTRAINT_INDEX,
        {Column::aliasDesc, Column::environmentDesc, Column::extensionsDesc, Column::formatsDesc, Column::tagsDesc},
        fitableMeta);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Save failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(fitableMeta).c_str());
        return FIT_ERR_FAIL;
    }
    return (result->CountAffected() == 1) ? FIT_ERR_SUCCESS : FIT_ERR_FAIL;
}

FitCode TableFitable::Delete(const Fit::RegistryInfo::Application& application)
{
    FitableMeta entity;
    entity.application = application;
    return DeleteHandler(SqlBuilderT::BuildDelete(TABLE_NAME, g_convertor.GetAppWhereColumns(), entity),
        [&](const char* errMsg) {
            FIT_LOG_ERROR("Delete by app failed, error: %s params: %s.", errMsg,
                          g_convertor.ToLogString(entity).c_str());
        });
}

FitCode TableFitable::Delete(const FitableMeta& fitableMeta)
{
    return DeleteHandler(SqlBuilderT::BuildDelete(TABLE_NAME, g_convertor.GetFullWhereColumns(), fitableMeta),
        [&](const char* errMsg) {
            FIT_LOG_ERROR("Delete by meta failed, error: %s params: %s.", errMsg,
                          g_convertor.ToLogString(fitableMeta).c_str());
        });
}

FitCode TableFitable::DeleteHandler(const Fit::Pg::SqlCmd& sqlCmd,
                                    const std::function<void(const char*)>& logFunc) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    return FIT_ERR_SUCCESS;
}

Fit::vector<Fit::RegistryInfo::FitableMeta> TableFitable::Query(const Fit::string& genericableId)
{
    FitableMeta entity;
    entity.fitable.genericableId = genericableId;
    return QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {Column::genericableIdDesc}, entity),
        [&](const char* errMsg) {
            FIT_LOG_ERROR("Query by genericable id failed, error: %s params: %s.", errMsg, genericableId.c_str());
        });
}

Fit::vector<FitableMeta> TableFitable::Query(const Fit::RegistryInfo::Application& application)
{
    FitableMeta entity;
    entity.application = application;
    return QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), g_convertor.GetAppWhereColumns(), entity),
        [&](const char* errMsg) {
            FIT_LOG_ERROR("Query by application failed, error: %s params: %s.", errMsg,
                          g_convertor.ToLogString(entity).c_str());
        });
}

Fit::vector<FitableMeta> TableFitable::Query(const Fit::RegistryInfo::Fitable& fitable)
{
    FitableMeta entity;
    entity.fitable = fitable;
    return QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), g_convertor.GetFitableWhereColumns(), entity),
        [&](const char* errMsg) {
            FIT_LOG_ERROR("Query by fitable failed, error: %s params: %s.", errMsg,
                          g_convertor.ToLogString(entity).c_str());
        });
}

Fit::vector<FitableMeta> TableFitable::Query(const Fit::RegistryInfo::FitableMeta& fitableMeta)
{
    return QueryHandler(SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(),
                                                 g_convertor.GetFullWhereColumns(), fitableMeta),
                        [&](const char* errMsg) {
                            FIT_LOG_ERROR("Query by fitable meta failed, error: %s params: %s.", errMsg,
                                          g_convertor.ToLogString(fitableMeta).c_str());
                        });
}

Fit::vector<FitableMeta> TableFitable::QueryAll()
{
    return QueryHandler(SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {}, {}),
                        [&](const char* errMsg) { FIT_LOG_ERROR("Query all failed, error: %s.", errMsg); });
}

Fit::vector<FitableMeta> TableFitable::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd,
                                                    const std::function<void(const char*)>& logFunc) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return {};
    }
    Fit::vector<FitableMeta> resultCollector;
    resultCollector = g_convertor.Parse(result);
    return resultCollector;
}
