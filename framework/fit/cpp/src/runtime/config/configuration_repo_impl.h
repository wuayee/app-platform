/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/19 15:34
 * Notes:       :
 */

#ifndef CONFIGURATION_REPO_IMPL_H
#define CONFIGURATION_REPO_IMPL_H

#include "configuration_repo.h"

namespace Fit {
namespace Configuration {
using GenericableConfigurationMap = map<Fit::string, GenericConfigPtr>;
class ConfigurationRepoImpl : public ConfigurationRepo {
public:
    ConfigurationRepoImpl();
    ~ConfigurationRepoImpl() override = default;
    FitCode Get(const Fit::string &genericId, GenericableConfiguration &out) override;
    GenericConfigPtr Getter(const Fit::string &genericId) override;
    FitCode Set(GenericConfigPtr val) override;
private:
    GenericableConfigurationMap genericables_ {};
};
}
}

#endif // CONFIGURATION_REPO_IMPL_H
