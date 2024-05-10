/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 16:06
 */
#ifndef OBJCONTEXT_HPP
#define OBJCONTEXT_HPP

#include <cstddef>
#include <cstdint>
#include <fit/stl/list.hpp>
#include <fit/fit_code.h>
#include <fit/internal/util/context/allocator.hpp>
#include "allocator.hpp"

namespace Fit {
namespace Context {
struct CacheObjDetail {
    void *realMem;
    void *obj;
    std::function<void()> deconstruct;
};

class ObjContext {
public:
    explicit ObjContext(AllocatorPtr allocator);
    virtual ~ObjContext() = default;

    void *Malloc(size_t size);
    void Free(void *obj);
    int32_t LinkObj(const CacheObjDetail &cacheObj);
    int32_t UnlinkObj(void *obj);
    void DeleteClassObjList();

private:
    using CacheObjDetailPtr = std::shared_ptr<CacheObjDetail>;
    using ClassObjList = Fit::list<CacheObjDetailPtr>;

    CacheObjDetailPtr CreateCacheObj(const CacheObjDetail &cacheObj);

    AllocatorPtr allocator_;
    ClassObjList classObjList_;
};

using ObjManagerPtr = std::unique_ptr<ObjContext>;
}
}

#endif // OBJCONTEXT_HPP
