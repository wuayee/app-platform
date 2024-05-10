/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:49:53
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_NOTIFY_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_NOTIFY_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __notifyFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> *>;
};

/**
 * 通知元数据信息变更
 * @param fitableMetas
 */
class notifyFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(__notifyFitableMetas::InType)> {
public:
    static constexpr const char *GENERIC_ID = "6b7f5cad6044488fb4426d5b1998d99d";
    notifyFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(__notifyFitableMetas::InType)>(GENERIC_ID) {}
    explicit notifyFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__notifyFitableMetas::InType)>(GENERIC_ID, ctx) {}
    ~notifyFitableMetas() = default;
};
}
}
}
}
}

#endif