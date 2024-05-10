/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/5/31
 * Notes:       :
 */

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/stl/memory.hpp>

namespace Fit {
namespace Framework {
namespace Annotation {
struct FitableDetail::Impl {
    FitableFunctionProxyType functionProxy;
    Fit::string genericId;
    Fit::string genericVersion {"1.0.0"};
    Fit::string fitableId;
    Fit::string fitableVersion {"1.0.0"};
    FitableType type {FitableType::MAIN};
    bool isCpp {true};
    Fit::map<Fit::string, Fit::string> properties;

    explicit Impl(FitableFunctionProxyType val)
    {
        functionProxy = std::move(val);
    }

    const FitableFunctionProxyType& GetFunctionProxy() const noexcept
    {
        return functionProxy;
    }

    Impl& SetGenericId(const char* val)
    {
        genericId = val;
        return *this;
    }

    const Fit::string& GetGenericId() const noexcept
    {
        return genericId;
    }

    Impl& SetFitableId(const char* val)
    {
        fitableId = val;
        return *this;
    }

    Impl& SetGenericVersion(const char* val)
    {
        genericVersion = val;
        return *this;
    }

    const Fit::string& GetGenericVersion() const noexcept
    {
        return genericVersion;
    }

    const Fit::string& GetFitableId() const noexcept
    {
        return fitableId;
    }

    Impl& SetFitableVersion(const char* val)
    {
        fitableVersion = val;
        return *this;
    }

    const Fit::string& GetFitableVersion() const noexcept
    {
        return fitableVersion;
    }

    Impl& SetType(FitableType val)
    {
        type = val;
        return *this;
    }

    FitableType GetType() const noexcept
    {
        return type;
    }

    Impl& SetProperty(const char* key, const char* value)
    {
        properties[key] = value;
        return *this;
    }
};

FitableDetail::FitableDetail(FitableFunctionProxyType val)
{
    impl_ = make_unique<Impl>(move(val));
}

FitableDetail::~FitableDetail()
{
}

FitableDetail& FitableDetail::SetGenericId(const char* val)
{
    impl_->SetGenericId(val);
    return *this;
}

FitableDetail& FitableDetail::SetFitableId(const char* val)
{
    impl_->SetFitableId(val);
    return *this;
}

FitableDetail& FitableDetail::SetFitableVersion(const char* val)
{
    impl_->SetFitableVersion(val);
    return *this;
}

FitableDetail& FitableDetail::SetType(FitableType val)
{
    impl_->SetType(val);
    return *this;
}

const Fit::string& FitableDetail::GetGenericId() const noexcept
{
    return impl_->GetGenericId();
}

const Fit::string& FitableDetail::GetFitableId() const noexcept
{
    return impl_->GetFitableId();
}

const Fit::string& FitableDetail::GetFitableVersion() const noexcept
{
    return impl_->GetFitableVersion();
}

FitableType FitableDetail::GetType() const noexcept
{
    return impl_->GetType();
}

FitableDetail::FitableDetail(const FitableDetail& other)
{
    impl_ = make_unique<Impl>(other.GetFunctionProxy());
    *this = other;
}

FitableDetail& FitableDetail::operator=(const FitableDetail& other)
{
    *impl_ = *other.impl_;
    return *this;
}

FitableDetail::FitableDetail(FitableDetail&& other) noexcept
{
    impl_ = std::move(other.impl_);
    other.impl_ = nullptr;
}

FitableDetail& FitableDetail::operator=(FitableDetail&& other) noexcept
{
    if (this != &other) {
        impl_ = std::move(other.impl_);
    }
    return *this;
}

FitableFunctionProxyType FitableDetail::GetFunctionProxy() const noexcept
{
    return impl_->GetFunctionProxy();
}
FitableDetail& FitableDetail::SetGenericVersion(const char* val)
{
    impl_->SetGenericVersion(val);
    return *this;
}
const Fit::string& FitableDetail::GetGenericVersion() const noexcept
{
    return impl_->GetGenericVersion();
}
}
}
} // LCOV_EXCL_LINE