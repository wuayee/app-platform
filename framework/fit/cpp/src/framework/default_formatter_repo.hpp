/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#ifndef DEFAULT_FORMATTER_REPO_HPP
#define DEFAULT_FORMATTER_REPO_HPP

#include <fit/stl/map.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/framework/formatter/formatter_collector_inner.hpp>
#include "fit/internal/framework/formatter_repo.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
struct FormatterKey {
    Fit::string generic_id;
    uint32_t type;

    bool operator<(const FormatterKey &key) const
    {
        return generic_id > key.generic_id;
    }
};

class DefaultFormatterRepo : public FormatterRepo {
public:
    DefaultFormatterRepo();
    ~DefaultFormatterRepo() override;

    bool Start() override;
    bool Stop() override;

    FormatterMetaPtr Get(const BaseSerialization &baseSerialization) override;
    FitCode Add(FormatterMetaPtrList formatterMetas) override;
    FitCode Remove(FormatterMetaPtrList formatterMetas) override;
    Fit::vector<int32_t> GetFormats(const Fit::string &genericId) override;
    void Clear() override;
private:
    Fit::shared_mutex sharedMt_ {};
    Fit::map<FormatterKey, FormatterMetaPtrList> formatters_ {};
    Fit::unique_ptr<FormatterMetaReceiver> receiver_ {};
    FormatterMetaReceiver* oldReceiver_ {};
};
}
}
}

#endif // DEFAULT_FORMATTER_REPO_HPP
