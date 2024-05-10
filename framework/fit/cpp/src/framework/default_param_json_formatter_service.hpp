/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/24 14:30
 */

#ifndef DEFAULT_PARAM_JSON_FORMATTER_SERVICE
#define DEFAULT_PARAM_JSON_FORMATTER_SERVICE

#include <fit/internal/framework/param_json_formatter_service.hpp>
#include "fit/internal/framework/formatter_repo.hpp"

namespace Fit {
namespace Framework {
namespace ParamJsonFormatter {
class DefaultParamJsonFormatterService : public ParamJsonFormatterService {
public:
    DefaultParamJsonFormatterService();
    explicit DefaultParamJsonFormatterService(Formatter::FormatterRepoPtr repo);
    ~DefaultParamJsonFormatterService() override = default;

    bool Start() override;
    bool Stop() override;

    FitCode SerializeParamToJson(ContextObj ctx,
        const Fit::string& genericID,
        const Arguments& args,
        Fit::string& result) override;

    FitCode SerializeIndexParamToJson(ContextObj ctx,
        const Fit::string& genericID,
        int32_t idx,
        const Argument& arg,
        Fit::string& result) override;
private:
    FitCode GetConvertList(const Fit::string& genericID,
        Formatter::ArgConverterList &converterList);
    Formatter::FormatterRepoPtr repo_ {nullptr};
};
}
}
}

#endif // DEFAULT_PARAM_JSON_FORMATTER_SERVICE