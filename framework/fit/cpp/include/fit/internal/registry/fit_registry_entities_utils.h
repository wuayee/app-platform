/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : fit_registry
 * Author       : s00558940
 * Create       : 2020/10/7 14:35
 */

#ifndef FIT_REGISTRY_ENTITIES_UTILS_H
#define FIT_REGISTRY_ENTITIES_UTILS_H

#include <sstream>
#include "fit/internal/registry/fit_registry_entities.h"
#include "fit/internal/fit_address_utils.h"

namespace fit_registry_entities_utils {
class log_render {
public:
    static Fit::string render(const db_worker_info_t &worker)
    {
        std::ostringstream result;
        result << "address = (" << fit_address_utils::convert_to_string(worker.address) << ")"
               << ", status = " << (worker.is_online ? "online" : "offline")
               << ", start_time = " << Fit::TimeUtil::to_string<Fit::TimeUtil::normal_local_time>(worker.start_time)
               << ", update_time = " << Fit::TimeUtil::to_string<Fit::TimeUtil::normal_local_time>(worker.update_time)
               << ", id = " << worker.id
               << ", token = " << worker.token;

        return Fit::to_fit_string(result.str());
    }

    static Fit::string render(const db_service_info_t &db_service)
    {
        std::ostringstream result;
        result << "fitable = (" << db_service.service.fitable.generic_id
               << ":" << db_service.service.fitable.fitable_id << ")"
               << ", version = (" << db_service.service.fitable.generic_version
               << ":" << db_service.service.fitable.fitable_version << ")"
               << ", service_name = " << db_service.service.service_name;
        for (const auto& address : db_service.service.addresses) {
            result << ", address = (" << fit_address_utils::convert_to_string(address) << ")";
        }
        result << ", status = " << (db_service.is_online ? "online" : "offline")
               << ", start_time = "
               << Fit::TimeUtil::to_string<Fit::TimeUtil::normal_local_time>(db_service.start_time);

        return Fit::to_fit_string(result.str());
    }
};
}

#endif // FIT_REGISTRY_ENTITIES_UTILS_H
