/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/31 19:48
 * Notes        :
 */

#include <genericable/com_huawei_fit_heartbeat_query_heartbeat_address_list/1.0.0/cplusplus/queryHeartbeatAddressList.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_address_utils.h>
#include <core/fit_heartbeat_manager.h>
namespace fit {
namespace heartbeat {
static FitCode QueryHeartbeatAddressListImpl(void *ctx,
    const Fit::string *sceneType,
    Fit::vector<fit::registry::Address> **addresses)
{
    if (sceneType == nullptr) {
        FIT_LOG_ERROR("Param is null.");
        return FIT_ERR_PARAM;
    }
    auto allBeatInfo = fit_heartbeat_manager::instance()->get_heartbeat_service().query_all_beat();
    *addresses = Fit::Context::NewObj<Fit::vector<fit::registry::Address>>(ctx);
    for (const auto &beatInfo : allBeatInfo) {
        if (*sceneType != beatInfo.addressBeatInfo.beat_info.sceneType) {
            continue;
        }
        if (beatInfo.status == Fit::Heartbeat::HeartbeatStatus::ALIVE) {
            fit::registry::Address address;
            address.id = beatInfo.addressBeatInfo.id;
            if (!beatInfo.addressBeatInfo.addresses.empty()) {
                auto &fit_addr = beatInfo.addressBeatInfo.addresses.front();
                address.host = fit_addr.ip;
                address.port = fit_addr.port;
                address.protocol = static_cast<int32_t>(fit_addr.protocol);
                address.formats.insert(address.formats.end(), fit_addr.formats.begin(), fit_addr.formats.end());
                address.environment = fit_addr.environment;
            }
            (*addresses)->push_back(address);
        }
    }

    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(QueryHeartbeatAddressListImpl)
        .SetGenericId(fit::heartbeat::queryHeartbeatAddressList::GENERIC_ID)
        .SetFitableId("query_heartbeat_address_list_cpp");
}
}
}
