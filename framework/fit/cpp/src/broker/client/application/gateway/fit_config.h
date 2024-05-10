/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef IFIT_CONFIG_H
#define IFIT_CONFIG_H

#include "fit/stl/string.hpp"
#include "fit/stl/vector.hpp"

#include <memory>

namespace Fit {
class IFitConfig {
public:
    virtual ~IFitConfig() {}
    virtual bool EnableTrust() const = 0;
    virtual bool LocalOnly() const = 0;
    virtual bool TraceIgnore() const = 0;
    virtual Fit::string GetGenericId() const = 0;
    virtual Fit::string GetRoutine() const = 0;
    virtual Fit::string GetDefault() const = 0;
    virtual Fit::string GetDegradation(const Fit::string &id) const = 0;
    virtual Fit::string GetValidate() const = 0;
    virtual Fit::string GetBefore() const = 0;
    virtual Fit::string GetAfter() const = 0;
    virtual Fit::string GetError() const = 0;
    virtual bool IsRegistryFitable() const = 0;
    virtual Fit::string GetRuleId() const = 0;
    virtual Fit::string GetFitableIdByAlias(const Fit::string &alias) const = 0;
    virtual Fit::vector<Fit::string> GetParamTagByIdx(int32_t idx) const = 0;
    virtual Fit::string GetRandomFitable() const = 0;
};

using FitConfigPtr = std::shared_ptr<IFitConfig>;
}
#endif