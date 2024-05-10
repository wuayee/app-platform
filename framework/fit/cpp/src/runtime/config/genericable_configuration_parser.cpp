/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/3/10
 * Notes:       :
 */

#include "genericable_configuration_parser.hpp"

namespace {
constexpr char ARRAY_SPLIT_DELI = ',';
constexpr char FITABLE_ID_CONFIG_ENCODE_CHAR = '-';
constexpr char FITABLE_ID_CONFIG_DECODE_CHAR = '.';
}

namespace Fit {
namespace Configuration {
Fit::string TagsRoutine::Key()
{
    return "tags";
}

RoutineFunc TagsRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        config->SetTags(StringUtils::Split(value, ARRAY_SPLIT_DELI));
    };
}

Fit::string LoadbalanceRoutine::Key()
{
    return "loadbalance";
}

RoutineFunc LoadbalanceRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        config->SetLoadbalance(value);
    };
}

Fit::string TrustRoutine::Key()
{
    return "trust";
}

RoutineFunc TrustRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        constexpr uint32_t expectedKeyLayerNum = 1;
        if (keys.size() != expectedKeyLayerNum) {
            return;
        }

        const Fit::string &type = *keys.begin();
        auto trust_config = config->GetTrust();
        if (type == "validate") {
            trust_config.validate = value;
        } else if (type == "before") {
            trust_config.before = value;
        } else if (type == "after") {
            trust_config.after = value;
        } else if (type == "error") {
            trust_config.error = value;
        }
        config->SetTrust(trust_config);
    };
}

Fit::string ParamsRoutine::Key()
{
    return "params";
}

RoutineFunc ParamsRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        constexpr uint32_t expectedKeyLayerNum = 1;
        if (keys.size() <= expectedKeyLayerNum) {
            return;
        }

        const Fit::string &name = *keys.begin();
        ParamConfiguration tmp {name};
        ParamConfiguration *targetParamConfig {&tmp};
        auto params = config->GetParams();
        auto iter = std::find_if(params.begin(), params.end(), [&name](const ParamConfiguration &param) {
            return param.name == name;
        });
        if (iter != params.end()) {
            targetParamConfig = &(*iter);
        }

        AttributeRoutine(*targetParamConfig, range_skip<Fit::vector<Fit::string>>(keys, 1), value);

        if (targetParamConfig == &tmp) {
            params.push_back(*targetParamConfig);
        }

        config->SetParams(params);
    };
}

void ParamsRoutine::AttributeRoutine(ParamConfiguration &config,
    const StringArrRangeSkip &keys, const Fit::string &value)
{
    const Fit::string &attrKey = *keys.begin();
    if (attrKey == "taggers") {
        TaggersRoutine(config, range_skip<Fit::vector<Fit::string>>(keys, 1), value);
    } else if (attrKey == "index") {
        IndexRoutine(config, range_skip<Fit::vector<Fit::string>>(keys, 1), value);
    }
}

void ParamsRoutine::TaggersRoutine(ParamConfiguration &config,
    const StringArrRangeSkip &keys, const Fit::string &value)
{
    uint32_t expectedKeySize = 2;
    if (keys.size() < expectedKeySize) {
        return;
    }

    auto &name = *(++keys.begin());
    if (name == "id") {
        for (auto &item : config.taggerIds) {
            if (item == value) {
                return;
            }
        }
        config.taggerIds.push_back(value);
    }
}

void ParamsRoutine::IndexRoutine(ParamConfiguration &config,
    const StringArrRangeSkip &keys, const Fit::string &value)
{
    config.index = std::atoi(value.c_str());
}

Fit::string RouterRoutine::Key()
{
    return "route";
}

RoutineFunc RouterRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        uint32_t expectedKeySize = 1;
        if (keys.size() < expectedKeySize) {
            return;
        }

        auto &name = *(keys.begin());
        if (name == "default") {
            config->SetDefaultFitableId(value);
        } else if (name == "rule") {
            auto rule = config->GetRule();
            RuleRoutine(rule, range_skip<Fit::vector<Fit::string>>(keys, 1), value);
            config->SetRule(rule);
        }
    };
}

void RouterRoutine::RuleRoutine(RuleConfiguration &config, const StringArrRangeSkip &keys,
    const Fit::string &value)
{
    uint32_t expectedKeySize = 1;
    if (keys.size() != expectedKeySize) {
        return;
    }

    auto &name = *(keys.begin());
    if (name == "id") {
        config.id = value;
    } else if (name == "type") {
        config.types = StringUtils::Split(value, ARRAY_SPLIT_DELI);
    }
}

Fit::string FitablesRoutine::Key()
{
    return "fitables";
}

RoutineFunc FitablesRoutine::GetRoutine()
{
    return [](const GenericConfigPtr &config, const StringArrRangeSkip &keys,
        const Fit::string &value) {
        constexpr uint32_t expectedKeyLayerNum = 1;
        if (keys.size() < expectedKeyLayerNum) {
            return;
        }

        // fitable_id need replace '-' to '.' its a limit from config server
        Fit::string fitableId = *keys.begin();
        std::replace(fitableId.begin(), fitableId.end(), FITABLE_ID_CONFIG_ENCODE_CHAR, FITABLE_ID_CONFIG_DECODE_CHAR);
        const Fit::string &key = *(++keys.begin());

        FitableConfiguration fitableConfig;
        config->GetFitable(fitableId, fitableConfig);
        fitableConfig.fitableId = std::move(fitableId);
        if (keys.size() > 1) {
            FitableRoutine(fitableConfig, range_skip<Fit::vector<Fit::string>>(keys, 1), value);
        }
        config->SetFitable(fitableConfig);
    };
}

void FitablesRoutine::FitableRoutine(FitableConfiguration &config, const StringArrRangeSkip &keys,
    const Fit::string &value)
{
    uint32_t expectedKeySize = 1;
    if (keys.size() != expectedKeySize) {
        return;
    }

    auto &name = *(keys.begin());
    if (name == "degradation") {
        config.degradation = value;
    } else if (name == "aliases") {
        config.aliases = StringUtils::Split(value, ARRAY_SPLIT_DELI);
    }
}
}
}