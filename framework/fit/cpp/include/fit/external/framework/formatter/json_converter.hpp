/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/14 19:48
 * Notes        :
 */

#ifndef JSON_CONVERTER_HPP
#define JSON_CONVERTER_HPP

#include <fit/fit_log.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/any.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/value.hpp>
#include <fit/fit_code.h>
#include <fit/external/util/context/context_api.hpp>

#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <fit/external/util/base64.h>
#include <sstream>

#include "formatter_helper.hpp"
#include "formatter_collector.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<typename T>
inline bool TryConvertToNumber(const Fit::string &data, T &result)
{
    return ((std::istringstream(data) >> result >> std::ws).eof());
}

template<typename T>
inline bool TryConvertToNumber(const char *data, T &result)
{
    return ((std::istringstream(data) >> result >> std::ws).eof());
}

template<>
inline bool TryConvertToNumber(const Fit::string &data, bool &result)
{
    return ((std::istringstream(data.c_str()) >> std::boolalpha >> result >> std::ws).eof());
}

template<>
inline bool TryConvertToNumber(const char *data, bool &result)
{
    return ((std::istringstream(data) >> std::boolalpha >> result >> std::ws).eof());
}

template<typename T>
inline Fit::string TransformToKey(const T &value)
{
    return Fit::to_string(value);
}

template<>
inline Fit::string TransformToKey(const Fit::string &value)
{
    return value;
}

template<typename T>
FitCode TransformKeyToValue(const Fit::string &key, T &value);

template<>
inline FitCode TransformKeyToValue(const Fit::string &key, int32_t &value)
{
    value = Fit::stol(key);
    return FIT_OK;
}

template<typename T>
inline FitCode TransformKeyToValue(const Fit::string &key, uint32_t &value)
{
    value = Fit::stoul(key);
    return FIT_OK;
}

template<typename T>
inline FitCode TransformKeyToValue(const Fit::string &key, int64_t &value)
{
    value = Fit::stoll(key);
    return FIT_OK;
}

template<typename T>
inline FitCode TransformKeyToValue(const Fit::string &key, uint64_t &value)
{
    value = Fit::stoull(key);
    return FIT_OK;
}

template<>
inline FitCode TransformKeyToValue(const Fit::string &key, Fit::string &value)
{
    value = key;
    return FIT_OK;
}

template<typename T>
FitCode MessageToJson(ContextObj ctx, const T &value, rapidjson::Writer<rapidjson::StringBuffer> &writer);

template<>
inline FitCode MessageToJson(ContextObj ctx, const bool &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Bool(value);
    return FIT_OK;
}

inline FitCode MessageToJson(ContextObj ctx, Fit::vector<bool>::reference value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Bool(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const int32_t &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Int(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const uint32_t &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Uint(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const int64_t &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Int64(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const uint64_t &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Uint64(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const float &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Double(value); // 注：writer 里无 Float 函数
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const double &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.Double(value);
    return FIT_OK;
}

template<>
inline FitCode MessageToJson(ContextObj ctx, const Fit::string &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.String(value.c_str()); // 注：仅支持文本字符串
    return FIT_OK;
}

template<>
FitCode MessageToJson(ContextObj ctx, const Fit::bytes &value, rapidjson::Writer<rapidjson::StringBuffer> &writer);

template<>
FitCode MessageToJson(ContextObj ctx, const Fit::Value &value, rapidjson::Writer<rapidjson::StringBuffer> &writer);

template<typename T>
inline FitCode MessageToJson(ContextObj ctx,
    const Fit::vector<T> &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartArray();
    for (auto it = value.begin(); it != value.end(); ++it) {
        auto ret = MessageToJson(ctx, *it, writer);
        if (ret != FIT_OK) {
            return ret;
        }
    }
    writer.EndArray();
    return FIT_OK;
}

template<typename KEY, typename VALUE>
inline FitCode MessageToJson(ContextObj ctx,
    const Fit::map<KEY, VALUE> &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();
    for (auto it = value.begin(); it != value.end(); ++it) {
        auto jsonKey = TransformToKey(it->first);
        writer.Key(jsonKey.c_str());
        auto ret = MessageToJson(ctx, it->second, writer);
        if (ret != FIT_OK) {
            return ret;
        }
    }
    writer.EndObject();
    return FIT_OK;
}

template<typename T>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, T &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, bool &value);

FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::vector<bool>::reference value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, int32_t &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, uint32_t &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, int64_t &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, uint64_t &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, float &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, double &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::string &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::bytes &value);

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::Value &value);

template<typename T>
inline FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::vector<T> &value)
{
    if (jsonValue.IsNull()) {
        return FIT_OK;
    }
    if (!jsonValue.IsArray()) {
        FIT_LOG_ERROR("JsonValue is not an array, type(%d).", jsonValue.GetType());
        return FIT_ERR_DESERIALIZE_JSON;
    }

    auto arr = jsonValue.GetArray();
    value.resize(arr.Size());
    for (size_t i = 0; i < arr.Size(); ++i) {
        auto ret = JsonToMessage(ctx, arr[i], value[i]);
        if (ret != FIT_OK) {
            return ret;
        }
    }

    return FIT_OK;
}

template<typename T>
inline FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, T *&value)
{
    value = Fit::Context::NewObj<T>(ctx);
    if (value == nullptr) {
        return FIT_BAD_ALLOC;
    }
    return JsonToMessage(ctx, jsonValue, *value);
}

template<typename KEY, typename VALUE>
inline FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, Fit::map<KEY, VALUE> &value)
{
    if (!jsonValue.IsObject()) {
        FIT_LOG_ERROR("Not a object, type(%d).", jsonValue.GetType());
        return FIT_ERR_DESERIALIZE_JSON;
    }

    for (auto iter = jsonValue.MemberBegin(); iter != jsonValue.MemberEnd(); ++iter) {
        KEY tmpKey{};
        VALUE tmpValue{};
        auto jsonKey = (iter->name).GetString();
        TransformKeyToValue(jsonKey, tmpKey);
        auto ret = JsonToMessage(ctx, jsonValue[jsonKey], tmpValue);
        if (ret != FIT_OK) {
            return ret;
        }
        value[tmpKey] = tmpValue;
    }
    return FIT_OK;
}

template<typename T, typename F>
FitCode SerializeTemplate(ContextObj ctx, const Argument&arg, Fit::string &result, F&& f)
{
    if (arg.type() != typeid(T)) {
        FIT_LOG_ERROR("Not matched type. [typeName=(%s->%s)]", arg.type().name(), typeid(T).name());
        return FIT_ERR_PARAM;
    }

    using namespace rapidjson;
    StringBuffer sb;
    Writer<StringBuffer> writer(sb);
    T unpackArg = Fit::any_cast<T>(arg);
    if (IsNullArg(unpackArg)) {
        writer.Null();
    } else {
        auto ret = f(ctx, writer, unpackArg);
        if (ret != FIT_OK) {
            return ret;
        }
    }
    auto buffer = sb.GetString();
    result = Fit::string(buffer);

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode SerializeArgToString(ContextObj ctx, const Argument&arg, Fit::string &result)
{
    auto body = [](ContextObj ctx, rapidjson::Writer<rapidjson::StringBuffer> &writer, T &unpackArg) {
        return MessageToJson(ctx, ExtractArgToRef(unpackArg), writer);
    };

    return SerializeTemplate<T>(ctx, arg, result, body);
}

template<typename T, typename ItemT>
FitCode SerializeRepeatedArgToString(ContextObj ctx, const Argument&arg, Fit::string &result)
{
    auto body = [](ContextObj ctx, rapidjson::Writer<rapidjson::StringBuffer> &writer, T &unpackArg) {
        writer.StartArray();
        auto& container = ExtractArgToRef(unpackArg);
        for (auto iter = std::begin(container); iter != std::end(container); ++iter) {
            auto ret = MessageToJson(ctx, *iter, writer);
            if (ret != FIT_OK) {
                return ret;
            }
        }
        writer.EndArray();
        return FIT_OK;
    };
    return SerializeTemplate<T>(ctx, arg, result, body);
}

template<typename T, typename ItemT>
FitCode SerializeMapArgToString(ContextObj ctx, const Argument&arg, Fit::string &result)
{
    auto body = [](ContextObj ctx, rapidjson::Writer<rapidjson::StringBuffer> &writer, T &unpackArg) {
        writer.StartObject();
        for (const auto &item : ExtractArgToRef(unpackArg)) {
            auto key = TransformToKey(item.first);
            writer.Key(key.data());
            auto ret = MessageToJson(ctx, item.second, writer);
            if (ret != FIT_OK) {
                return ret;
            }
        }
        writer.EndObject();
        return FIT_OK;
    };
    return SerializeTemplate<T>(ctx, arg, result, body);
}

template<typename T, typename F>
FitCode DeserializeTemplate(ContextObj ctx, const Fit::string &str, Argument&result, F&& f)
{
    rapidjson::Document doc;
    doc.Parse(str.c_str());
    if (doc.HasParseError()) {
        FIT_LOG_ERROR("Parse json has error in deserialize. [errorCode=%d, json=%s]", doc.GetParseError(), str.c_str());
        return FIT_ERR_DESERIALIZE_JSON;
    }
    if (doc.IsNull()) {
        result = T{};
        return FIT_OK;
    }
    return f(ctx, doc, result);
}

template<typename T, typename ItemT>
FitCode DeserializeStringToArg(ContextObj ctx, const Fit::string &str, Argument&result)
{
    auto body = [](ContextObj ctx, const rapidjson::Document& doc, Argument&result) {
        ItemT* unpackArg{};
        if (IsNullArg(CreateArg(ctx, unpackArg))) {
            FIT_LOG_ERROR("Failed to create arg. [typeName=%s]", typeid(T).name());
            return FIT_BAD_ALLOC;
        }
        auto ret = JsonToMessage(ctx, doc, ExtractArgToRef(unpackArg));
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to convert to message. [typeName=%s, ret=%x]", typeid(T).name(), ret);
            return ret;
        }
        result = T{unpackArg};
        return FIT_OK;
    };

    return DeserializeTemplate<T>(ctx, str, result, body);
}

template<typename T, typename ItemT>
FitCode DeserializeStringToRepeatedArg(ContextObj ctx, const Fit::string &str, Argument&result)
{
    auto body = [](ContextObj ctx, const rapidjson::Document& doc, Argument&result) {
        if (!doc.IsArray()) {
            FIT_LOG_ERROR("Json is not an array. [type=%d]", doc.GetType());
            return FIT_ERR_DESERIALIZE_JSON;
        }

        ItemT *unpackArg {};
        if (IsNullArg(CreateArg(ctx, unpackArg))) {
            FIT_LOG_ERROR("Failed to create arg. [typeName=%s]", typeid(T).name());
            return FIT_BAD_ALLOC;
        }

        auto &container = ExtractArgToRef(unpackArg);
        auto arr = doc.GetArray();
        container.resize(arr.Size());
        int32_t i = 0;
        for (const auto &itemArr : arr) {
            auto ret = JsonToMessage(ctx, itemArr, container[i]);
            if (ret != FIT_OK) {
                FIT_LOG_ERROR("Failed to convert to message. [ret=%x, index=%d, typeName=%s]", ret, i,
                    typeid(ItemT).name());
                return ret;
            }
            ++i;
        }
        result = T {unpackArg};
        return FIT_OK;
    };

    return DeserializeTemplate<T>(ctx, str, result, body);
}


template<typename T, typename ItemT>
FitCode DeserializeStringToMapArg(ContextObj ctx, const Fit::string &str, Argument &result)
{
    auto body = [](ContextObj ctx, const rapidjson::Document& doc, Argument&result) {
        if (!doc.IsObject()) {
            FIT_LOG_ERROR("Json is not an array. [type=%d]", doc.GetType());
            return FIT_ERR_DESERIALIZE_JSON;
        }
        ItemT* unpackArg{};
        if (IsNullArg(CreateArg(ctx, unpackArg))) {
            FIT_LOG_ERROR("Failed to create arg. [typeName=%s]", typeid(T).name());
            return FIT_BAD_ALLOC;
        }

        auto& container = ExtractArgToRef(unpackArg);
        using KEY = typename std::remove_pointer<ItemT>::type::key_type;
        using VALUE = typename std::remove_pointer<ItemT>::type::mapped_type;
        for (auto iter = doc.MemberBegin(); iter != doc.MemberEnd(); ++iter) {
            KEY key{};
            VALUE value{};
            auto jsonKey = (iter->name).GetString();
            TransformKeyToValue(jsonKey, key);
            auto ret = JsonToMessage(ctx, doc[jsonKey], value);
            if (ret != FIT_OK) {
                FIT_LOG_ERROR("Failed to convert to message. [ret=%x, key=%s, typeName=%s]", ret, jsonKey,
                    typeid(ItemT).name());
                return ret;
            }
            container[key] = value;
        }
        result = T{unpackArg};
        return FIT_OK;
    };

    return DeserializeTemplate<T>(ctx, str, result, body);
}

template<typename T>
using ConverterBuilder = ConverterBuilder<T, PROTOCOL_TYPE_JSON>;

template<typename T>
using CreateArgOutBuilder = CreateArgOutBuilder<T>;
}
template<>
class ArgConverterDispatcher<PROTOCOL_TYPE_JSON> {
public:
    template<typename T>
    static ArgConverter Raw()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Json::SerializeArgToString<T, ItemT>, Json::DeserializeStringToArg<T, ItemT>};
    }
    template<typename T>
    static ArgConverter Repeated()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Json::SerializeRepeatedArgToString<T, ItemT>, Json::DeserializeStringToRepeatedArg<T, ItemT>};
    }

    template<typename T>
    static ArgConverter Mapped()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Json::SerializeMapArgToString<T, ItemT>, Json::DeserializeStringToMapArg<T, ItemT>};
    }
};
}
}
}

#endif // JSONCONVERTER_HPP
