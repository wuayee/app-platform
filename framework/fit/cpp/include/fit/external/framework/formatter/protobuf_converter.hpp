/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/27
 * Notes:       :
 */

#ifndef PROTOBUF_CONVERTER_HPP
#define PROTOBUF_CONVERTER_HPP

#include <type_traits>
#include <fit/stl/vector.hpp>
#include <fit/stl/any.hpp>
#include <fit/stl/string.hpp>
#include <fit/value.hpp>
#include <google/protobuf/map.h>
#include <google/protobuf/repeated_field.h>
#include <fit/fit_code.h>
#include <fit/stl/map.hpp>
#include <fit/external/util/context/context_api.hpp>
#include "formatter_collector.hpp"
#include "formatter_helper.hpp"
#include "protobuf_traits.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Protobuf {
template<typename T, typename PROTO>
inline FitCode MessageToProtobuf(ContextObj ctx, const T& src, PROTO& dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::string& src, std::string& dst)
{
    dst = Fit::to_std_string(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::bytes& src, std::string& dst)
{
    dst = std::string(src.data(), src.size());
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::string& src,
    ProtobufTypeTraits<Fit::string>::type& dst)
{
    dst.set_value(std::string(src.data(), src.size()));
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::bytes& src,
    ProtobufTypeTraits<Fit::bytes>::type& dst)
{
    dst.set_value(Fit::to_std_string(src));
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const bool& src,
    ProtobufTypeTraits<bool>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::vector<bool>::reference src, bool& dst)
{
    dst = src;
    return FIT_OK;
}

inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::vector<bool>::reference src,
    ProtobufTypeTraits<bool>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const int32_t& src,
    ProtobufTypeTraits<int32_t>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const uint32_t& src,
    ProtobufTypeTraits<uint32_t>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const int64_t& src,
    ProtobufTypeTraits<int64_t>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const uint64_t& src,
    ProtobufTypeTraits<uint64_t>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const float& src,
    ProtobufTypeTraits<float>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const double& src,
    ProtobufTypeTraits<double>::type& dst)
{
    dst.set_value(src);
    return FIT_OK;
}

template<>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::Value &src, ProtobufTypeTraits<Fit::Value>::type &dst);

template<typename C_REPEATED, typename PROTO_REPEATED>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::vector<C_REPEATED>& src,
    google::protobuf::RepeatedPtrField<PROTO_REPEATED>& ptrDst)
{
    if (src.empty()) {
        return FIT_OK;
    }
    ptrDst.Reserve(src.size());
    for (uint32_t i = 0; i < src.size(); i++) {
        MessageToProtobuf(ctx, src[i], *ptrDst.Add());
    }
    return FIT_OK;
}

template<typename C_REPEATED, typename PROTO_REPEATED>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::vector<C_REPEATED>& src,
    google::protobuf::RepeatedField<PROTO_REPEATED>& dst)
{
    if (src.empty()) {
        return FIT_OK;
    }
    dst.Reserve(src.size());
    for (uint32_t i = 0; i < src.size(); i++) {
        MessageToProtobuf(ctx, src[i], *dst.Add());
    }
    return FIT_OK;
}

template<typename PB_KEY, typename PB_VALUE, typename KEY, typename VALUE>
inline FitCode MessageToProtobuf(ContextObj ctx, const Fit::map<KEY, VALUE> &src,
    ::google::protobuf::Map<PB_KEY, PB_VALUE> &dst)
{
    if (src.empty()) {
        return FIT_OK;
    }

    for (auto it = src.begin(); it != src.end(); ++it) {
        PB_KEY key{};
        PB_VALUE value{};
        MessageToProtobuf(ctx, it->first, key);
        MessageToProtobuf(ctx, it->second, value);
        dst[key] = value;
    }
    return FIT_OK;
}


/// proto to message

template<typename PROTO, typename T>
inline FitCode ProtobufToMessage(ContextObj ctx, const PROTO& src, T& dst)
{
    dst = src;
    return FIT_OK;
}

inline FitCode ProtobufToMessage(ContextObj ctx, const std::string& src, Fit::string& dst)
{
    dst = Fit::to_fit_string(src);
    return FIT_OK;
}

inline FitCode ProtobufToMessage(ContextObj ctx, const std::string& src, Fit::bytes& dst)
{
    dst = Fit::string(src.data(), src.size());
    return FIT_OK;
}

inline FitCode ProtobufToMessage(ContextObj ctx, const bool& src, Fit::vector<bool>::reference dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const BooleanMessage& src, bool& dst)
{
    dst = src.value();
    return FIT_OK;
}

inline FitCode ProtobufToMessage(ContextObj ctx, const BooleanMessage& src, Fit::vector<bool>::reference dst)
{
    dst = src.value();
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const IntegerMessage& src, int32_t& dst)
{
    dst = src.value();
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const IntegerMessage& src, uint32_t& dst)
{
    dst = static_cast<uint32_t>(src.value());
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const LongMessage& src, int64_t& dst)
{
    dst = src.value();
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const LongMessage& src, uint64_t& dst)
{
    dst = static_cast<uint64_t>(src.value());
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const FloatMessage& src, float& dst)
{
    dst = src.value();
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const DoubleMessage& src, double& dst)
{
    dst = src.value();
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const StringMessage& src, Fit::string& dst)
{
    dst = Fit::to_fit_string(src.value());
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const ByteStringMessage& src, Fit::bytes& dst)
{
    dst = Fit::to_fit_string(src.value());
    return FIT_OK;
}

template<>
inline FitCode ProtobufToMessage(ContextObj ctx, const StringMessage& src, Fit::Value& dst);

template<typename PROTO_REPEATED, typename T_REPEATED>
inline FitCode ProtobufToMessage(ContextObj ctx,
    const ::google::protobuf::RepeatedPtrField<PROTO_REPEATED>& ptrSrc,
    Fit::vector<T_REPEATED>& dst)
{
    if (ptrSrc.empty()) {
        return FIT_OK;
    }
    dst.resize(ptrSrc.size());
    for (int i = 0; i < ptrSrc.size(); i++) {
        ProtobufToMessage(ctx, ptrSrc[i], dst[i]);
    }
    return FIT_OK;
}

template<typename PROTO_REPEATED, typename T_REPEATED>
inline FitCode ProtobufToMessage(ContextObj ctx,
    const ::google::protobuf::RepeatedField<PROTO_REPEATED>& src, Fit::vector<T_REPEATED>& dst)
{
    if (src.empty()) {
        return FIT_OK;
    }
    dst.resize(src.size());
    for (int i = 0; i < src.size(); i++) {
        ProtobufToMessage(ctx, src[i], dst[i]);
    }
    return FIT_OK;
}

template<typename PROTO, typename T>
inline FitCode ProtobufToMessage(ContextObj ctx, const PROTO& src, T*& dst)
{
    dst = Fit::Context::NewObj<T>(ctx);
    if (!dst) {
        return FIT_BAD_ALLOC;
    }

    return ProtobufToMessage(ctx, src, *dst);
}

template<
    typename PB_KEY,
    typename PB_VALUE,
    typename KEY,
    typename VALUE>
inline FitCode ProtobufToMessage(ContextObj ctx, const ::google::protobuf::Map<PB_KEY, PB_VALUE> &src,
    Fit::map<KEY, VALUE> &dst)
{
    if (src.empty()) {
        return FIT_OK;
    }

    for (auto it = src.begin(); it != src.end(); ++it) {
        KEY key{};
        VALUE value{};
        ProtobufToMessage(ctx, it->first, key);
        ProtobufToMessage(ctx, it->second, value);
        dst[key] = value;
    }
    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode SerializeArgToString(ContextObj ctx, const Argument& arg, Fit::string& result)
{
    if (arg.type() != typeid(T)) {
        return FIT_ERR_PARAM;
    }
    T unpackArg = Fit::any_cast<T>(arg);
    if (IsNullArg(unpackArg)) {
        return FIT_NULL_PARAM;
    }
    using PbType = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type>::type;
    PbType pb{};
    auto ret = MessageToProtobuf(ctx, ExtractArgToRef(unpackArg), pb);
    if (ret != FIT_OK) {
        return ret;
    }
    auto buffer = pb.SerializeAsString();
    result = Fit::string(buffer.data(), buffer.size());

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode SerializeRepeatedArgToString(ContextObj ctx, const Argument& arg, Fit::string& result)
{
    if (arg.type() != typeid(T)) {
        return FIT_ERR_PARAM;
    }
    T unpackArg = Fit::any_cast<T>(arg);
    if (IsNullArg(unpackArg)) {
        return FIT_NULL_PARAM;
    }

    auto& container = ExtractArgToRef(unpackArg);
    using ElementType = typename std::decay<
        typename std::decay<decltype(ExtractArgToRef(unpackArg))>::type::value_type>::type;
    FitRepeatedArgument repeatedPb;
    repeatedPb.mutable_arguments()->Reserve(container.size());

    for (auto iter = std::begin(container); iter != std::end(container); ++iter) {
        using PbType = typename ProtobufTypeTraits<ElementType>::type;
        PbType pb{};
        auto ret = MessageToProtobuf(ctx, *iter, pb);
        if (ret != FIT_OK) {
            return ret;
        }

        repeatedPb.add_arguments(pb.SerializeAsString());
    }

    auto buffer = repeatedPb.SerializeAsString();
    result = Fit::string(buffer.data(), buffer.size());

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode SerializeMapArgToString(ContextObj ctx, const Argument& arg, Fit::string& result)
{
    if (arg.type() != typeid(T)) {
        return FIT_ERR_PARAM;
    }
    T unpackArg = Fit::any_cast<T>(arg);
    if (IsNullArg(unpackArg)) {
        return FIT_NULL_PARAM;
    }

    using PbKey = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type::key_type>::type;
    using PbValue = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type::mapped_type>::type;

    FitMapArgument mapArgument{};
    auto& container = ExtractArgToRef(unpackArg);
    mapArgument.mutable_arguments()->Reserve(container.size());
    for (auto it = container.begin(); it != container.end(); it++) {
        auto entry = mapArgument.mutable_arguments()->Add();
        PbKey key{};
        PbValue value{};
        MessageToProtobuf(ctx, it->first, key);
        MessageToProtobuf(ctx, it->second, value);
        entry->set_key(key.SerializeAsString());
        entry->set_value(value.SerializeAsString());
    }
    auto buffer = mapArgument.SerializeAsString();
    result = Fit::string(buffer.data(), buffer.size());

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode DeserializeStringToArg(ContextObj ctx, const Fit::string& buffer, Argument& result)
{
    ItemT* unpackArg{};
    if (IsNullArg(CreateArg(ctx, unpackArg))) {
        return FIT_BAD_ALLOC;
    }
    using PbType = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type>::type;
    PbType pb{};
    if (!pb.ParseFromArray(buffer.data(), buffer.size())) {
        return FIT_ERR_DESERIALIZE;
    }

    auto ret = ProtobufToMessage(ctx, pb, ExtractArgToRef(unpackArg));
    if (ret != FIT_OK) {
        return ret;
    }

    result = T{unpackArg};

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode DeserializeStringToRepeatedArg(ContextObj ctx, const Fit::string& buffer, Argument& result)
{
    ItemT* unpackArg{};
    if (IsNullArg(CreateArg(ctx, unpackArg))) {
        return FIT_BAD_ALLOC;
    }

    FitRepeatedArgument repeatedPb;
    if (!repeatedPb.ParseFromArray(buffer.data(), buffer.size())) {
        return FIT_ERR_DESERIALIZE;
    }

    auto& container = ExtractArgToRef(unpackArg);
    using ElementType = typename std::decay<decltype(*container.begin())>::type;
    container.resize(repeatedPb.arguments().size());
    for (int32_t i = 0; i < repeatedPb.arguments().size(); ++i) {
        auto& item = repeatedPb.arguments(i);
        using PbType = typename ProtobufTypeTraits<ElementType>::type;
        PbType pb{};
        if (!pb.ParseFromArray(item.data(), item.size())) {
            return FIT_ERR_DESERIALIZE;
        }
        auto ret = ProtobufToMessage(ctx, pb, container[i]);
        if (ret != FIT_OK) {
            return ret;
        }
    }

    result = T{unpackArg};

    return FIT_OK;
}

template<typename T, typename ItemT>
FitCode DeserializeStringToMapArg(ContextObj ctx, const Fit::string& buffer, Argument& result)
{
    ItemT* unpackArg{};
    if (IsNullArg(CreateArg(ctx, unpackArg))) {
        return FIT_BAD_ALLOC;
    }

    FitMapArgument mapArgument;
    if (!mapArgument.ParseFromArray(buffer.data(), buffer.size())) {
        return FIT_ERR_DESERIALIZE;
    }

    using PbKey = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type::key_type>::type;
    using PbValue = typename ProtobufTypeTraits<typename std::remove_pointer<ItemT>::type::mapped_type>::type;
    using Key = typename std::remove_pointer<ItemT>::type::key_type;
    using Value = typename std::remove_pointer<ItemT>::type::mapped_type;

    auto& container = ExtractArgToRef(unpackArg);
    for (const auto &entry : mapArgument.arguments()) {
        Key key{};
        Value value{};
        PbKey pbKey{};
        PbValue pbValue{};
        pbKey.ParseFromString(entry.key());
        pbValue.ParseFromString(entry.value());
        ProtobufToMessage(ctx, pbKey, key);
        ProtobufToMessage(ctx, pbValue, value);
        container[key] = value;
    }
    result = T{unpackArg};

    return FIT_OK;
}

template<typename T>
using ConverterBuilder = ConverterBuilder<T, PROTOCOL_TYPE_PROTOBUF>;

template<typename T>
using CreateArgOutBuilder = CreateArgOutBuilder<T>;
}
template<>
class ArgConverterDispatcher<PROTOCOL_TYPE_PROTOBUF> {
public:
    template<typename T>
    static ArgConverter Raw()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Protobuf::SerializeArgToString<T, ItemT>, Protobuf::DeserializeStringToArg<T, ItemT>};
    }
    template<typename T>
    static ArgConverter Repeated()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Protobuf::SerializeRepeatedArgToString<T, ItemT>, Protobuf::DeserializeStringToRepeatedArg<T, ItemT>};
    }

    template<typename T>
    static ArgConverter Mapped()
    {
        static_assert(!std::is_reference<T>::value, "can't given a reference type");
        using ItemT = typename std::remove_const<typename std::remove_pointer<T>::type>::type;
        return {Protobuf::SerializeMapArgToString<T, ItemT>, Protobuf::DeserializeStringToMapArg<T, ItemT>};
    }
};
}
}
}
#endif // PROTOBUFCONVERTER_HPP
