/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : 查询注册中心中对应genericable下运行中的fitable
 * Author       : 宋永坦 s00558940
 * Date         : 2023-04-17 15:03:59
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_RUNNING_FITABLES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_RUNNING_FITABLES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct QueryRunningFitablesParam {
    Fit::string genericableId{};
    Fit::string genericableVersion{};
    bool HasGenericableId() const noexcept { return hasFields_[_FieldIndex::genericableId]; }
    const Fit::string &GetGenericableId() const
    {
        if (!HasGenericableId()) {
            FIT_THROW_INVALID_ARGUMENT("no genericableId setted");
        }
        return genericableId;
    }
    void SetGenericableId(const char *val, uint32_t len)
    {
        genericableId = Fit::string{val, len};
        hasFields_[_FieldIndex::genericableId] = true;
    }
    void SetGenericableId(Fit::string val)
    {
        genericableId = std::move(val);
        hasFields_[_FieldIndex::genericableId] = true;
    }
    Fit::string *MutableGenericableId()
    {
        hasFields_[_FieldIndex::genericableId] = true;
        return &genericableId;
    }
    void ClearGenericableId()
    {
        hasFields_[_FieldIndex::genericableId] = false;
        genericableId = Fit::string{};
    }
    bool HasGenericableVersion() const noexcept { return hasFields_[_FieldIndex::genericableVersion]; }
    const Fit::string &GetGenericableVersion() const
    {
        if (!HasGenericableVersion()) {
            FIT_THROW_INVALID_ARGUMENT("no genericableVersion setted");
        }
        return genericableVersion;
    }
    void SetGenericableVersion(const char *val, uint32_t len)
    {
        genericableVersion = Fit::string{val, len};
        hasFields_[_FieldIndex::genericableVersion] = true;
    }
    void SetGenericableVersion(Fit::string val)
    {
        genericableVersion = std::move(val);
        hasFields_[_FieldIndex::genericableVersion] = true;
    }
    Fit::string *MutableGenericableVersion()
    {
        hasFields_[_FieldIndex::genericableVersion] = true;
        return &genericableVersion;
    }
    void ClearGenericableVersion()
    {
        hasFields_[_FieldIndex::genericableVersion] = false;
        genericableVersion = Fit::string{};
    }
    void Reset()
    {
        ClearGenericableId();
        ClearGenericableVersion();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 2;
    ::Fit::Bits<FIELD_COUNT> hasFields_ {true};
    struct _FieldIndex {
        static constexpr uint32_t genericableId = 0;
        static constexpr uint32_t genericableVersion = 1;
    };
};
struct RunningFitable {
    ::fit::hakuna::kernel::registry::shared::FitableMeta *meta{nullptr};

    Fit::vector<Fit::string> environments{};
    bool HasMeta() const noexcept { return meta != nullptr; }
    const ::fit::hakuna::kernel::registry::shared::FitableMeta &GetMeta() const
    {
        if (!HasMeta()) {
            FIT_THROW_INVALID_ARGUMENT("no fitable setted");
        }
        return *meta;
    }
    void SetMeta(::fit::hakuna::kernel::registry::shared::FitableMeta *val)
    {
        meta = val;
        hasFields_[_FieldIndex::meta] = true;
    }
    ::fit::hakuna::kernel::registry::shared::FitableMeta *&MutableMeta()
    {
        hasFields_[_FieldIndex::meta] = true;
        return meta;
    }
    void ClearMeta()
    {
        meta = {};
    }
    bool HasEnvironments() const noexcept { return true; }
    const Fit::vector<Fit::string> &GetEnvironments() const
    {
        return environments;
    }
    void SetEnvironments(Fit::vector<Fit::string> val)
    {
        environments = std::move(val);
    }
    Fit::vector<Fit::string> *MutableEnvironments()
    {
        return &environments;
    }
    void ClearEnvironments()
    {
        environments.clear();
    }
    void Reset()
    {
        ClearMeta();
        ClearEnvironments();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 2;
    ::Fit::Bits<FIELD_COUNT> hasFields_ {true};
    struct _FieldIndex {
        static constexpr uint32_t meta = 0;
        static constexpr uint32_t environments = 1;
    };
};
struct __queryRunningFitables {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<QueryRunningFitablesParam> *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<RunningFitable> **>;
};

/**
 * 查询当前注册中心正在运行的fitable
 *
 * @param req
 * @return
 */
class queryRunningFitables : public ::Fit::Framework::ProxyClient<FitCode(
    __queryRunningFitables::InType, __queryRunningFitables::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "7c52fb4fdfa243af928f23607fbbee02";
    queryRunningFitables() : ::Fit::Framework::ProxyClient<FitCode(
        __queryRunningFitables::InType, __queryRunningFitables::OutType)>(GENERIC_ID) {}
    explicit queryRunningFitables(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(
            __queryRunningFitables::InType, __queryRunningFitables::OutType)>(GENERIC_ID, ctx) {}
    ~queryRunningFitables() = default;
};
}
}
}
}
}

#endif
