/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#ifndef FORMATTERSERVICE_HPP
#define FORMATTERSERVICE_HPP

#include <fit/fit_code.h>
#include <fit/stl/vector.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/internal/runtime/runtime_element.hpp>
#include "formatter_repo.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
class FormatterEntry {
public:
    virtual ~FormatterEntry() = default;
    virtual int32_t GetFormateType() const = 0;
    virtual FitCode SerializeRequest(
        ContextObj ctx, const ArgConverterList& converters, const BaseSerialization& target,
        const Arguments& args, string& result) = 0;
    virtual FitCode DeserializeRequest(
        ContextObj ctx, const ArgConverterList& converters, const BaseSerialization& target,
        const string& buffer, Arguments& result) = 0;
};

struct Response {
    FitCode code;
    Fit::string msg;
    Arguments args;
};

class FormatterService : public RuntimeElementBase {
public:
    FormatterService() : RuntimeElementBase("formatterService") {};
    ~FormatterService() override = default;
    virtual FitCode SerializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization, const Arguments& args,
        Fit::string& result) = 0;
    virtual FitCode DeserializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Fit::string& buffer, Arguments& args) = 0;

    virtual Fit::string SerializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Response& response) = 0;
    virtual Response DeserializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Fit::string& buffer) = 0;

    virtual Arguments CreateArgOut(ContextObj ctx, const BaseSerialization& baseSerialization) = 0;
    virtual FormatterMetaPtr GetFormatter(const BaseSerialization& baseSerialization) = 0;

    virtual Fit::vector<int32_t> GetFormats(const Fit::string &genericId) = 0;

    virtual void ClearAllFormats() = 0;
};
using FormatterServicePtr = std::shared_ptr<FormatterService>;

FormatterServicePtr CreateFormatterService(Runtime* runtime);
void FinitFormatterService();
}
}
}
#endif // FORMATTERSERVICE_HPP
