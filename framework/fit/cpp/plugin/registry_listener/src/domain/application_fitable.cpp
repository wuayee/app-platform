/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for concrete fitable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <domain/application_fitable.hpp>

#include <domain/application.hpp>
#include <domain/fitable.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

ApplicationFitable::ApplicationFitable(ApplicationPtr application, FitablePtr fitable)
    : application_(std::move(application)), fitable_(std::move(fitable))
{
}

RegistryListenerPtr ApplicationFitable::GetRegistryListener() const
{
    return GetFitable()->GetRegistryListener();
}

ApplicationPtr ApplicationFitable::GetApplication() const
{
    return application_;
}

FitablePtr ApplicationFitable::GetFitable() const
{
    return fitable_;
}

Fit::vector<int32_t> ApplicationFitable::GetFormats() const
{
    return formats_;
}

void ApplicationFitable::SetFormats(Fit::vector<int32_t> formats)
{
    formats_ = std::move(formats);
}

int32_t ApplicationFitable::Compare(const ApplicationFitablePtr& another) const
{
    if (another.get() == this) {
        return 0;
    } else {
        return Compare(another->GetApplication(), another->GetFitable());
    }
}

int32_t ApplicationFitable::Compare(const ApplicationPtr& application, const FitablePtr& fitable) const
{
    int32_t ret = application_->Compare(application);
    if (ret == 0) {
        ret = fitable_->Compare(fitable);
    }
    return ret;
}
