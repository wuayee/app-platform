/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/11
 * Notes:       :
 */

#ifndef GENERICABLE_CONFIGURATION_JSON_PARSER_HPP
#define GENERICABLE_CONFIGURATION_JSON_PARSER_HPP

#include <functional>

#include "configuration_entities.h"

namespace Fit {
namespace Configuration {
class GenericableConfigurationJsonParser {
public:
    using ResultCallbackFunc = std::function<void(GenericConfigPtr config)>;

    explicit GenericableConfigurationJsonParser(ResultCallbackFunc callback)
        : callback_(std::move(callback)) {}

    ~GenericableConfigurationJsonParser() = default;

    FitCode LoadFromFile(const Fit::string &file);

private:
    ResultCallbackFunc callback_;
};
}
}
#endif // GENERICABLE_CONFIGURATION_JSON_PARSER_HPP
