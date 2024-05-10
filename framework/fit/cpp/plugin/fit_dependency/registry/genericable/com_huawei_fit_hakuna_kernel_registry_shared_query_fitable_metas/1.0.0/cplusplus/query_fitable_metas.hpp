/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:43:06
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_QUERY_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_QUERY_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __queryFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<Fit::string> *,
        const Fit::string *>;
    using OutType =
        ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **>;
};

/**
 * 根据genericableId查询元数据信息
 *
 * @param genericableIds
 * @param environment
 * @return FitableMeta
 */
class queryFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(
    __queryFitableMetas::InType, __queryFitableMetas::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "838c8f3ea8e149b3bab72bbf5c9d8a4d";
    queryFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(
        __queryFitableMetas::InType, __queryFitableMetas::OutType)>(GENERIC_ID) {}
    explicit queryFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(
            __queryFitableMetas::InType, __queryFitableMetas::OutType)>(GENERIC_ID, ctx) {}
    ~queryFitableMetas() = default;
};
}
}
}
}
}

#endif
