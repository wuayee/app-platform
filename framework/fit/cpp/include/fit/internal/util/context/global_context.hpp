/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/22 16:14
 */
#ifndef GLOBAL_CONTEXT_H
#define GLOBAL_CONTEXT_H

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>
#include <memory>
#include <fit/fit_code.h>

namespace Fit {
namespace Context {
class GlobalContext {
public:
    using CacheType = Fit::map<Fit::string, Fit::string>;

    GlobalContext() = default;
    ~GlobalContext() = default;

    void Ref();
    void UnRef();

    bool PutGlobalContext(const Fit::string &key, const Fit::string &value);
    bool RemoveGlobalContext(const Fit::string &key);
    Fit::string GetGlobalContext(const Fit::string &key) const;
    const CacheType &GetAllGlobalContext() const;
    bool RestoreGlobalContext(const CacheType &context);
    bool IsEmpty();

    FitCode Serialize(Fit::string &result);
    FitCode Deserialize(const Fit::string &result);

private:
    CacheType valueMap_;
    uint32_t refCount {0};
};

using GlobalContextPtr = std::shared_ptr<GlobalContext>;
}
}

#endif // GLOBAL_CONTEXT_H
