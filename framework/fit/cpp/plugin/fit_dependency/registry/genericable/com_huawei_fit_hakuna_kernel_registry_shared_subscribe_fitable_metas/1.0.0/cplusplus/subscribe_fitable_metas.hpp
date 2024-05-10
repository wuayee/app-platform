/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:44:47
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_SUBSCRIBE_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_SUBSCRIBE_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __subscribeFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<Fit::string> *,
        const Fit::string *,
        const Fit::string *,
        const Fit::string *>;
    using OutType =
        ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **>;
};

/**
 * 根据genericableId订阅元数据
 *
 * @param genericableIds
 * @param environment
 * @param workerId
 * @param callbackId
 * @return FitableMeta
 */
class subscribeFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(
    __subscribeFitableMetas::InType, __subscribeFitableMetas::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "302b69af338c4fb585c1fac4ec6f1adc";
    subscribeFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(
        __subscribeFitableMetas::InType, __subscribeFitableMetas::OutType)>(GENERIC_ID) {}
    explicit subscribeFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(
            __subscribeFitableMetas::InType, __subscribeFitableMetas::OutType)>(GENERIC_ID, ctx) {}
    ~subscribeFitableMetas() = default;
};
}
}
}
}
}

#endif
