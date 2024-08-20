/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/4/30 17:04
 * Notes        :
 */

#ifndef BROKERSERVER_H
#define BROKERSERVER_H

#include <fit/fit_code.h>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_server_process_v3/1.0.0/cplusplus/processV3.hpp>

namespace Fit {
class BrokerServer {
public:
    Fit::vector<fit::registry::Address> StartServer(Framework::Formatter::FormatterServicePtr formatter,
        Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr);
    FitCode StopServer();

    static BrokerServer &Instance();

    FitCode RequestResponse(ContextObj ctx,
        const fit::hakuna::kernel::broker::shared::MetaData &metadata,
        const Fit::string &data,
        ::fit::hakuna::kernel::broker::shared::FitResponse &rsp);

private:
    int32_t IsAuthorized(const Fit::string& accessToken, const fit::registry::Fitable& fitableIn);
    fit::registry::Fitable GetFitableFromMeta(const fit_meta_data &meta);
    FitCode GetFitableType(
        const fit_meta_data &meta,
        Fit::Framework::Annotation::FitableType &fitableType);

    FitCode RequestResponse(ContextObj ctx,
        const fit_meta_data &meta,
        const Fit::string &data,
        const Fit::Framework::Annotation::FitableType &fitableType,
        ::fit::hakuna::kernel::broker::shared::FitResponse &rsp);
    Framework::Formatter::FormatterServicePtr formatter_ = nullptr;
    Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr_ = nullptr;
};
}
#endif // BROKERSERVER_H
