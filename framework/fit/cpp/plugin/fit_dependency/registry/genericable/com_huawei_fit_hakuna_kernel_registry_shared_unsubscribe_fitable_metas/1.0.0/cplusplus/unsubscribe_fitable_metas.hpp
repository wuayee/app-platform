/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:47:35
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_UNSUBSCRIBE_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_UNSUBSCRIBE_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __unsubscribeFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<Fit::string> *,
        const Fit::string *,
        const Fit::string *,
        const Fit::string *>;
};

/**
 * 反订阅元数据信息
 *
 * @param genericableIds
 * @param environment
 * @param workerId
 * @param callbackId
 */
class unsubscribeFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(__unsubscribeFitableMetas::InType)> {
public:
    static constexpr const char *GENERIC_ID = "3a6cc3cb30ea45d49f70681d88601463";
    unsubscribeFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(__unsubscribeFitableMetas::InType)>(GENERIC_ID) {}
    explicit unsubscribeFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__unsubscribeFitableMetas::InType)>(GENERIC_ID, ctx) {}
    ~unsubscribeFitableMetas() = default;
};
}
}
}
}
}

#endif
