/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/4/29 11:35
 * Notes        :
 */

#include <genericable/com_huawei_fit_hakuna_kernel_broker_server_process_v3/1.0.0/cplusplus/processV3.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_log.h>
#include <fit/external/util/registration.hpp>

#include "fit/internal/broker/broker_server.h"

namespace Fit {
namespace Core {
namespace BrokerServer {
FitCode BrokerServerProcess(ContextObj ctx, const fit::hakuna::kernel::broker::shared::MetaData *metadata,
    const Fit::bytes *data, ::fit::hakuna::kernel::broker::shared::FitResponse **rsp)
{
    *rsp = Fit::Context::NewObj<::fit::hakuna::kernel::broker::shared::FitResponse>(ctx);
    if (*rsp == nullptr) {
        FIT_LOG_ERROR("Create context object failed.");
        return FIT_ERR_FAIL;
    }

    return Fit::BrokerServer::Instance().RequestResponse(ctx, *metadata, *data, **rsp);
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(BrokerServerProcess)
        .SetGenericId(::fit::hakuna::kernel::broker::server::processV3::GENERIC_ID)
        .SetFitableId("CppDefaultBrokerServerProcess");
}
}
}
} // LCOV_EXCL_LINE