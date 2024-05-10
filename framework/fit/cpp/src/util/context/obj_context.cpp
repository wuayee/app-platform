/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 16:17
 */

#include <fit/internal/util/context/obj_context.hpp>
#include <algorithm>

#include <fit/fit_log.h>

namespace Fit {
namespace Context {
ObjContext::ObjContext(AllocatorPtr allocator)
    : allocator_(std::move(allocator)) {}

void *ObjContext::Malloc(size_t size)
{
    return allocator_->Malloc(size);
}

void ObjContext::Free(void *obj)
{
    allocator_->Free(obj);
}

int32_t ObjContext::LinkObj(const CacheObjDetail &cacheObj)
{
    auto listEntry = CreateCacheObj(cacheObj);
    classObjList_.push_front(listEntry);
    return FIT_ERR_SUCCESS;
}

int32_t ObjContext::UnlinkObj(void *obj)
{
    auto it = std::find_if(classObjList_.begin(), classObjList_.end(), [obj](const CacheObjDetailPtr &objPtr) {
        return objPtr->obj == obj;
    });
    if (it != classObjList_.end()) {
        const auto &objPtr = *it;
        if (objPtr->deconstruct != nullptr) {
            objPtr->deconstruct();
        }
        Free(objPtr->realMem);
        classObjList_.erase(it);
    }

    return FIT_ERR_SUCCESS;
}

void ObjContext::DeleteClassObjList()
{
    std::for_each(classObjList_.begin(), classObjList_.end(), [this](const CacheObjDetailPtr &objPtr) {
        if (objPtr->deconstruct != nullptr) {
            objPtr->deconstruct();
        }

        Free(objPtr->realMem);
    });

    classObjList_.clear();
}

ObjContext::CacheObjDetailPtr ObjContext::CreateCacheObj(const CacheObjDetail &cacheObj)
{
    auto mem = static_cast<CacheObjDetail*>(Malloc(sizeof(CacheObjDetail)));
    auto obj = new(mem) CacheObjDetail();

    obj->obj = cacheObj.obj;
    obj->realMem = cacheObj.realMem;
    obj->deconstruct = cacheObj.deconstruct;

    return CacheObjDetailPtr(obj, [this, mem](CacheObjDetail *ptr) {
        ptr->~CacheObjDetail();
        Free(mem);
    });
}
}
}