/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/11
 * Notes:       :
 */

#include "genericable_configuration_json_parser.hpp"

#include <fstream>
#include <rapidjson/document.h>
#include <fit/external/util/string_utils.hpp>

using namespace rapidjson;
using Fit::Configuration::GenericableConfiguration;
using Fit::Configuration::TrustConfiguration;
using Fit::Configuration::TagList;
using Fit::Configuration::AliasList;
using Fit::Configuration::FitableConfiguration;
using Fit::Configuration::GenericConfigPtr;

namespace {
const char *KEY_GENERICABLES = "genericables";
const char *KEY_GENERICABLE_DEFAULT = "default";
const char *KEY_GENERICABLE_ROUTE = "route";
const char *KEY_GENERICABLE_LOADBALANCE = "loadbalance";
const char *KEY_GENERICABLE_TAGS = "tags";
const char *KEY_GENERICABLE_TRUST = "trust";
const char *KEY_TRUST_VALIDATE = "validate";
const char *KEY_TRUST_BEFORE = "before";
const char *KEY_TRUST_AFTER = "after";
const char *KEY_TRUST_ERROR = "error";
const char *KEY_GENERICABLE_FITABLES = "fitables";
const char *KEY_FITABLE_ALIASES = "aliases";
const char *KEY_FITABLE_DEGRADATION = "degradation";
const char *KEY_SERVICE_NAME = "name";
const char *KEY_SOURCE_TYPES = "genericable-config-sources";

Fit::string GetChildStringEle(const Value &ele, const char *key)
{
    auto iter = ele.FindMember(key);
    if (iter == ele.MemberEnd()) {
        return "";
    }
    if (!iter->value.IsString()) {
        return "";
    }

    return iter->value.GetString();
}

/**
 * 根据多级key查询一个字符串值
 * @param ele
 * @param keys 多级key
 * @return 不存在时返回空字符串
 */
Fit::string GetChildStringEle(const Value &ele, const Fit::vector<Fit::string> &keys)
{
    if (keys.empty()) {
        return "";
    }

    const Value *target = &ele;
    for (auto &key : keys) {
        auto iter = target->FindMember(key.c_str());
        if (iter == ele.MemberEnd()) {
            return "";
        }
        target = &iter->value;
    }
    if (!target->IsString()) {
        return "";
    }

    return target->GetString();
}

TrustConfiguration GetChildWithTrust(const Value &ele, const char *key)
{
    auto iter = ele.FindMember(key);
    if (iter == ele.MemberEnd()) {
        return {};
    }

    TrustConfiguration trust {};
    trust.validate = GetChildStringEle(iter->value, KEY_TRUST_VALIDATE);
    trust.before = GetChildStringEle(iter->value, KEY_TRUST_BEFORE);
    trust.after = GetChildStringEle(iter->value, KEY_TRUST_AFTER);
    trust.error = GetChildStringEle(iter->value, KEY_TRUST_ERROR);

    return trust;
}

FitableConfiguration GetFitableConfig(Fit::string id, const Value &ele)
{
    FitableConfiguration fitable {};
    fitable.fitableId = std::move(id);
    fitable.degradation = GetChildStringEle(ele, KEY_FITABLE_DEGRADATION);
    // fitables.xxx.aliases="1,2,3"
    fitable.aliases = Fit::StringUtils::Split(GetChildStringEle(ele, KEY_FITABLE_ALIASES), ',');

    return fitable;
}

Fit::vector<FitableConfiguration> GetFitablesConfig(const Value &ele)
{
    const auto &fitablesIter = ele.FindMember(KEY_GENERICABLE_FITABLES);
    if (fitablesIter == ele.MemberEnd()) {
        return {};
    }

    Fit::vector<FitableConfiguration> result;
    for (auto &fitableEle : fitablesIter->value.GetObject()) {
        result.push_back(GetFitableConfig(fitableEle.name.GetString(), fitableEle.value));
    }

    return result;
}

GenericConfigPtr GetGenericableConfig(const Fit::string &id, const Value &ele)
{
    auto genericable = std::make_shared<GenericableConfiguration>();
    genericable->SetGenericId(id);
    // route.default="123"
    genericable->SetDefaultFitableId(GetChildStringEle(ele, {KEY_GENERICABLE_ROUTE, KEY_GENERICABLE_DEFAULT}));
    genericable->SetLoadbalance(GetChildStringEle(ele, KEY_GENERICABLE_LOADBALANCE));
    genericable->SetTrust(GetChildWithTrust(ele, KEY_GENERICABLE_TRUST));
    // xxx.tags="1,2,3"
    genericable->SetTags(Fit::StringUtils::Split(GetChildStringEle(ele, KEY_GENERICABLE_TAGS), ','));
    genericable->SetGenericName(GetChildStringEle(ele, KEY_SERVICE_NAME));
    genericable->SetConfigSourceTypes(Fit::StringUtils::Split(GetChildStringEle(ele, KEY_SOURCE_TYPES), ','));
    // fitables
    auto fitables = GetFitablesConfig(ele);
    for (auto &fitable : fitables) {
        genericable->SetFitable(fitable);
    }

    return genericable;
}
}
namespace Fit {
namespace Configuration {
FitCode GenericableConfigurationJsonParser::LoadFromFile(const Fit::string &file)
{
    std::ifstream fs;
    fs.open(Fit::to_std_string(file));
    if (!fs.is_open()) {
        FIT_LOG_ERROR("Open file failed, file = %s.", file.c_str());
        return FIT_ERR_NOT_FOUND;
    }

    Fit::string jsonContent;
    fs.seekg(0, std::ios::end);
    int32_t len = fs.tellg();
    fs.seekg(0, std::ios::beg);
    jsonContent.resize(static_cast<uint32_t>(len));
    fs.read(const_cast<char *>(jsonContent.data()), jsonContent.length());
    fs.close();

    Document doc;
    if (doc.Parse(jsonContent.c_str(), jsonContent.length()).HasParseError()) {
        FIT_LOG_ERROR("Parse error = %ld, file = %s.", doc.GetErrorOffset(), file.c_str());
        return FIT_ERR_PARAM;
    }

    auto genericablesEle = doc.FindMember(KEY_GENERICABLES);
    if (genericablesEle != doc.MemberEnd()) {
        for (auto &genericable_ele : genericablesEle->value.GetObject()) {
            callback_(GetGenericableConfig(genericable_ele.name.GetString(),
                genericable_ele.value));
        }
    }

    return FIT_ERR_SUCCESS;
}
}
}