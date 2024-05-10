/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2021-09-14 10:45:38
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SYNCHRONIZEFITSERVICE_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SYNCHRONIZEFITSERVICE_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
#include <fit/stl/vector.hpp>
#include <fit/memory/fit_base.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct SyncSeviceAddress : public FitBase {
    ::fit::hakuna::kernel::registry::shared::FitableInstance *fitableInstance { nullptr };
    // 操作类型0 增加；1删除
    int32_t operateType {};
    bool HasFitableInstance() const noexcept
    {
        return fitableInstance != nullptr;
    }
    const ::fit::hakuna::kernel::registry::shared::FitableInstance &GetFitableInstance() const
    {
        if (!HasFitableInstance()) {
            FIT_THROW_INVALID_ARGUMENT("no fitableInstance setted");
        }
        return *fitableInstance;
    }
    void SetFitableInstance(::fit::hakuna::kernel::registry::shared::FitableInstance *val)
    {
        fitableInstance = val;
        hasFields_[_FieldIndex::fitableInstance] = true;
    }
    ::fit::hakuna::kernel::registry::shared::FitableInstance *&MutableFitableInstance()
    {
        hasFields_[_FieldIndex::fitableInstance] = true;
        return fitableInstance;
    }
    void ClearFitableInstance()
    {
        fitableInstance = {};
    }
    bool HasOperateType() const noexcept
    {
        return hasFields_[_FieldIndex::operateType];
    }
    int32_t GetOperateType() const
    {
        if (!HasOperateType()) {
            FIT_THROW_INVALID_ARGUMENT("no operateType setted");
        }
        return operateType;
    }
    void SetOperateType(int32_t val)
    {
        operateType = val;
        hasFields_[_FieldIndex::operateType] = true;
    }
    int32_t *MutableOperateType()
    {
        hasFields_[_FieldIndex::operateType] = true;
        return &operateType;
    }
    void ClearOperateType()
    {
        hasFields_[_FieldIndex::operateType] = false;
        operateType = int32_t {};
    }
    void Reset()
    {
        ClearFitableInstance();
        ClearOperateType();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 2;
    ::Fit::Bits<FIELD_COUNT> hasFields_ { true };
    struct _FieldIndex {
        static constexpr uint32_t fitableInstance = 0;
        static constexpr uint32_t operateType = 1;
    };
};
struct __synchronizeFitService {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::vector<SyncSeviceAddress> *>;
    using OutType = ::Fit::Framework::ArgumentsOut<int32_t **>;
};

class synchronizeFitService
    : public ::Fit::Framework::ProxyClient<FitCode(__synchronizeFitService::InType, __synchronizeFitService::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "ce35055fd49b4803addddc4a89efab66";
    synchronizeFitService()
        : ::Fit::Framework::ProxyClient<FitCode(__synchronizeFitService::InType, __synchronizeFitService::OutType)>(
        GENERIC_ID)
    {}
    explicit synchronizeFitService(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__synchronizeFitService::InType, __synchronizeFitService::OutType)>(
        GENERIC_ID, ctx)
    {}
    ~synchronizeFitService() = default;
};
}
}
}
}
}

#endif
