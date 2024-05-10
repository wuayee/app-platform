/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement for disables fitables
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#include "disable_fitables_element.hpp"

#include <genericable/com_huawei_fit_hakuna_system_worker_get_disabled_fitables/1.0.0/cplusplus/getDisabledFitables.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/broker/broker_client.h>

using namespace Fit;
using ::fit::hakuna::system::worker::GenericableInfo;
using ::fit::hakuna::system::worker::getDisabledFitables;
using Fit::Framework::Annotation::FitableType;
using Framework::FitableDiscovery;

DisableFitablesElement::DisableFitablesElement() : RuntimeElementBase("disableFitables") {}
DisableFitablesElement::~DisableFitablesElement() = default;

bool DisableFitablesElement::Start()
{
    Fit::vector<GenericableInfo> disabledFitables;
    for (const auto& item : GetRuntime().GetElementIs<FitableDiscovery>()->GetLocalFitableByGenericId(
        getDisabledFitables::GENERIC_ID)) {
        fit::registry::Fitable fitable;
        fitable.genericId = item->GetGenericId();
        fitable.genericVersion = "";
        fitable.fitId = item->GetFitableId();
        fitable.fitVersion = item->GetFitableVersion();
        Fit::Framework::Arguments in;
        Fit::vector<::fit::hakuna::system::worker::GenericableInfo> *result = nullptr;
        Fit::Framework::Arguments out = Framework::PackArgs(&result);
        auto *ctx = NewContextDefault();
        if (GetRuntime().GetElementIs<IBrokerClient>()->LocalInvoke(ctx, fitable, in, out, FitableType::MAIN) ==
            FIT_OK) {
            if (result != nullptr) {
                disabledFitables.insert(disabledFitables.end(), result->begin(), result->end());
            }
        }
        ContextDestroy(ctx);
    }

    // 标记失活服务
    for (const auto &disabledFitable : disabledFitables) {
        Fit::vector<Fit::string> fitIds;
        for (auto &fitableInfo : disabledFitable.fitableInfos) {
            fitIds.emplace_back(fitableInfo.id);
        }
        GetRuntime().GetElementIs<FitableDiscovery>()->DisableFitables(disabledFitable.id, fitIds);
    }
    return true;
}

bool DisableFitablesElement::Stop()
{
    return true;
}