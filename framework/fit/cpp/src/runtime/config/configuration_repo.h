/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/19 14:54
 * Notes:       :
 */

#ifndef CONFIGURATION_REPO_H
#define CONFIGURATION_REPO_H

#include <memory>
#include <fit/fit_code.h>
#include <fit/internal/runtime/runtime_element.hpp>
#include "configuration_entities.h"

namespace Fit {
namespace Configuration {
class ConfigurationRepo {
public:
    virtual ~ConfigurationRepo() = default;

    // @return FIT_ERR_NOT_FOUND - not exist
    // @return FIT_ERR_SUCCESS - ok
    virtual FitCode Get(const Fit::string &genericId, GenericableConfiguration &out) = 0;

    virtual GenericConfigPtr Getter(const Fit::string &genericId) = 0;
    // @return FIT_ERR_SUCCESS - ok
    virtual FitCode Set(GenericConfigPtr val) = 0;
};

using ConfigurationRepoPtr = std::shared_ptr<ConfigurationRepo>;
}
}
#endif // CONFIGURATION_REPO_H
