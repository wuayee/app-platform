/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/13
 * Notes:       :
 */

#ifndef BM_FORMATTER_ADD_HPP
#define BM_FORMATTER_ADD_HPP

#include "helper.hpp"
#include "../generiacable/add.hpp"

namespace Fit {
namespace Benchmark {
void RegisterAddProtobufFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo)
{
    auto converter = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    converter->SetGenericId(Add::GENERIC_ID);
    converter->SetFormat(0);

    converter->SetArgsInConverter({
        Fit::Framework::Formatter::Protobuf::BuildArgConverter<const int*>(),
        Fit::Framework::Formatter::Protobuf::BuildArgConverter<const int*>()
    });

    converter->SetArgsOutConverter({
        Fit::Framework::Formatter::Protobuf::BuildArgConverter<int**>()
    });

    formatterRepo->Add({converter});
}

void RegisterAddJsonFormatter(const ::Fit::Framework::Formatter::FormatterRepoPtr &formatterRepo)
{
    auto converter = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
    converter->SetGenericId(Add::GENERIC_ID);
    converter->SetFormat(1);

    converter->SetArgsInConverter({
        Fit::Framework::Formatter::Json::BuildArgConverter<const int*>(),
        Fit::Framework::Formatter::Json::BuildArgConverter<const int*>()
    });

    converter->SetArgsOutConverter({
        Fit::Framework::Formatter::Json::BuildArgConverter<int**>()
    });

    formatterRepo->Add({converter});
}
}
}
#endif // FORMATTER_ADD_HPP
