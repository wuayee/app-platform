/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : registry client element
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#ifndef FIT_REGISTRY_CLIENT_ELEMENT_HPP
#define FIT_REGISTRY_CLIENT_ELEMENT_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <common_config.h>
#include <register_fitable_base.h>

namespace Fit {
constexpr const int32_t DEFAULT_TIMES = 0;
class RegistryClientElement : public RuntimeElementBase {
public:
    RegistryClientElement();
    ~RegistryClientElement() override;

    bool Start() override;
    bool Stop() override;

    void EnableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds);
    void DisableFitables(const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds);
private:
    bool HasRegistryClient();
    FitCode RegisterFitableInner(const Framework::Annotation::FitableDetailPtrList &fitables);
    FitCode RegisterFitables(const Framework::Annotation::FitableDetailPtrList &fitables);
    FitCode UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables);

    int32_t StartRegistrarTask();
    void RegistrarTick();

    FitCode PublishIfHasRegistry();
    bool HasRegistry();
    FitCode ClearRegistryAddress();
    FitCode SetRegistryAddress();
private:
    // outside
    Framework::FitableDiscoveryPtr fitableDiscovery_ {};
    std::shared_ptr<CommonConfig> commonConfig_ {nullptr};
    Fit::Framework::Formatter::FormatterServicePtr formatterService_ {};
    std::shared_ptr<RegisterFitableBase> registerFitable_ {nullptr};
    Fit::vector<fit::registry::Address> serverAddresses_;
    // inside
    Fit::vector<RenewInfo> renewInfoSet_;
    RenewInfo currentRenewInfo_;
    int32_t times_ {DEFAULT_TIMES};
    uint timerHandle_ {0};
    std::shared_ptr<Fit::Thread::thread_pool> threadPool_ {};
    std::unique_ptr<Fit::timer> registerTimer_ {};
    bool threadExitFlag_ {false};
};
}

#endif // FIT_REGISTRY_CLIENT_ELEMENT_HPP
