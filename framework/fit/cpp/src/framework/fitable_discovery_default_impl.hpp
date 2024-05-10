/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/17
 * Notes:       :
 */

#ifndef FITABLE_DISCOVERY_DEFAULT_IMPL_HPP
#define FITABLE_DISCOVERY_DEFAULT_IMPL_HPP

#include <fit/stl/map.hpp>
#include <fit/stl/set.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>

namespace Fit {
namespace Framework {
class FitableDiscoveryDefaultImpl : public FitableDiscovery {
public:
    FitableDiscoveryDefaultImpl();
    ~FitableDiscoveryDefaultImpl() override = default;

    bool Start() override;
    bool Stop() override;

    void RegisterLocalFitable(Annotation::FitableDetailPtrList details) override;

    void UnRegisterLocalFitable(const Annotation::FitableDetailPtrList &details) override;

    Annotation::FitableDetailPtrList GetLocalFitable(const Framework::Fitable &fitable) override;
    Annotation::FitableDetailPtrList GetLocalFitableByGenericId(const Fit::string &genericId) override;
    Annotation::FitableDetailPtrList GetAllLocalFitables() override;

    Annotation::FitableDetailPtrList DisableFitables(
        const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) override;
    Annotation::FitableDetailPtrList EnableFitables(
        const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) override;

    void ClearAllLocalFitables() override;

private:
    Fit::shared_mutex sharedMtx_ {};
    using GenericId = Fit::string;
    Fit::map<GenericId, Annotation::FitableDetailPtrList> localFitables_ {};

    using FitableId = Fit::string;
    Fit::set<Fit::pair<GenericId, FitableId>> disabledFitables_ {};
    Annotation::FitableDetailReceiver receiver_ {};
    Annotation::FitableDetailReceiver* oldReceiver_ {};
};
}
}
#endif // FITABLE_DISCOVERY_DEFAULT_IMPL_HPP
