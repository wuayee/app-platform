/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/19 10:34
 * Notes:       :
 */

#ifndef CONFIGURATION_ENTITIES_H
#define CONFIGURATION_ENTITIES_H

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/utility.hpp>
#include <fit/fit_log.h>
#include <algorithm>
#include <fit/fit_code.h>
#include <memory>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>

namespace Fit {
namespace Configuration {
struct TrustConfiguration {
    Fit::string before;
    Fit::string validate;
    Fit::string after;
    Fit::string error;
};

using AliasList = Fit::vector<Fit::string>;
using ApplicationSet = Fit::vector<::fit::hakuna::kernel::registry::shared::Application>;
struct FitableConfiguration {
    AliasList aliases;
    Fit::string degradation;
    Fit::string fitableId;
    Fit::map<Fit::string, Fit::string> extensions;
    // application和formats一一对应
    ApplicationSet applications;
    Fit::vector<Fit::vector<int32_t>> applicationsFormats;
};
using FitableSet = Fit::vector<FitableConfiguration>;
using TagList = Fit::vector<Fit::string>;

// 参数路由
constexpr const char *RULE_TYPE_PARAM = "P";
// 标签路由
constexpr const char *RULE_TYPE_TAG = "T";
using RuleTypeList = Fit::vector<Fit::string>;
struct RuleConfiguration {
    /**
     * 规则路由的规则id
     * 如果为空，则表示不需要进行规则路由
     */
    Fit::string id;
    /**
     * 启用的规则类型，RULE_TYPE_PARAM/RULE_TYPE_TAG
     * 如果类型中包含标签路由则表示，需要去打标中心动态获取标签信息，并传给规则中心
     * 如果类型中包含参数路由则表示，传入到规则中心的参数需要参数信息
     * 如果为空，则表示不需要进行规则路由
     */
    // 规则类型，RULE_TYPE_PARAM/RULE_TYPE_TAG，存在时表示启用了这些规则，不存在时则不用进行规则路由
    RuleTypeList types;
    /**
     * 默认情况下调用使用的fitable id
     */
    Fit::string defaultFitableId;
};

using TaggerIds = Fit::vector<Fit::string>;
/**
 * 参数配置信息
 */
struct ParamConfiguration {
    // 参数名称
    Fit::string name;
    // 参数索引位置，从0开始
    uint8_t index;
    // 参数关联的打标id，用于传给打标中心获取动态标签
    TaggerIds taggerIds;
};
using ParamConfigurationList = Fit::vector<ParamConfiguration>;

using ConfigSourceTypes = Fit::vector<Fit::string>;

class GenericableConfiguration {
public:
    Fit::string GetGenericId() const
    {
        return genericId_;
    }

    void SetGenericId(const Fit::string &val)
    {
        genericId_ = val;
    }

    Fit::string GetGenericName() const
    {
        return genericName_;
    }

    void SetGenericName(const Fit::string &val)
    {
        genericName_ = val;
    }

    TagList GetTags() const
    {
        return tags_;
    }

    void SetTags(const TagList &val)
    {
        tags_ = val;
    }

    bool HasTag(const Fit::string &tag) const
    {
        return exist(tags_.begin(), tags_.end(), [&tag](const Fit::string &v) { return tag == v; });
    }

    const Fit::string &GetDefaultFitableId() const
    {
        return rule_.defaultFitableId;
    }

    void SetDefaultFitableId(const Fit::string &val)
    {
        rule_.defaultFitableId = val;
    }

    Fit::string GetRoute() const
    {
        return route_;
    }

    void SetRoute(const Fit::string &val)
    {
        route_ = val;
    }

    Fit::string GetLoadbalance() const
    {
        return loadbalance_;
    }

    void SetLoadbalance(const Fit::string &val)
    {
        loadbalance_ = val;
    }

    TrustConfiguration GetTrust() const
    {
        return trust_;
    }

    void SetTrust(const TrustConfiguration &val)
    {
        trust_ = val;
    }

    int32_t GetFitable(const Fit::string &fitable_id, FitableConfiguration &out) const
    {
        auto iter = fitables_.find(fitable_id);
        if (iter == fitables_.end()) {
            return FIT_ERR_NOT_FOUND;
        }
        out = iter->second;

        return FIT_ERR_SUCCESS;
    }

    void SetFitable(FitableConfiguration &out)
    {
        fitables_[out.fitableId] = out;
    }

    Fit::string GetFitableIdByAlias(const Fit::string &alias) const
    {
        for (const auto &fitable : fitables_) {
            if (exist(fitable.second.aliases.begin(), fitable.second.aliases.end(),
                [&alias](const Fit::string &v) { return alias == v; })) {
                return fitable.second.fitableId;
            }
        }
        FIT_LOG_ERROR("Not found alias = %s, generic_id = %s.", alias.c_str(), GetGenericId().c_str());

        return "";
    }

    FitableSet GetFitables() const
    {
        FitableSet ret {};
        for (const auto &fitable : fitables_) {
            ret.push_back(fitable.second);
        }
        return ret;
    }

    const RuleConfiguration &GetRule() const
    {
        return rule_;
    };

    void SetRule(RuleConfiguration rule)
    {
        rule_ = std::move(rule);
    };

    const ParamConfigurationList &GetParams() const
    {
        return params_;
    };

    void SetParams(ParamConfigurationList params)
    {
        params_ = std::move(params);
    };

    ConfigSourceTypes GetConfigSourceTypes() const
    {
        return configSourceTypes_;
    }

    void SetConfigSourceTypes(const ConfigSourceTypes& configSourceTypes)
    {
        configSourceTypes_ = configSourceTypes;
    }
private:
    Fit::string genericId_;
    Fit::string genericName_;
    TrustConfiguration trust_;
    TagList tags_;
    Fit::string route_;
    Fit::vector<Fit::string> tagIds_ {};
    Fit::string ruleId_ {};
    Fit::string loadbalance_;
    using FitableConfigurationMap = Fit::map<Fit::string, FitableConfiguration>;
    FitableConfigurationMap fitables_;
    RuleConfiguration rule_ {};
    ParamConfigurationList params_;
    ConfigSourceTypes configSourceTypes_;
};

using GenericConfigPtr = std::shared_ptr<GenericableConfiguration>;
}
}
#endif // CONFIGURATION_ENTITIES_H
