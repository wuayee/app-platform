/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : host to net;net to host
 * Author       : songyongtan
 * Create       : 2020/11/13 20:42
 * Notes:       :
 */
#include "fit_meta_header_package_net_util.h"
#include <netinet/in.h>

namespace fit_meta_header_package_net_util {
void host_to_net(fit_meta_header_package &package)
{
    package.version = htons(package.version);
    package.fit_id_length = htons(package.fit_id_length);
}

void net_to_host(fit_meta_header_package &package)
{
    package.version = ntohs(package.version);
    package.fit_id_length = ntohs(package.fit_id_length);
}
}

namespace fit_response_meta_header_package_net_util {
void host_to_net(fit_response_meta_header_package& package)
{
    package.version = htons(package.version);
    package.code = htonl(package.code);
    package.message_size = htonl(package.message_size);
}

void net_to_host(fit_response_meta_header_package& package)
{
    package.version = ntohs(package.version);
    package.code = ntohl(package.code);
    package.message_size = ntohl(package.message_size);
}
}
