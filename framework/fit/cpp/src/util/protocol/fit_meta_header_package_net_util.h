/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : host to net;net to host
 * Author       : songyongtan
 * Create       : 2020/11/13 20:30
 * Notes:       :
 */

#ifndef FIT_META_HEADER_PACKAGE_NET_UTIL_H
#define FIT_META_HEADER_PACKAGE_NET_UTIL_H

#include "fit_meta_package.h"
#include "fit_response_meta_package.h"

namespace fit_meta_header_package_net_util {
void host_to_net(fit_meta_header_package &package);

void net_to_host(fit_meta_header_package &package);
}
namespace fit_response_meta_header_package_net_util {
void host_to_net(fit_response_meta_header_package &package);

void net_to_host(fit_response_meta_header_package &package);
}
#endif // FIT_META_HEADER_PACKAGE_NET_UTIL_H
