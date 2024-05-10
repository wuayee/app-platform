/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:39:48
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_REGISTER_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_REGISTER_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __registerFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> *>;
};

/**
 * 注册fitable元数据
 * @param fitableMetas
 */
class registerFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(__registerFitableMetas::InType)> {
public:
    static constexpr const char *GENERIC_ID = "9a52297ae247479b9554308960c82395";
    registerFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(__registerFitableMetas::InType)>(GENERIC_ID) {}
    explicit registerFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__registerFitableMetas::InType)>(GENERIC_ID, ctx) {}
    ~registerFitableMetas() = default;
};
}
}
}
}
}

#endif
