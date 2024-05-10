/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : sql field declare helper macros
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_UTILS_FIELD_DECLARE_HELPER_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_UTILS_FIELD_DECLARE_HELPER_HPP

#define PQ_HELPER_TYPE_VARCHAR_DECLARE(fieldName, Object, field)                                  \
    {                                                                                             \
        fieldName, Fit::Pg::TYPE_VARCHAR,                                                         \
            [](const Object& object, Fit::vector<Fit::string>&) { return object.field.c_str(); }, \
            [](Fit::string value, Object& object) { object.field = Fit::move(value); }            \
    }

#define PQ_HELPER_TYPE_INT_DECLARE(fieldName, Object, field)                                           \
    {                                                                                                  \
        fieldName, Fit::Pg::TYPE_INT,                                                                  \
            [](const Object& object, Fit::vector<Fit::string>& holder) {                               \
                holder.emplace_back(Fit::to_string(object.field));                                     \
                return holder.back().c_str();                                                          \
            },                                                                                         \
            [](Fit::string value, Object& object) { object.field = Fit::StringUtils::ToInt32(value); } \
    }

#define PQ_HELPER_JSON_FIELD_DECLARE(fieldName, Object, field)                                                    \
    {                                                                                                             \
        fieldName, Fit::Pg::TYPE_VARCHAR,                                                                         \
            [](const Object& object, Fit::vector<Fit::string>& holder) {                                          \
                holder.emplace_back();                                                                            \
                Fit::JsonConverterUtil::MessageToJson(object.field, holder.back());                               \
                return holder.back().c_str();                                                                     \
            },                                                                                                    \
            [](Fit::string value, Object& object) { Fit::JsonConverterUtil::JsonToMessage(value, object.field); } \
    }

#endif  // REGISTRY_SERVER_REPOSITORY_PG_UTILS_FIELD_DECLARE_HELPER_HPP
