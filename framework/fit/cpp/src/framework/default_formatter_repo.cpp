/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#include "default_formatter_repo.hpp"
#include <algorithm>
#include <fit/stl/set.hpp>
#include <fit/fit_log.h>

namespace Fit {
namespace Framework {
namespace Formatter {
DefaultFormatterRepo::DefaultFormatterRepo() = default;
DefaultFormatterRepo::~DefaultFormatterRepo() = default;

bool DefaultFormatterRepo::Start()
{
    receiver_ = make_unique<FormatterMetaReceiver>();
    receiver_->Register = std::bind(&DefaultFormatterRepo::Add, this, std::placeholders::_1);
    receiver_->UnRegister = std::bind(&DefaultFormatterRepo::Remove, this, std::placeholders::_1);

    oldReceiver_ = FormatterMetaFlowTo(receiver_.get());
    Add(PopFormatterMetaCache());

    FIT_LOG_INFO("Genericable formatter repo is started.");
    return true;
}

bool DefaultFormatterRepo::Stop()
{
    FormatterMetaFlowTo(oldReceiver_);
    FIT_LOG_INFO("Genericable formatter repo is stopped.");
    return true;
}
FormatterMetaPtr DefaultFormatterRepo::Get(const BaseSerialization& baseSerialization)
{
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMt_);
    auto iter = formatters_.find(FormatterKey {
        baseSerialization.genericId,
        uint32_t(baseSerialization.fitableType)
    });
    if (iter == formatters_.end()) {
        return nullptr;
    }

    for (auto& item : iter->second) {
        for (const auto& it : baseSerialization.formats) {
            if (item->GetFormat() == it) {
                return item;
            }
        }
    }

    return nullptr;
}

FitCode DefaultFormatterRepo::Add(FormatterMetaPtrList formatterMetas)
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMt_);
    for (auto& item : formatterMetas) {
        auto iter = formatters_.find(FormatterKey {
            item->GetGenericId(),
            uint32_t(item->GetFitableType())
        });
        if (iter == formatters_.end()) {
            formatters_[FormatterKey {
                item->GetGenericId(),
                uint32_t(item->GetFitableType())
            }] = FormatterMetaPtrList {item};
            continue;
        }
        if (!Fit::exist(iter->second.begin(), iter->second.end(),
            [&item](const FormatterMetaPtr& v) -> bool { return v.get() == item.get(); })) {
            iter->second.push_back(item);
        }
    }
    return FIT_OK;
}

FitCode DefaultFormatterRepo::Remove(FormatterMetaPtrList formatterMetas)
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMt_);
    for (auto& item : formatterMetas) {
        auto iter = formatters_.find(FormatterKey {item->GetGenericId(), uint32_t(item->GetFitableType())});
        if (iter == formatters_.end()) {
            continue;
        }
        auto removeIter = std::remove_if(iter->second.begin(), iter->second.end(),
            [&item](const FormatterMetaPtr& v) -> bool { return v.get() == item.get(); });
        iter->second.erase(removeIter, iter->second.end());
        if (iter->second.empty()) {
            formatters_.erase(iter);
        }
    }
    return FIT_OK;
}

Fit::vector<int32_t> DefaultFormatterRepo::GetFormats(const Fit::string &genericId)
{
    Fit::set<int32_t> formats;
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMt_);
    for (const auto &item : formatters_) {
        if (item.first.generic_id == genericId) {
            for (const auto &formatterMetaPtr : item.second) {
                formats.insert(formatterMetaPtr->GetFormat());
            }
        }
    }
    return Fit::vector<int32_t>(formats.begin(), formats.end());
}

void DefaultFormatterRepo::Clear()
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMt_);
    formatters_.clear();
}
}
}
} // LCOV_EXCL_LINE