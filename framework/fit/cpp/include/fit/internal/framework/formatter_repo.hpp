/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#ifndef FIT_FORMATTER_REPO_HPP
#define FIT_FORMATTER_REPO_HPP

#include <fit/fit_code.h>
#include <fit/stl/memory.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {

struct BaseSerialization {
    Fit::string genericId;
    Fit::vector<int> formats;
    Fit::Framework::Annotation::FitableType fitableType;
};

class FormatterRepo : public RuntimeElementBase {
public:
    FormatterRepo() : RuntimeElementBase("formatterRepo") {};
    ~FormatterRepo() override = default;

    virtual FormatterMetaPtr Get(const BaseSerialization& baseSerialization) = 0;
    virtual FitCode Add(FormatterMetaPtrList formatterMetas) = 0;
    virtual FitCode Remove(FormatterMetaPtrList formatterMetas) = 0;

    virtual Fit::vector<int32_t> GetFormats(const Fit::string &genericId) = 0;
    virtual void Clear() = 0;
};
using FormatterRepoPtr = std::shared_ptr<FormatterRepo>;
unique_ptr<FormatterRepo> CreateFormatterRepo();
}
}
}
#endif // FIT_FORMATTER_REPO_HPP
