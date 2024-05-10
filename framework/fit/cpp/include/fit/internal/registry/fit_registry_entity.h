/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/10/8 19:23
 * Notes:       :
 */

#ifndef FIT_REGISTRY_ENTITY_H
#define FIT_REGISTRY_ENTITY_H

#include <fit/stl/vector.hpp>
#include <fit/stl/unordered_set.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/memory/fit_base.hpp>
#include "fit/internal/fit_fitable.h"

const int32_t REGISTRY_ERROR = -1;
const int32_t REGISTRY_SUCCESS = 0;
const int32_t REGISTRY_NO_NEED_UPDATE = 1;
const int32_t REGISTRY_NOT_EXIST = 2;
const int32_t REGISTRY_EXIST = 3;
const int32_t DEFAULT_TIMEOUT_TIME_SECOND = 90; // 默认超时时间为s
const uint64_t DEFAULT_SYNC_COUNT = 0; // 从db向memory同步次数【初始值】
constexpr int32_t DEFAULT_WORKER_FAILED_SLEEP_SECOND = 1; // 向db写失败后，sleep时间
constexpr int32_t RETRY_OPERATE_DB_TIMES = 2; // 向db写失败后，重试次数
constexpr const size_t MILLION_SECONDS_PER_SECOND  = 1000; // 1s等于1000ms
constexpr const size_t MAX_WORKER_ID_LEN = 128; // workerId最大长度128

namespace Fit {
namespace RegistryInfo {
struct Application : public FitBase {
    Fit::string name;
    Fit::string nameVersion;
    string GetStrId() const
    {
        return name + '|' + nameVersion;
    }
    bool Equals(const Application& other)
    {
        return name == other.name && nameVersion == other.nameVersion;
    }
};
struct ApplicationMeta : public FitBase {
    Application id;
    Fit::map<Fit::string, Fit::string> extensions {};
    bool Equals(const ApplicationMeta& other)
    {
        return id.Equals(other.id) && extensions == other.extensions;
    }
};
struct Address : public FitBase {
    Fit::string host;
    uint32_t port;
    Fit::fit_protocol_type protocol;
    Fit::string workerId;
};

struct Worker : public FitBase {
    Fit::string workerId;
    Application application;
    uint64_t expire; // 过期的时间段
    Fit::string environment;
    uint64_t createTime;
    Fit::string version;
    map<string, string> extensions;
};

class WorkerEqual {
public:
    bool operator()(const Worker& l, const Worker& r) const
    {
        return l.workerId == r.workerId && l.application.GetStrId() == r.application.GetStrId() &&
               l.expire == r.expire && l.environment == r.environment && l.version == r.version &&
               l.extensions == r.extensions;
    }
};

struct FlatAddress : public FitBase {
    string host;
    uint32_t port;
    fit_protocol_type protocol;
    string workerId;
    string environment;
    fit_format_type_set formats;
    map<string, string> extensions;
};

// 一个应用下的所有worker和addresses
struct ApplicationInstance : public FitBase {
    Fit::vector<Worker> workers;
    Fit::vector<Address> addresses;
};

struct Fitable : public FitBase {
    Fit::string genericableId;
    Fit::string fitableId;
    Fit::string genericableVersion;
    Fit::string fitableVersion;
};

struct FitableMeta : public FitBase {
    Fitable fitable;
    Fit::fit_format_type_set formats; // PROTOBUF = 0, JSON = 1
    Application application;
    Fit::vector<Fit::string> aliases;
    Fit::vector<Fit::string> tags;
    Fit::map<Fit::string, Fit::string> extensions;
    Fit::string environment;
};
using FitableMetaPtr = std::shared_ptr<Fit::RegistryInfo::FitableMeta>;

struct FitableAddress : public FitBase {
    Fitable fitable;
    Fit::vector<Worker> workers;
};

struct FitableMetaAddress : public FitBase {
    FitableMeta fitableMeta;
    Fit::vector<Worker> workers;
};

struct WorkerDetail : public FitBase {
    Worker worker;
    vector<Address> addresses;
    ApplicationMeta app;
    vector<FitableMeta> fitables;
};

template<typename T>
struct DbType {
    Fit::string dbKey;
    T value;
};

class AddressCompare {
public:
    bool operator()(const RegistryInfo::Address& addressLeft, const RegistryInfo::Address& addressRight) const
    {
        if (addressLeft.host != addressRight.host) {
            return addressLeft.host < addressRight.host;
        }

        if (addressLeft.port != addressRight.port) {
            return addressLeft.port < addressRight.port;
        }

        if (addressLeft.protocol != addressRight.protocol) {
            return static_cast<int32_t>(addressLeft.protocol) < static_cast<int32_t>(addressRight.protocol);
        }

        if (addressLeft.workerId != addressRight.workerId) {
            return addressLeft.workerId < addressRight.workerId;
        }
        return true;
    }
};

class AddressEqual {
public:
    bool operator()(const RegistryInfo::Address& addressLeft, const RegistryInfo::Address& addressRight) const
    {
        return addressLeft.workerId == addressRight.workerId &&
            addressLeft.host == addressRight.host &&
            addressLeft.port == addressRight.port &&
            addressLeft.protocol == addressRight.protocol;
    }
};

class ApplicationHash {
public:
    size_t operator() (const Application& application) const
    {
        return std::hash<Fit::string>()(application.name) ^
            std::hash<Fit::string>()(application.nameVersion);
    }
};

class ApplicationEqual {
public:
    bool operator() (const Application& applicationLeft, const Application& applicationRight) const
    {
        return applicationLeft.name == applicationRight.name &&
            applicationLeft.nameVersion == applicationRight.nameVersion;
    }
};

class FitableHash {
public:
    size_t operator() (const Fitable& fitable) const
    {
        return std::hash<Fit::string>()(fitable.genericableId) ^
            std::hash<Fit::string>()(fitable.genericableVersion) ^
            std::hash<Fit::string>()(fitable.fitableId);
    }
};

class FitableEqual {
public:
    bool operator() (const Fitable& fitableLeft, const Fitable& fitableRight) const
    {
        return fitableLeft.fitableId == fitableRight.fitableId &&
            fitableLeft.genericableId == fitableRight.genericableId &&
            fitableLeft.genericableVersion == fitableRight.genericableVersion;
    }
};

class FitableMetaSharedPtrHash {
public:
    size_t operator()(const FitableMetaPtr& v) const noexcept
    {
        return std::hash<FitableMeta*>()(v.get());
    }
};
class FitableMetaSharedPtrEq {
public:
    bool operator()(const FitableMetaPtr& l, const FitableMetaPtr& r) const noexcept
    {
        return l.get() == r.get();
    }
};

struct WorkerMeta {
    Worker worker;
    Fit::vector<Address> addresses;
};
using WorkerMap = Fit::unordered_map<Fit::string, Worker>;
using AddressMap = Fit::unordered_map<Fit::string, Fit::vector<Address>>;
using WorkerMetaMap = Fit::unordered_map<Application, WorkerMeta, ApplicationHash, ApplicationEqual>;
using FitableMetaMap = Fit::unordered_map<Application, Fit::vector<FitableMeta>, ApplicationHash, ApplicationEqual>;
}
}

// ========================= registry struct v2 =========================
struct fit_service_instance_t : public FitBase {
    Fit::fitable_id fitable;
    Fit::string service_name;
    Fit::vector<Fit::fit_address> addresses;
    uint32_t timeoutSeconds; // 精度s
    Fit::RegistryInfo::Application application;
    Fit::vector<Fit::string> aliases;
    Fit::vector<Fit::string> tags;
    Fit::map<Fit::string, Fit::string> extensions;
};

struct fit_worker_info_t : public FitBase {
    Fit::fit_address address;
    Fit::string token;
    Fit::string id;
};

class fit_worker_info_equal_to {
public:
    bool operator()(const fit_worker_info_t &a, const fit_worker_info_t &b) const
    {
        return Fit::fit_address_equal_to()(a.address, b.address);
    }
};

class fit_worker_info_hash {
public:
    size_t operator()(const fit_worker_info_t &value) const
    {
        return Fit::fit_address_hash()(value.address);
    }
};
using fit_worker_info_hash_set = Fit::unordered_set<fit_worker_info_t, fit_worker_info_hash, fit_worker_info_equal_to>;
using fit_service_instance_set = Fit::vector<fit_service_instance_t>;
using fit_worker_info_set  = Fit::vector<fit_worker_info_t>;
#endif // FIT_REGISTRY_ENTITY_H
