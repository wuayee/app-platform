/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/14
 * Notes:       :
 */

#ifndef FITABLE_DISCOVERY_HPP
#define FITABLE_DISCOVERY_HPP

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/internal/framework/entity.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

namespace Fit {
namespace Framework {
class __attribute__ ((visibility ("default"))) FitableDiscovery : public RuntimeElementBase {
public:
    FitableDiscovery() : RuntimeElementBase("fitableDiscovery") {};
    ~FitableDiscovery() override = default;

    virtual void RegisterLocalFitable(Annotation::FitableDetailPtrList details) = 0;
    virtual void UnRegisterLocalFitable(const Annotation::FitableDetailPtrList &details) = 0;
    virtual Annotation::FitableDetailPtrList GetLocalFitable(const Framework::Fitable &id) = 0;
    virtual Annotation::FitableDetailPtrList GetLocalFitableByGenericId(const Fit::string &genericId) = 0;
    virtual Annotation::FitableDetailPtrList GetAllLocalFitables() = 0;

    virtual Annotation::FitableDetailPtrList DisableFitables(
        const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) = 0;
    virtual Annotation::FitableDetailPtrList EnableFitables(
        const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds) = 0;

    virtual void ClearAllLocalFitables() = 0;
};
using FitableDiscoveryPtr = std::shared_ptr<FitableDiscovery>;
unique_ptr<FitableDiscovery> __attribute__ ((visibility ("default"))) CreateFitableDiscovery();
}
}

#endif // FITABLE_DISCOVERY_HPP
