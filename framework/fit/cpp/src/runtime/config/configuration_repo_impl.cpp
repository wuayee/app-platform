/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/19 15:34
 * Notes:       :
 */

#include "configuration_repo_impl.h"

#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include "genericable_configuration_json_parser.hpp"

using Fit::Configuration::GenericableConfiguration;
using Fit::Configuration::GenericConfigPtr;
using Fit::Config::SystemConfig;

namespace Fit {
namespace Configuration {
FitCode ConfigurationRepoImpl::Get(const Fit::string &genericId, GenericableConfiguration &out)
{
    auto iter = genericables_.find(genericId);
    if (iter == genericables_.end()) {
        return FIT_ERR_NOT_FOUND;
    }
    out = *iter->second;

    return FIT_ERR_SUCCESS;
}

GenericConfigPtr ConfigurationRepoImpl::Getter(const Fit::string &genericId)
{
    auto iter = genericables_.find(genericId);
    if (iter != genericables_.end()) {
        return iter->second;
    }
    return nullptr;
}

FitCode ConfigurationRepoImpl::Set(GenericConfigPtr val)
{
    genericables_[val->GetGenericId()] = std::move(val);

    return FIT_ERR_SUCCESS;
}

ConfigurationRepoImpl::ConfigurationRepoImpl() = default;
}
}