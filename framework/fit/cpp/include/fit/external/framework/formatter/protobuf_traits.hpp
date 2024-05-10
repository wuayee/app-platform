/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2022-04-21
 * Notes:       :
 */

#ifndef FIT_PROTOBUF_TRAITS_HPP
#define FIT_PROTOBUF_TRAITS_HPP

#include <type_traits>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/value.hpp>
#include "serialization.pb.h"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Protobuf {
using ::com::huawei::fitframework::broker::serialization::FitRequest;
using ::com::huawei::fitframework::broker::serialization::FitResponse;
using ::com::huawei::fitframework::broker::serialization::FitRepeatedArgument;
using ::com::huawei::fitframework::broker::serialization::ByteStringMessage;
using ::com::huawei::fitframework::broker::serialization::StringMessage;
using ::com::huawei::fitframework::broker::serialization::BooleanMessage;
using ::com::huawei::fitframework::broker::serialization::IntegerMessage;
using ::com::huawei::fitframework::broker::serialization::LongMessage;
using ::com::huawei::fitframework::broker::serialization::FloatMessage;
using ::com::huawei::fitframework::broker::serialization::DoubleMessage;
using ::com::huawei::fitframework::broker::serialization::FitMapArgument;
using ::com::huawei::fitframework::broker::serialization::MapEntry;

template<typename T>
class ProtobufTypeTraits {
};

template<>
class ProtobufTypeTraits<bool> {
public:
    using type = BooleanMessage;
};

template<>
class ProtobufTypeTraits<Fit::vector<bool>::reference> {
public:
    using type = BooleanMessage;
};

template<>
class ProtobufTypeTraits<int32_t> {
public:
    using type = IntegerMessage;
};

template<>
class ProtobufTypeTraits<uint32_t> {
public:
    using type = IntegerMessage;
};

template<>
class ProtobufTypeTraits<int64_t> {
public:
    using type = LongMessage;
};

template<>
class ProtobufTypeTraits<uint64_t> {
public:
    using type = LongMessage;
};

template<>
class ProtobufTypeTraits<float> {
public:
    using type = FloatMessage;
};

template<>
class ProtobufTypeTraits<double> {
public:
    using type = DoubleMessage;
};

template<>
class ProtobufTypeTraits<Fit::string> {
public:
    using type = StringMessage;
};

template<>
class ProtobufTypeTraits<Fit::bytes> {
public:
    using type = ByteStringMessage;
};

template<>
class ProtobufTypeTraits<Fit::Value> {
public:
    using type = StringMessage;
};
}
}
}
}
#endif // FIT_PROTOBUF_TRAITS_HPP
