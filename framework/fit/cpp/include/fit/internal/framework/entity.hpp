/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/14
 * Notes:       :
 */

#ifndef ENTITY_HPP
#define ENTITY_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>

namespace Fit {
namespace Framework {
struct Fitable {
    Fit::string genericId;
    Fit::string fitableId;
    Fit::string fitableVersion;
    Fit::string genericVersion;
};

struct Application {
    string name;
    string version;
    map<string, string> extensions;
};

struct Worker {
    string id;
    map<string, string> extensions;
};

struct Address {
    Fit::string host;
    int32_t port;
    Fit::string workerId;
    /**
     * rsocket------0
     * socket-------1
     * http---------2
     * grpc---------3
     * https--------4
     * uc-----------10
     * shareMemory--11
     */
    int32_t protocol;
    /**
     * protobuf-----0
     * jackson------1
     */
    Fit::vector<int32_t> formats;
    Fit::string environment;
    map<string, string> extensions;
};

struct ServiceMeta {
    Fitable fitable;
    Application application;
};

struct ServiceAddress {
    ServiceMeta serviceMeta;
    Address address;
};

inline bool operator==(const Fitable &l, const Fitable &r)
{
    return l.genericId == r.genericId &&
        l.genericVersion == r.genericVersion &&
        l.fitableId == r.fitableId &&
        l.fitableVersion == r.fitableVersion;
}
}
}
#endif // ENTITY_HPP
