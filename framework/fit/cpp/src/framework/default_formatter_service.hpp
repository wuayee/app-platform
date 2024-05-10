/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#ifndef DEFAULT_FORMATTER_SERVICE_HPP
#define DEFAULT_FORMATTER_SERVICE_HPP

#include <fit/stl/mutex.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include "include/fit/internal/framework/formatter_repo.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
class DefaultFormatterService : public FormatterService {
public:
    DefaultFormatterService();
    explicit DefaultFormatterService(FormatterRepoPtr repo);
    ~DefaultFormatterService() override = default;

    bool Start() override;
    bool Stop() override;

    FitCode SerializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization, const Arguments& args,
        Fit::string& result) override;
    FitCode DeserializeRequest(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Fit::string& buffer, Arguments& args) override;

    Fit::string SerializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Response& response) override;
    Response DeserializeResponse(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Fit::string& buffer) override;

    Arguments CreateArgOut(ContextObj ctx, const BaseSerialization& baseSerialization) override;
    FormatterMetaPtr GetFormatter(const BaseSerialization& baseSerialization) override;

    Fit::vector<int32_t> GetFormats(const Fit::string &genericId) override;

    void ClearAllFormats() override;
    void AddFormatterEntry(shared_ptr<FormatterEntry> val);
    void RemoveFormatterEntry(const shared_ptr<FormatterEntry>& val);

protected:
    FitCode SerializeArgOut(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Arguments& args, Fit::string& result);

    FitCode DeserializeArgOut(ContextObj ctx, const BaseSerialization& baseSerialization,
        const Fit::string& buffer, Arguments& args);

    FitCode SerializeRequestToJson(ContextObj ctx, const ArgConverterList &converterList,
        const BaseSerialization& baseSerialization,
        const Arguments& args, Fit::string &result);

    FitCode DeserializeJsonToRequest(ContextObj ctx, const ArgConverterList &converterList,
        const BaseSerialization& baseSerialization,
        Arguments& args, const Fit::string& buffer);

private:
    FormatterRepoPtr repo_ {};
    shared_mutex mt_;
    map<int32_t, shared_ptr<FormatterEntry>> formatterEntry_;
};
using FormatterServicePtr = std::shared_ptr<FormatterService>;

FormatterServicePtr CreateFormatterService();
}
}
}
#endif // DEFAULT_FORMATTER_SERVICE_HPP
