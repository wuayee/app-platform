/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/13
 * Notes:       :
 */

#ifndef BM_FORMATTER_HELPER_HPP
#define BM_FORMATTER_HELPER_HPP

#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/framework/formatter/protobuf_converter.hpp>
#include <fit/external/framework/formatter/json_converter.hpp>
#include "add.hpp"

namespace Fit {
namespace Benchmark {
void RegisterRedundantProtobufFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo,
    const char *genericId)
{
    auto converter = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    converter->SetGenericId(genericId);
    converter->SetFormat(0);

    formatterRepo->Add({converter});
}

void RegisterRedundantJsonFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo,
    const char *genericId)
{
    auto converter = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    converter->SetGenericId(genericId);
    converter->SetFormat(1);

    formatterRepo->Add({converter});
}

void PrepareRedundantProtobufFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo,
    int32_t redundantFormatterCount)
{
    for (int32_t i = 0; i < redundantFormatterCount; ++i) {
        Fit::string genericId = std::to_string(i) + "ddeb28fc438e4acdbdadeaf880d0b5af";

        RegisterRedundantProtobufFormatter(formatterRepo, genericId.c_str());
    }
}

void PrepareRedundantJsonFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo,
    int32_t redundantFormatterCount)
{
    for (int32_t i = 0; i < redundantFormatterCount; ++i) {
        Fit::string genericId = std::to_string(i) + "ddeb28fc438e4acdbdadeaf880d0b5af";

        RegisterRedundantJsonFormatter(formatterRepo, genericId.c_str());
    }
}
}
}
#endif // BM_HELPER_HPP
