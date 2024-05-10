/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/5
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/fit_log.h>
#include <framework/formatter_util.hpp>
#include <rapidjson/stringbuffer.h>
#include <rapidjson/writer.h>
#include <plugin/plugin_config_impl.hpp>
#include "prepared_data/generiacable/add.hpp"
#include "prepared_data/formatter/add.hpp"

using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Annotation;
using namespace ::Fit::Framework::Formatter;
using namespace ::Fit::Benchmark;

namespace {
FormatterServicePtr PrepareFormatterServiceWithProtobuf(int32_t redundantFormatterCount)
{
    auto repo = Fit::Framework::Formatter::CreateFormatterRepo();
    RegisterAddProtobufFormatter(repo);
    PrepareRedundantProtobufFormatter(repo, redundantFormatterCount);

    return CreateFormatterService(repo);
}

FormatterServicePtr PrepareEnvWithProtobuf(benchmark::State &state)
{
    FitLogSetOutput(FitLogOutputType::file);
    auto redundantFormatterCount = state.range(0);
    return PrepareFormatterServiceWithProtobuf(redundantFormatterCount);
}

FormatterServicePtr PrepareFormatterServiceWithJson(int32_t redundantFormatterCount)
{
    auto repo = Fit::Framework::Formatter::CreateFormatterRepo();
    RegisterAddJsonFormatter(repo);
    PrepareRedundantJsonFormatter(repo, redundantFormatterCount);

    return CreateFormatterService(repo);
}

FormatterServicePtr PrepareEnvWithJson(benchmark::State &state)
{
    FitLogSetOutput(FitLogOutputType::file);
    auto redundantFormatterCount = state.range(0);
    return PrepareFormatterServiceWithJson(redundantFormatterCount);
}

std::string RawProtobufSerializeRequest_Add(int32_t a, int32_t b)
{
    Protobuf::FitRequest request;
    request.mutable_arguments()->Reserve(2);
    FitRequestInitNullArgumentFlags(*request.mutable_argumentnulls(), 2);
    for (uint32_t i = 0; i < 2; ++i) {
        Protobuf::IntegerMessage pb;
        pb.set_value(i);

        FitRequestSetNullArgumentFlags(*request.mutable_argumentnulls(), i, false);
        request.mutable_arguments()->Add(pb.SerializeAsString());
    }
    return request.SerializeAsString();
}

void RawProtobufDeserializeRequest_Add(const Fit::string &buffer, Fit::vector<int32_t> &result)
{
    Protobuf::FitRequest request;
    if (!request.ParseFromArray(buffer.data(), buffer.size())) {
        FIT_LOG_ERROR("Error deserialize.");
        return;
    }

    if (request.arguments().size() != 2) {
        FIT_LOG_ERROR("Not matched arguments size");
        return;
    }

    for (int32_t i = 0; i < request.arguments().size(); ++i) {
        Protobuf::IntegerMessage pb;
        pb.ParseFromString(request.arguments(i));
        result[i] = pb.value();
    }
}

std::string SerializeRequest_RawJson_Add_With_Rapidjson(int32_t a, int32_t b)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    writer.StartArray();
    for (uint32_t i = 0; i < 2; ++i) {
        rapidjson::StringBuffer valueStringBuffer;
        rapidjson::Writer<rapidjson::StringBuffer> valueWriter(valueStringBuffer);
        valueWriter.Int(i);

        rapidjson::Document doc;
        doc.Parse(valueStringBuffer.GetString());
        doc.Accept(writer);
    }
    writer.EndArray();
    return sb.GetString();
}

void DeserializeRequest_RawJson_Add_With_Rapidjson(const Fit::string &buffer, Fit::vector<int32_t> &result)
{
    rapidjson::Document doc;
    doc.Parse(buffer.c_str());
    if (doc.HasParseError()) {
        FIT_LOG_ERROR("Parse json error: json = %s.", buffer.c_str());
        return;
    }

    auto arr = doc.GetArray();
    for (size_t i = 0; i < arr.Size(); ++i) {
        if (!arr[i].IsNumber()) {
            FIT_LOG_ERROR("Not match type, expect number.");
            return;
        }
        result[i] = arr[i].GetInt();
    }
}
}

// benchmark case
static void BM_FormatterService_GetFormatter(benchmark::State &state)
{
    static FormatterServicePtr formatterService;
    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithProtobuf(state);
    }

    const int32_t a {1};
    const int32_t b {2};
    Arguments args {&a, &b};
    BaseSerialization target;
    target.genericId = Add::GENERIC_ID;
    target.formats = {0};
    target.fitableType = FitableType::MAIN;
    for (auto _ : state) {
        formatterService->GetFormatter(target);
    }

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_GetFormatter)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_GetFormatter)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)->Threads(100);

static void BM_RawProtobufSerializeRequest_Add(benchmark::State &state)
{
    const int32_t a {1};
    const int32_t b {2};
    for (auto _ : state) {
        benchmark::DoNotOptimize(RawProtobufSerializeRequest_Add(a, b));
    }
}

BENCHMARK(BM_RawProtobufSerializeRequest_Add);

static void BM_FormatterService_SerializeRequest_Protobuf_Add(benchmark::State &state)
{
    static FormatterServicePtr formatterService;
    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithProtobuf(state);
    }

    const int32_t a {1};
    const int32_t b {2};
    Arguments args {&a, &b};
    BaseSerialization target;
    target.genericId = Add::GENERIC_ID;
    target.formats = {0};
    target.fitableType = FitableType::MAIN;
    for (auto _ : state) {
        Fit::string result;
        formatterService->SerializeRequest(nullptr, target, args, result);
    }

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_SerializeRequest_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_SerializeRequest_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

static void BM_RawProtobufDeserializeRequest_Add(benchmark::State &state)
{
    const int32_t a {1};
    const int32_t b {2};
    auto serializeBuffer = RawProtobufSerializeRequest_Add(a, b);
    Fit::vector<int32_t> deserializeResult;
    deserializeResult.reserve(2);
    for (auto _ : state) {
        RawProtobufDeserializeRequest_Add(serializeBuffer, deserializeResult);
    }
}

BENCHMARK(BM_RawProtobufDeserializeRequest_Add);

static void BM_FormatterService_DeserializeRequest_Protobuf_Add(benchmark::State &state)
{
    static Fit::string serializeBuffer;
    static BaseSerialization target;
    static FormatterServicePtr formatterService;

    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithProtobuf(state);
        const int32_t a {1};
        const int32_t b {2};
        Arguments args {&a, &b};
        target.genericId = Add::GENERIC_ID;
        target.formats = {0};
        target.fitableType = FitableType::MAIN;
        formatterService->SerializeRequest(nullptr, target, args, serializeBuffer);
    }

    auto ctx = NewContextDefault();
    for (auto _ : state) {
        Arguments argsResult;
        formatterService->DeserializeRequest(ctx, target, serializeBuffer, argsResult);
    }
    ContextDestroy(ctx);

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_DeserializeRequest_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_DeserializeRequest_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

static void BM_FormatterService_SerializeResponse_Protobuf_Add(benchmark::State &state)
{
    static FormatterServicePtr formatterService;
    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithProtobuf(state);
    }

    int32_t tmp {3};
    int32_t *result = &tmp;
    Arguments args {&result};
    Response inputResponse;
    inputResponse.args = args;
    BaseSerialization target;
    target.genericId = Add::GENERIC_ID;
    target.formats = {0};
    target.fitableType = FitableType::MAIN;
    for (auto _ : state) {
        auto buffer = formatterService->SerializeResponse(nullptr, target, inputResponse);
    }

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_SerializeResponse_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_SerializeResponse_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

static void BM_FormatterService_DeserializeResponse_Protobuf_Add(benchmark::State &state)
{
    static Fit::string serializeBuffer;
    static BaseSerialization target;
    static FormatterServicePtr formatterService;

    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithProtobuf(state);
        int32_t tmp {3};
        int32_t *result = &tmp;
        Arguments args {&result};
        Response inputResponse;
        inputResponse.args = args;
        target.genericId = Add::GENERIC_ID;
        target.formats = {0};
        target.fitableType = FitableType::MAIN;
        serializeBuffer = formatterService->SerializeResponse(nullptr, target, inputResponse);
    }

    auto ctx = NewContextDefault();
    for (auto _ : state) {
        formatterService->DeserializeResponse(ctx, target, serializeBuffer);
    }
    ContextDestroy(ctx);

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_DeserializeResponse_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_DeserializeResponse_Protobuf_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

// json
static void BM_SerializeRequest_RawJson_Add_With_Rapidjson(benchmark::State &state)
{
    const int32_t a {1};
    const int32_t b {2};
    for (auto _ : state) {
        benchmark::DoNotOptimize(SerializeRequest_RawJson_Add_With_Rapidjson(a, b));
    }
}

BENCHMARK(BM_SerializeRequest_RawJson_Add_With_Rapidjson);

static void BM_FormatterService_SerializeRequest_Json_Add(benchmark::State &state)
{
    static FormatterServicePtr formatterService;
    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithJson(state);
    }

    const int32_t a {1};
    const int32_t b {2};
    Arguments args {&a, &b};
    BaseSerialization target;
    target.genericId = Add::GENERIC_ID;
    target.formats = {1};
    target.fitableType = FitableType::MAIN;
    Fit::string result;
    for (auto _ : state) {
        formatterService->SerializeRequest(nullptr, target, args, result);
    }

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_SerializeRequest_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_SerializeRequest_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)->Threads(100);

static void BM_DeserializeRequest_RawJson_Add_With_Rapidjson_Add(benchmark::State &state)
{
    const int32_t a {1};
    const int32_t b {2};
    auto serializeBuffer = SerializeRequest_RawJson_Add_With_Rapidjson(a, b);
    Fit::vector<int32_t> deserializeResult;
    deserializeResult.reserve(2);
    for (auto _ : state) {
        DeserializeRequest_RawJson_Add_With_Rapidjson(serializeBuffer, deserializeResult);
    }
}

BENCHMARK(BM_DeserializeRequest_RawJson_Add_With_Rapidjson_Add);

static void BM_FormatterService_DeserializeRequest_Json_Add(benchmark::State &state)
{
    static Fit::string serializeBuffer;
    static BaseSerialization target;
    static FormatterServicePtr formatterService;

    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithJson(state);
        const int32_t a {1};
        const int32_t b {2};
        Arguments args {&a, &b};
        target.genericId = Add::GENERIC_ID;
        target.formats = {1};
        target.fitableType = FitableType::MAIN;
        formatterService->SerializeRequest(nullptr, target, args, serializeBuffer);
    }

    auto ctx = NewContextDefault();
    for (auto _ : state) {
        Arguments argsResult;
        formatterService->DeserializeRequest(ctx, target, serializeBuffer, argsResult);
    }
    ContextDestroy(ctx);

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_DeserializeRequest_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_DeserializeRequest_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

static void BM_FormatterService_SerializeResponse_Json_Add(benchmark::State &state)
{
    static FormatterServicePtr formatterService;
    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithJson(state);
    }

    int32_t tmp {3};
    int32_t *result = &tmp;
    Arguments args {&result};
    Response inputResponse;
    inputResponse.args = args;
    BaseSerialization target;
    target.genericId = Add::GENERIC_ID;
    target.formats = {1};
    target.fitableType = FitableType::MAIN;
    for (auto _ : state) {
        auto buffer = formatterService->SerializeResponse(nullptr, target, inputResponse);
    }

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_SerializeResponse_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_SerializeResponse_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);

static void BM_FormatterService_DeserializeResponse_Json_Add(benchmark::State &state)
{
    static Fit::string serializeBuffer;
    static BaseSerialization target;
    static FormatterServicePtr formatterService;

    if (state.thread_index == 0) {
        formatterService = PrepareEnvWithJson(state);
        int32_t tmp {3};
        int32_t *result = &tmp;
        Arguments args {&result};
        Response inputResponse {};
        inputResponse.args = args;
        target.genericId = Add::GENERIC_ID;
        target.formats = {1};
        target.fitableType = FitableType::MAIN;
        serializeBuffer = formatterService->SerializeResponse(nullptr, target, inputResponse);
    }

    auto ctx = NewContextDefault();
    for (auto _ : state) {
        formatterService->DeserializeResponse(ctx, target, serializeBuffer);
    }
    ContextDestroy(ctx);

    if (state.thread_index == 0) {
        formatterService.reset();
    }
}

BENCHMARK(BM_FormatterService_DeserializeResponse_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192);
BENCHMARK(BM_FormatterService_DeserializeResponse_Json_Add)->Arg(1)->Arg(64)->Arg(512)->Arg(4096)->Arg(8192)
    ->Threads(100);
