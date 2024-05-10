/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table entity convertor
 * Author       : x00649642
 * Create       : 2023-11-27
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_UTILS_ENTITY_CONVERTOR_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_UTILS_ENTITY_CONVERTOR_HPP

#include "fit/fit_log.h"

#include "sql_wrapper/sql_builder.hpp"
#include "sql_wrapper/sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
template <typename Entity> struct EntityConvertor {
    virtual Fit::vector<Fit::Pg::ColumnDesc<Entity>> GetAllColumns() = 0;

    vector<Entity> Parse(SqlExecResultPtr& recordSet)
    {
        return Parse(recordSet, GetAllColumns());
    }

    vector<Entity> Parse(SqlExecResultPtr& recordSet, const Fit::vector<Fit::Pg::ColumnDesc<Entity>>& columns)
    {
        vector<Entity> result;
        result.reserve(recordSet->CountRow());
        if (columns.size() != static_cast<uint32_t>(recordSet->CountCol())) {
            FIT_LOG_WARN("Parse return blank. sql result columns are %d, expect columns %lu", recordSet->CountCol(),
                         columns.size());
            return {};
        }
        Entity entity{};
        for (int32_t rowCount = 0; rowCount < recordSet->CountRow(); ++rowCount) {
            auto rowRecord = recordSet->GetResultRow(rowCount);
            for (int32_t col = 0; col < recordSet->CountCol(); ++col) {
                columns[col].readValue(Fit::move(rowRecord[col]), entity);
            }
            result.emplace_back(Fit::move(entity));
        }
        return result;
    }

    Fit::string ToLogString(const Entity& entity)
    {
        return ToLogString(entity, GetAllColumns());
    }

    Fit::string ToLogString(const Entity& entity, const Fit::vector<Fit::Pg::ColumnDesc<Entity>>& columns)
    {
        std::ostringstream oStream;
        Fit::vector<Fit::string> holder;
        for (const auto& col : columns) {
            oStream << col.name << "=" << col.getSqlParam(entity, holder) << " ";
        }
        return {oStream.str()};
    }
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_UTILS_ENTITY_CONVERTOR_HPP
