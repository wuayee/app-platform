/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/13
 * Notes:       :
 */

#include <fit/external/framework/formatter/json_converter.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<>
__attribute__((visibility ("default"))) FitCode MessageToJson(
    ContextObj ctx, const bytes &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.String(Base64Encode(value).c_str());
    return FIT_OK;
}

template<>
__attribute__((visibility ("default"))) FitCode MessageToJson(
    ContextObj ctx, const Value &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    switch (value.Type()) {
        case ValueType::NULL_:
            writer.Null();
            break;
        case ValueType::BOOL:
            writer.Bool(value.AsBool());
            break;
        case ValueType::NUMBER:
            writer.Double(value.AsDouble());
            break;
        case ValueType::STRING:
            writer.String(value.AsString());
            break;
        case ValueType::OBJECT:
            writer.StartObject();
            for (auto &node : value.AsObject()) {
                writer.String(node.Name());
                MessageToJson(ctx, node.Value(), writer);
            }
            writer.EndObject();
            break;
        case ValueType::ARRAY:
            writer.StartArray();
            for (auto &node : value.AsArray()) {
                MessageToJson(ctx, node, writer);
            }
            writer.EndArray();
            break;
        default:
            break;
    }

    return FIT_OK;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, bytes &value)
{
    if (!jsonValue.IsString()) {
        FIT_LOG_ERROR("JsonValue is not a bytes.");
        return FIT_ERR_DESERIALIZE;
    }

    value = Base64Decode(jsonValue.GetString());
    return FIT_OK;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, Value &value)
{
    switch (jsonValue.GetType()) {
        case rapidjson::kNullType:
            value.SetNull();
            break;
        case rapidjson::kFalseType:
            value.SetBool(false);
            break;
        case rapidjson::kTrueType:
            value.SetBool(true);
            break;
        case rapidjson::kObjectType:
            value.SetObject();
            for (auto &node : jsonValue.GetObject()) {
                JsonToMessage(ctx, node.value, value.AsObject().Add(node.name.GetString()));
            }
            break;
        case rapidjson::kArrayType:
            value.SetArray();
            for (auto &node : jsonValue.GetArray()) {
                JsonToMessage(ctx, node, value.AsArray().PushBack());
            }
            break;
        case rapidjson::kStringType:
            value.SetString(jsonValue.GetString());
            break;
        case rapidjson::kNumberType:
            value.SetDouble(jsonValue.GetDouble());
            break;
        default:
            break;
    }

    return FIT_OK;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, string &value)
{
    if (!jsonValue.IsString()) {
        FIT_LOG_ERROR("JsonValue is not a string.");
        return FIT_ERR_DESERIALIZE_JSON;
    }

    value = jsonValue.GetString();
    return FIT_OK;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, double &value)
{
    if (jsonValue.IsDouble()) {
        value = jsonValue.GetDouble();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable double, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, float &value)
{
    if (jsonValue.IsFloat()) {
        value = jsonValue.GetFloat();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable float, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, uint64_t &value)
{
    if (jsonValue.IsUint64()) {
        value = jsonValue.GetUint64();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable uint64, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, int64_t &value)
{
    if (jsonValue.IsInt64()) {
        value = jsonValue.GetInt64();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable int64, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value &jsonValue, uint32_t &value)
{
    if (jsonValue.IsUint()) {
        value = jsonValue.GetUint();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable uint32, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(ContextObj ctx,
    const rapidjson::Value &jsonValue, int32_t &value)
{
    if (jsonValue.IsInt()) {
        value = jsonValue.GetInt();
        return FIT_OK;
    }
    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable int32, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}

__attribute__((visibility ("default"))) FitCode JsonToMessage(ContextObj ctx,
    const rapidjson::Value &jsonValue, Fit::vector<bool>::reference value)
{
    bool result {};
    auto ret = JsonToMessage(ctx, jsonValue, result);
    if (ret == FIT_OK) {
        value = result;
    }

    return ret;
}

template<>
__attribute__((visibility ("default"))) FitCode JsonToMessage(ContextObj ctx,
    const rapidjson::Value &jsonValue, bool &value)
{
    if (jsonValue.IsBool()) {
        value = jsonValue.GetBool();
        return FIT_OK;
    }

    if (jsonValue.IsString()) {
        if (TryConvertToNumber(jsonValue.GetString(), value)) {
            return FIT_OK;
        }
    }

    FIT_LOG_ERROR("JsonValue is not a convertable bool, type(%d).", jsonValue.GetType());
    return FIT_ERR_DESERIALIZE_JSON;
}
}
}
}
} // LCOV_EXCL_LINE