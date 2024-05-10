/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */
#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/internal/framework/formatter/formatter_collector_inner.hpp>
#include <fit/stl/map.hpp>
#include <fit/fit_log.h>
#include <mutex>
#include <utility>

namespace Fit {
namespace Framework {
namespace Formatter {
class FormatterMeta::Impl {
public:
    void SetGenericId(Fit::string val)
    {
        genericId_ = std::move(val);
    }

    const Fit::string& GetGenericId() const
    {
        return genericId_;
    }

    void SetFormat(int32_t val)
    {
        format_ = val;
    }

    int32_t GetFormat() const
    {
        return format_;
    }

    void SetArgsInConverter(ArgConverterList val)
    {
        argsIn_ = std::move(val);
    }

    const ArgConverterList& GetArgsInConverter() const
    {
        return argsIn_;
    }

    void SetArgsOutConverter(ArgConverterList val)
    {
        argsOut_ = std::move(val);
    }

    const ArgConverterList& GetArgsOutConverter() const
    {
        return argsOut_;
    }

    Arguments CreateArgOut(ContextObj ctx) const
    {
        return createArgOutFunc_(ctx);
    }

    void SetCreateArgsOut(std::function<Arguments(ContextObj ctx)> func)
    {
        createArgOutFunc_ = std::move(func);
    }

    void SetFitableType(Fit::Framework::Annotation::FitableType fitableType)
    {
        fitableType_ = fitableType;
    }

    Fit::Framework::Annotation::FitableType GetFitableType()
    {
        return fitableType_;
    }
private:
    Fit::string genericId_;
    int32_t format_ {};
    ArgConverterList argsIn_;
    ArgConverterList argsOut_;
    std::function<Arguments(ContextObj ctx)> createArgOutFunc_;
    Fit::Framework::Annotation::FitableType fitableType_ {Fit::Framework::Annotation::FitableType::MAIN};
};

FormatterMeta::FormatterMeta() : impl_ {new Impl()} {}

FormatterMeta::~FormatterMeta()
{
    delete impl_;
}

void FormatterMeta::SetGenericId(Fit::string val)
{
    impl_->SetGenericId(std::move(val));
}

const Fit::string& FormatterMeta::GetGenericId() const
{
    return impl_->GetGenericId();
}

void FormatterMeta::SetFormat(int32_t val)
{
    impl_->SetFormat(val);
}

int32_t FormatterMeta::GetFormat() const
{
    return impl_->GetFormat();
}

void FormatterMeta::SetArgsInConverter(ArgConverterList val)
{
    impl_->SetArgsInConverter(std::move(val));
}

const ArgConverterList& FormatterMeta::GetArgsInConverter() const
{
    return impl_->GetArgsInConverter();
}

void FormatterMeta::SetArgsOutConverter(ArgConverterList val)
{
    impl_->SetArgsOutConverter(std::move(val));
}

const ArgConverterList& FormatterMeta::GetArgsOutConverter() const
{
    return impl_->GetArgsOutConverter();
}

Arguments FormatterMeta::CreateArgOut(ContextObj ctx) const
{
    return impl_->CreateArgOut(ctx);
}

void FormatterMeta::SetCreateArgsOut(std::function<Arguments(ContextObj ctx)> func)
{
    return impl_->SetCreateArgsOut(std::move(func));
}

void FormatterMeta::SetFitableType(Fit::Framework::Annotation::FitableType fitableType)
{
    return impl_->SetFitableType(fitableType);
}
Fit::Framework::Annotation::FitableType FormatterMeta::GetFitableType()
{
    return impl_->GetFitableType();
}

static std::mutex& GetLock()
{
    static auto* instance = new std::mutex;
    return *instance;
}

static Fit::map<void*, FormatterMetaPtr>& GetCacheInstance()
{
    static auto* instance = new Fit::map<void*, FormatterMetaPtr> {};
    return *instance;
}

static FormatterMetaReceiver*& GetReceiver()
{
    static FormatterMetaReceiver* instance {};
    return instance;
}

void FormatterCollector::Register(const FormatterMetaPtrList& val)
{
    for (auto& item : val) {
        FIT_LOG_DEBUG("Register gid = %s, format = %d, fitableType = %d.",
            item->GetGenericId().c_str(), item->GetFormat(), int32_t(item->GetFitableType()));
    }

    std::lock_guard<std::mutex> guard(GetLock());
    if (GetReceiver()) {
        return GetReceiver()->Register(val);
    }

    for (auto& item : val) {
        GetCacheInstance()[item.get()] = item;
    }
}

void FormatterCollector::UnRegister(const FormatterMetaPtrList& val)
{
    for (auto& item : val) {
        FIT_LOG_DEBUG("Unregister gid = %s, format = %d.", item->GetGenericId().c_str(), item->GetFormat());
    }
    std::lock_guard<std::mutex> guard(GetLock());
    if (GetReceiver()) {
        return GetReceiver()->UnRegister(val);
    }

    for (auto& item : val) {
        GetCacheInstance().erase(item.get());
    }
}

FormatterMetaPtrList __attribute__ ((visibility ("default"))) PopFormatterMetaCache()
{
    std::lock_guard<std::mutex> guard(GetLock());
    FormatterMetaPtrList result;
    result.reserve(GetCacheInstance().size());
    for (auto& item : GetCacheInstance()) {
        result.push_back(item.second);
    }
    GetCacheInstance().clear();

    return result;
}

__attribute__ ((visibility ("default"))) FormatterMetaReceiver* FormatterMetaFlowTo(FormatterMetaReceiver* target)
{
    std::lock_guard<std::mutex> guard(GetLock());
    FormatterMetaReceiver* old = GetReceiver();
    GetReceiver() = target;

    return old;
}
}
}
} // LCOV_EXCL_LINE