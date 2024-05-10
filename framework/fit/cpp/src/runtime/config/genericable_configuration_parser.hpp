/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/24
 * Notes:       :
 */

#ifndef GENERICABLE_CONFIGURATION_PARSER_HPP
#define GENERICABLE_CONFIGURATION_PARSER_HPP

#include <functional>
#include <fit/external/util/string_utils.hpp>
#include <fit/internal/fit_range_utils.h>
#include "configuration_entities.h"

namespace Fit {
namespace Configuration {
constexpr char KEY_SPLIT_DELI = '.';
using StringArrRangeSkip = range_skip<Fit::vector<Fit::string>>;
using RoutineFunc = std::function<void(const GenericConfigPtr &,
    const StringArrRangeSkip &, const Fit::string &)>;

/**
 * 需要处理的格式
 * tags=1,2,3
 */
class TagsRoutine {
public:
    static Fit::string Key();

    static RoutineFunc GetRoutine();
};

class LoadbalanceRoutine {
public:

    static Fit::string Key();

    static RoutineFunc GetRoutine();
};

class TrustRoutine {
public:
    static Fit::string Key();

    static RoutineFunc GetRoutine();
};

/**
 * 需要处理的格式
 * params.{paramName}.taggers.a2100ca0652646208f2e2f98911ac781.id=a2100ca0652646208f2e2f98911ac781
 * params.{paramName}.index=0
 */
class ParamsRoutine {
public:
    static Fit::string Key();

    static RoutineFunc GetRoutine();

    static void AttributeRoutine(ParamConfiguration &config, const StringArrRangeSkip &keys,
        const Fit::string &value);

    static void TaggersRoutine(ParamConfiguration &config, const StringArrRangeSkip &keys,
        const Fit::string &value);

    static void IndexRoutine(ParamConfiguration &config, const StringArrRangeSkip &keys,
        const Fit::string &value);
};

/**
 * 需要处理的格式
 * route.default=XXX
 * route.rule.id=XXX
 * route.rule.type = P,T
 */
class RouterRoutine {
public:
    static Fit::string Key();

    static RoutineFunc GetRoutine();

    static void RuleRoutine(RuleConfiguration &config, const StringArrRangeSkip &keys,
        const Fit::string &value);
};

/**
 * 需要处理的格式
 * fitables.{fitableId}.degradation=XXX
 * fitables.{fitableId}.aliases=XXX,XXX
 */
class FitablesRoutine {
public:
    static Fit::string Key();

    static RoutineFunc GetRoutine();

    static void FitableRoutine(FitableConfiguration &config, const StringArrRangeSkip &keys,
        const Fit::string &value);
};
}
}
#endif // GENERICABLE_CONFIGURATION_PARSER_HPP
