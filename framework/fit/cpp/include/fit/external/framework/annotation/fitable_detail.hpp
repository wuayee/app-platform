/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/8
 * Notes:       :
 */

#ifndef FIT_FITABLE_DETAIL_H
#define FIT_FITABLE_DETAIL_H

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <functional>
#include <fit/external/framework/function_proxy.hpp>

namespace Fit {
namespace Framework {
namespace Annotation {
enum class FitableType {
    MAIN = 0,
    VALIDATE = 1,
    BEFORE = 2,
    AFTER = 3,
    ERROR = 4,
    ROUTE = 5,
    LOADBALANCE = 6
};

using FitableFunctionProxyType = FunctionProxyType<FitCode>;
class FitableDetail {
public:
    explicit FitableDetail(FitableFunctionProxyType val);
    ~FitableDetail();

    FitableFunctionProxyType GetFunctionProxy() const noexcept;

    FitableDetail& SetGenericId(const char* val);
    const Fit::string& GetGenericId() const noexcept;

    FitableDetail& SetGenericVersion(const char* val);
    const Fit::string& GetGenericVersion() const noexcept;

    FitableDetail& SetFitableId(const char* val);
    const Fit::string& GetFitableId() const noexcept;

    FitableDetail& SetFitableVersion(const char* val);
    const Fit::string& GetFitableVersion() const noexcept;

    FitableDetail& SetType(FitableType val);
    FitableType GetType() const noexcept;

    FitableDetail(const FitableDetail& other);
    FitableDetail(FitableDetail &&other) noexcept;
    FitableDetail& operator=(const FitableDetail& other);
    FitableDetail& operator=(FitableDetail &&other) noexcept ;

private:
    struct Impl;
    std::unique_ptr<Impl> impl_;
};

using FitableDetailPtr = std::shared_ptr<FitableDetail>;
using FitableDetailPtrList = Fit::vector<FitableDetailPtr>;
using FitableDetailList = Fit::vector<Annotation::FitableDetail>;
}
}
}

#endif // BROKER_REGISTRY_FITABLE_H
