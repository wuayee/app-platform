/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 泛化调用。
 * Author       : 梁济时 l00298979
 * Date         : 2022-01-12 19:35:24
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_BROKER_CLIENT_GENERICINVOKEV3_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_BROKER_CLIENT_GENERICINVOKEV3_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace broker {
namespace client {
/**
 * 为泛化调用提供可选参数配置。
 */
struct GenericInvokeOptions {
    /* *
     * 指示目标服务所在工作进程的唯一标识。
     */
    Fit::string workerId {};
    /* *
     * 指示目标服务实现的别名。
     */
    Fit::string alias {};
    /* *
     * 表示调用超时的毫秒数。
     */
    int64_t timeout {};
    bool HasWorkerId() const noexcept
    {
        return hasFields_[_FieldIndex::workerId];
    }
    const Fit::string &GetWorkerId() const
    {
        if (!HasWorkerId()) {
            FIT_THROW_INVALID_ARGUMENT("no workerId setted");
        }
        return workerId;
    }
    void SetWorkerId(const char *val, uint32_t len)
    {
        workerId = Fit::string { val, len };
        hasFields_[_FieldIndex::workerId] = true;
    }
    void SetWorkerId(Fit::string val)
    {
        workerId = std::move(val);
        hasFields_[_FieldIndex::workerId] = true;
    }
    Fit::string *MutableWorkerId()
    {
        hasFields_[_FieldIndex::workerId] = true;
        return &workerId;
    }
    void ClearWorkerId()
    {
        hasFields_[_FieldIndex::workerId] = false;
        workerId = Fit::string {};
    }
    bool HasAlias() const noexcept
    {
        return hasFields_[_FieldIndex::alias];
    }
    const Fit::string &GetAlias() const
    {
        if (!HasAlias()) {
            FIT_THROW_INVALID_ARGUMENT("no alias setted");
        }
        return alias;
    }
    void SetAlias(const char *val, uint32_t len)
    {
        alias = Fit::string { val, len };
        hasFields_[_FieldIndex::alias] = true;
    }
    void SetAlias(Fit::string val)
    {
        alias = std::move(val);
        hasFields_[_FieldIndex::alias] = true;
    }
    Fit::string *MutableAlias()
    {
        hasFields_[_FieldIndex::alias] = true;
        return &alias;
    }
    void ClearAlias()
    {
        hasFields_[_FieldIndex::alias] = false;
        alias = Fit::string {};
    }
    bool HasTimeout() const noexcept
    {
        return hasFields_[_FieldIndex::timeout];
    }
    int64_t GetTimeout() const
    {
        if (!HasTimeout()) {
            FIT_THROW_INVALID_ARGUMENT("no timeout setted");
        }
        return timeout;
    }
    void SetTimeout(int64_t val)
    {
        timeout = val;
        hasFields_[_FieldIndex::timeout] = true;
    }
    int64_t *MutableTimeout()
    {
        hasFields_[_FieldIndex::timeout] = true;
        return &timeout;
    }
    void ClearTimeout()
    {
        hasFields_[_FieldIndex::timeout] = false;
        timeout = int64_t {};
    }
    void Reset()
    {
        ClearWorkerId();
        ClearAlias();
        ClearTimeout();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 3;
    ::Fit::Bits<FIELD_COUNT> hasFields_ { true };
    struct _FieldIndex {
        static constexpr uint32_t workerId = 0;
        static constexpr uint32_t alias = 1;
        static constexpr uint32_t timeout = 2;
    };
};
struct __genericInvokeV3 {
    using InType =
        ::Fit::Framework::ArgumentsIn<const Fit::string *, const Fit::string *, const GenericInvokeOptions *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::string **>;
};

/**
 * 泛化调用。
 *
 * @param genericableId 表示待调用的泛化服务的唯一标识的字符串。
 * @param requestJson 表示待调用泛化服务的输入参数的JSON表现形式的字符串。
 * @param options 表示泛化调用的可选参数配置。
 * @return 表示执行结果的JSON表现形式的字符串。
 */
class genericInvokeV3
    : public ::Fit::Framework::ProxyClient<FitCode(__genericInvokeV3::InType, __genericInvokeV3::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "8dd312b496994c38b8629bb25cfc6bc9";
    genericInvokeV3()
        : ::Fit::Framework::ProxyClient<FitCode(__genericInvokeV3::InType, __genericInvokeV3::OutType)>(GENERIC_ID)
    {}
    explicit genericInvokeV3(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__genericInvokeV3::InType, __genericInvokeV3::OutType)>(GENERIC_ID, ctx)
    {}
    ~genericInvokeV3() = default;
};
}
}
}
}
}

#endif
