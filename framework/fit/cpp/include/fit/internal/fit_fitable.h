/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-01 11:02:39
 */

#ifndef FIT_FITABLE_H
#define FIT_FITABLE_H

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <fit/memory/fit_base.hpp>
#include <functional>

namespace Fit {
struct fitable_id : public FitBase {
    Fit::string generic_id;
    Fit::string generic_version;
    Fit::string fitable_id;
    Fit::string fitable_version;
};

inline bool operator<(const fitable_id &lhs, const fitable_id &rhs)
{
    if (lhs.generic_id != rhs.generic_id) {
        return lhs.generic_id < rhs.generic_id;
    } else if (lhs.generic_version != rhs.generic_version) {
        return lhs.generic_version < rhs.generic_version;
    } else if (lhs.fitable_id != rhs.fitable_id) {
        return lhs.fitable_id < rhs.fitable_id;
    }
    return lhs.fitable_version < rhs.fitable_version;
}

struct fitable_service : public FitBase {
    fitable_id id;
    Fit::string service_name;
};

using plugin_id = Fit::string;

enum class fit_protocol_type : uint32_t {
    RSOCKET = 0,
    SOCKET = 1,
    HTTP = 2,
    GRPC = 3,
    HTTPS = 4,
    UC = 10,
    SHARE_MEMORY = 11,
    CROS_RPC = 12,
    MAX
};
enum fit_format_type : uint32_t {
    PROTOBUF = 0,
    JSON = 1,
    MAX
};

using fit_format_type_set = vector<fit_format_type>;

struct fit_address : public FitBase {
    fit_address()
    {
        port = 0;
        protocol = fit_protocol_type::MAX;
    }
    Fit::string ip;
    union {
        uint32_t port;
        uint32_t lsid;
    };
    // ͨ��Э������
    fit_protocol_type protocol;
    // ֧�ֵ����ݸ�ʽ
    fit_format_type_set formats;

    Fit::string environment;
    Fit::string id;
    map<string, string> extensions;
};

using fit_address_set = vector<fit_address>;

class fit_address_equal_to {
public:
    bool operator()(const fit_address &a, const fit_address &b) const
    {
        return a.ip == b.ip && a.port == b.port && a.protocol == b.protocol;
    }
};

class fit_address_hash {
public:
    size_t operator()(const fit_address &value) const
    {
        return std::hash<Fit::string>()(value.ip) ^
        std::hash<int>()(static_cast<int>(value.port)) ^
        std::hash<int>()(static_cast<int>(value.protocol));
    }
};

enum fitable_strategy_type : int32_t { defaults = 0, current, degradation, max };

struct fit_loaded_plugin : public FitBase {
    Fit::string id;
    Fit::string version;
    Fit::string location;
    size_t fitable_num;
};

struct fit_registered_fitable : public FitBase {
    fitable_id fitable;
    Fit::string plugin_id;
};


// queryFitServiceAddressList

struct ServiceMeta : public FitBase {
    fitable_id fitable;
    Fit::string serviceName;
    Fit::string pluginName;
};

struct ServiceAddress : public FitBase {
    ServiceMeta serviceMeta;
    fit_address address;
};


using fit_local_invoke =
    std::function<int32_t(const fitable_id &id, const Fit::string &payload_in, Fit::string &payload_out)>;
}  // namespace Fit

#endif
