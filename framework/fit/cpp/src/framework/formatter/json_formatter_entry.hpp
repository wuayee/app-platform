/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-12-25
 * Notes:       :
 */

#ifndef FIT_JSON_FORMATTER_ENTRY_HPP
#define FIT_JSON_FORMATTER_ENTRY_HPP

#include <fit/internal/framework/formatter_service.hpp>

namespace Fit {
using namespace Framework;
using namespace Framework::Formatter;
class JsonFormatterEntry : public FormatterEntry {
public:
    int32_t GetFormateType() const override;
    FitCode SerializeRequest(ContextObj ctx, const ArgConverterList& converters, const BaseSerialization& target,
        const Arguments& args, string& result) override;
    FitCode DeserializeRequest(ContextObj ctx, const ArgConverterList& converters, const BaseSerialization& target,
        const string& buffer, Arguments& result) override;
};
}

#endif