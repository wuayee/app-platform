/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data package builder
 * Author       : songyongtan
 * Create       : 2020/11/13 17:12
 * Notes:       :
 */

#include <fit/internal/util/protocol/fit_meta_package_builder.h>
#include <fit/fit_log.h>
#include "fit_generic_id_util.h"
#include "fit_meta_header_package_net_util.h"

Fit::string fit_meta_package_builder::build(const fit_meta_data &meta)
{
    fit_meta_header_package meta_header {};
    meta_header.version = meta.get_version();
    meta_header.payload_format = meta.get_payload_format();
    meta_header.generic_version[MAJOR_INDEX] = meta.get_generic_version().get_major();
    meta_header.generic_version[MINOR_INDEX] = meta.get_generic_version().get_minor();
    meta_header.generic_version[REVISION_INDEX] = meta.get_generic_version().get_revision();
    if (!fit_generic_id_util::str_to_hex(meta.get_generic_id(), meta_header.generic_id,
        sizeof(meta_header.generic_id))) {
        FIT_LOG_ERROR("Fail convert to hex, some char is not hex, generic id (%s)", meta.get_generic_id().c_str());
        return "";
    }
    meta_header.fit_id_length = meta.get_fit_id().size();

    Fit::string result;
    result.reserve(sizeof(fit_meta_header_package) +
        meta_header.fit_id_length +
        meta.GetTagValueLen());

    fit_meta_header_package_net_util::host_to_net(meta_header);
    result.assign((const char *)&meta_header, sizeof(fit_meta_header_package));
    result.append(meta.get_fit_id());

    meta.Serialize(result);
    return result;
}
