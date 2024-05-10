/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide register fitable interface.
 * Author       : w00561424
 * Date:        : 2023/09/18
 */
#include <register_fitable_base.h>
#include <fit/external/util/string_utils.hpp>
#include <sstream>
#include <utility>
namespace Fit {
Fit::vector<::fit::hakuna::kernel::registry::shared::Address> RegisterFitableBase::GetLocalAddresses(
    const Fit::vector<fit::registry::Address>& serverAddresses)
{
    ::Fit::map<::Fit::string, ::Fit::vector<::fit::hakuna::kernel::registry::shared::Endpoint>> groupedEndpoints;
    for (const auto& serverAddress : serverAddresses) {
        ::fit::hakuna::kernel::registry::shared::Endpoint endpoint {};
        endpoint.port = serverAddress.port;
        endpoint.protocol = serverAddress.protocol;
        groupedEndpoints[serverAddress.host].push_back(endpoint);
    }
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::Address> addresses;
    addresses.reserve(groupedEndpoints.size());
    for (auto& pair : groupedEndpoints) {
        ::fit::hakuna::kernel::registry::shared::Address address;
        address.host = pair.first;
        address.endpoints = std::move(pair.second);
        addresses.push_back(std::move(address));
    }
    return addresses;
}

Fit::string RegisterFitableBase::ComputeAppVersion(
    const ::fit::hakuna::kernel::registry::shared::Application& application,
    const ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta>& fitables)
{
    uint64_t ret = 0;
    std::stringstream ss;
    for (const auto& it : application.extensions) {
        ss << it.first << ":" << it.second << ":";
    }
    for (auto& fitable : fitables) {
        ss << fitable.fitable->genericableId << ":"
            << fitable.fitable->genericableVersion << ":"
            << fitable.fitable->fitableId << ":"
            << fitable.fitable->fitableVersion << ":";
        for (const auto& format : fitable.formats) {
            ss << format << ":";
        }
        for (const auto& alias : fitable.aliases) {
            ss << alias << ":";
        }
        for (const auto& it : fitable.extensions) {
            ss << it.first << ":"
                << it.second << ":";
        }
        ss << fitable.environment;
        ret ^= StringUtils::ComputeHash(Fit::to_fit_string(ss.str()));
        ss.clear();
    }
    return StringUtils::ToHexString(ret);
}

::Fit::vector<::fit::hakuna::kernel::shared::Fitable> RegisterFitableBase::Convert(
    const Framework::Annotation::FitableDetailPtrList& fitables)
{
    ::Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitableInfos;
    fitableInfos.reserve(fitables.size());
    for (const auto& fitable : fitables) {
        ::fit::hakuna::kernel::shared::Fitable fitableInfo;
        fitableInfo.genericableId = fitable->GetGenericId();
        fitableInfo.genericableVersion = fitable->GetGenericVersion();
        fitableInfo.fitableId = fitable->GetFitableId();
        fitableInfo.fitableVersion = fitable->GetFitableVersion();
        fitableInfos.emplace_back(std::move(fitableInfo));
    }
    return fitableInfos;
}

void RegisterFitableBase::Build(
    const Fit::Framework::Annotation::FitableDetailPtrList &fitables, ContextObj ctx,
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta>& fitableMetas)
{
    auto fitableInfos = Convert(fitables);
    for (const auto& fitable : fitableInfos) {
        ::fit::hakuna::kernel::registry::shared::FitableMeta meta {};
        meta.formats = formatterService_->GetFormats(fitable.genericableId);
        if (meta.formats.empty()) {
            continue;
        }
        meta.fitable = Context::NewObj<::fit::hakuna::kernel::shared::Fitable>(ctx);
        *meta.fitable = fitable;
        meta.environment = commonConfig_->GetWorkerEnvironment();
        meta.application = Context::NewObj<::fit::hakuna::kernel::registry::shared::Application>(ctx);
        auto genericableConfig = configurationService_->GetGenericableConfigPtr(fitable.genericableId);
        if (genericableConfig != nullptr) {
            meta.tags = genericableConfig->GetTags();
            Configuration::FitableConfiguration fitableConfig;
            if (genericableConfig->GetFitable(fitable.fitableId, fitableConfig) == FIT_OK) {
                meta.aliases = std::move(fitableConfig.aliases);
                meta.extensions = std::move(fitableConfig.extensions);
            }
        }
        fitableMetas.emplace_back(move(meta));
    }

    application_.name = commonConfig_->GetAppName();
    auto extensions = commonConfig_->GetAppExtensions();
    application_.extensions = move(extensions);
    application_.nameVersion = ComputeAppVersion(application_, fitableMetas);
    commonConfig_->SetAppVersion(application_.nameVersion);
    FIT_LOG_DEBUG("Appname:appversion:size :(%s:%s:%lu).",
        application_.name.c_str(), application_.nameVersion.c_str(), fitableMetas.size());

    for (size_t i = 0; i < fitableMetas.size(); ++i) {
        *fitableMetas[i].application = application_;
    }
}

Fit::string RegisterFitableBase::ComputeWorkerVersion(const ::fit::hakuna::kernel::registry::shared::Worker& worker,
    const fit::hakuna::kernel::registry::shared::Application& application)
{
    std::stringstream ss;
    // worker.expire暂时不需要
    ss << worker.id;
    ss << worker.environment;
    for (const auto& extension : worker.extensions) {
        ss << extension.first;
        ss << extension.second;
    }

    for (const auto& address : worker.addresses) {
        ss << address.host;
        for (const auto& endpoint : address.endpoints) {
            ss << endpoint.port;
            ss << endpoint.protocol;
        }
    }
    // application 计算
    ss << application.name;
    ss << application.nameVersion;

    uint64_t ret = StringUtils::ComputeHash(Fit::to_fit_string(ss.str()));
    return StringUtils::ToHexString(ret);
}
}