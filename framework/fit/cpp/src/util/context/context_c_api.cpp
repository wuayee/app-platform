/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 16:00
 */

#include <fit/external/util/context/context_c_api.h>
#include <fit/internal/util/context/context_wrapper.hpp>
#include <fit/internal/util/context/context.hpp>
#include <cstring>
#include <fit/fit_log.h>
#include <algorithm>
#include <securec.h>

namespace {
using namespace Fit::Context;

int32_t ContextLinkCObj(ContextObj ctx, void *mem)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_FAIL;
    }

    return wrapper->context.LinkObj(CacheObjDetail {
        mem,
        mem,
        nullptr
    });
}
}

void* ContextMalloc(ContextObj ctx, size_t size)
{
    auto realMem = ContextMallocNoManage(ctx, size);
    if (realMem == nullptr) {
        return nullptr;
    }
    memset_s(realMem, size, 0x0, size);
    auto ret = ContextLinkCObj(ctx, realMem);
    if (ret != FIT_ERR_SUCCESS) {
        ContextFreeNoManage(ctx, realMem);
        realMem = nullptr;
    }
    return realMem;
}

void ContextFreeHasManaged(ContextObj ctx, void* data)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }

    wrapper->context.UnlinkObj(data);
}

// string util
int32_t ContextStringResize(ContextObj ctx, size_t size, Fit_String *resStr)
{
    if (size == 0) {
        resStr->size = 0;
        resStr->data = nullptr;
        return FIT_OK;
    }

    resStr->data = static_cast<char *>(ContextMalloc(ctx, size + 1));
    if (resStr->data == nullptr) {
        resStr->size = 0;
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    resStr->size = size;
    resStr->data[size] = '\0';
    return FIT_OK;
}

int32_t ContextStringAssign(ContextObj ctx, const char *fromString, Fit_String *resStr)
{
    if (fromString == nullptr) {
        resStr->size = 0;
        resStr->data = nullptr;
        return FIT_OK;
    }
    return ContextStringAssignWithSize(ctx, fromString, strlen(fromString), resStr);
}

int32_t ContextStringAssignWithSize(ContextObj ctx, const char *fromString, size_t size, Fit_String *resStr)
{
    if (fromString == nullptr || size == 0) {
        resStr->size = 0;
        resStr->data = nullptr;
        return FIT_OK;
    }

    auto fromSize = strlen(fromString);
    if (fromSize == 0) {
        resStr->size = 0;
        resStr->data = nullptr;
        return FIT_OK;
    }

    auto realSize = (fromSize < size) ? (fromSize) : (size);
    resStr->data = static_cast<char *>(ContextMalloc(ctx, realSize + 1));
    if (resStr->data == nullptr) {
        resStr->size = 0;
        return FIT_ERR_CTX_BAD_ALLOC;
    }

    resStr->size = realSize;
    auto ret = memcpy_s(resStr->data, realSize, fromString, realSize);
    if (ret != 0) {
        FIT_LOG_ERROR("Copy data error, %d.", ret);
        return FIT_ERR_FAIL;
    }
    resStr->data[realSize] = '\0';
    return FIT_OK;
}

// bytes util
int32_t ContextBytesResize(ContextObj ctx, size_t size, Fit_Bytes *resBytes)
{
    if (size == 0) {
        resBytes->size = 0;
        resBytes->data = nullptr;
        return FIT_OK;
    }

    resBytes->data = static_cast<char *>(ContextMalloc(ctx, size));
    if (resBytes->data == nullptr) {
        resBytes->size = 0;
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    resBytes->size = size;
    return FIT_OK;
}

int32_t ContextBytesAssign(ContextObj ctx, const char *fromBytes, size_t size, Fit_Bytes *resBytes)
{
    if (fromBytes == nullptr) {
        resBytes->size = 0;
        resBytes->data = nullptr;
        return FIT_OK;
    }
    auto ret = ContextBytesResize(ctx, size, resBytes);
    if (ret != FIT_OK) {
        return ret;
    }
    resBytes->size = size;
    ret = memcpy_s(resBytes->data, size, fromBytes, size);
    if (ret != 0) {
        FIT_LOG_ERROR("Copy data error, %d.", ret);
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}

/**
 * 向global context中设置一组值
 * @param ctx 上下文对象句柄
 * @param key
 * @param value
 * @return 是否设置成功
 */
bool PutGlobalContext(ContextObj ctx, const Fit_String *key, const Fit_String *value)
{
    if (key == nullptr || value == nullptr || key->data == nullptr || value->data == nullptr) {
        FIT_LOG_ERROR("Bad input! please check it!");
        return false;
    }

    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.PutGlobalContext(
        Fit::string(key->data, key->size), Fit::string(value->data, value->size));
}

/**
 * 删除globalContext中key对应的值
 * @param ctx 上下文对象句柄
 * @param key
 * @return 是否成功
 */
bool RemoveGlobalContext(ContextObj ctx, const Fit_String *key)
{
    if (key == nullptr || key->data == nullptr) {
        FIT_LOG_ERROR("Bad input! please check it!");
        return false;
    }

    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.RemoveGlobalContext(Fit::string(key->data, key->size));
}

namespace {
bool CppStringToKvEntry(ContextObj ctx, const Fit::string &str, Fit_String *entry)
{
    if (entry == nullptr) {
        FIT_LOG_ERROR("Entry is nullptr.");
        return false;
    }
    entry->data = static_cast<char *>(ContextMalloc(ctx, str.size() + 1));
    entry->size = static_cast<uint32_t>(str.size());
    str.copy(entry->data, entry->size);
    return true;
}
}
/**
* 获取globalContext中key对应的值
* @param ctx 上下文对象句柄
* @param key
* @return 是否成功获取
*/
bool GetGlobalContext(ContextObj ctx, const Fit_String *key, Fit_String **value)
{
    if (key == nullptr || key->data == nullptr) {
        FIT_LOG_ERROR("Bad input! please check it!");
        return false;
    }

    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }

    auto res = wrapper->context.GetGlobalContext(Fit::string(key->data, key->size));
    if (res.empty()) {
        FIT_LOG_ERROR("Not find in global context, key:%s.", key->data);
        return false;
    }

    *value = static_cast<Fit_String *>(ContextMalloc(ctx, sizeof(Fit_String)));
    return CppStringToKvEntry(ctx, res, *value);
}

/**
 * 获取globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return resultSet key/value 数组
 * @return size 数组大小
 */
void GetAllGlobalContext(ContextObj ctx, ContextKV **resultSet, uint32_t *size)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    auto res = wrapper->context.GetAllGlobalContext();
    if (res.empty()) {
        return;
    }

    *size = static_cast<uint32_t>(res.size());
    (*resultSet) = static_cast<ContextKV *>(ContextMalloc(ctx, *size * sizeof(ContextKV)));
    if ((*resultSet) == nullptr) {
        FIT_LOG_ERROR("ResultSet is nullptr");
        return;
    }
    uint32_t idx {0};
    for (const auto &entry : res) {
        auto &tmp = (*resultSet)[idx];
        CppStringToKvEntry(ctx, entry.first, &tmp.key);
        CppStringToKvEntry(ctx, entry.second, &tmp.value);
        ++idx;
    }
}

/**
 * 设置globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @param kvSet key/value 数组
 * @param size 数组大小
 * @return 是否成功
 */
bool RestoreGlobalContext(ContextObj ctx, const ContextKV *kvSet, uint32_t size)
{
    if (kvSet == nullptr) {
        return false;
    }

    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }

    Fit::map<Fit::string, Fit::string> tmp;
    for (uint32_t i = 0; i < size; i++) {
        auto &entry = kvSet[i];
        tmp[Fit::string(entry.key.data, entry.key.size)] = Fit::string(entry.value.data, entry.value.size);
    }

    return wrapper->context.RestoreGlobalContext(tmp);
}

namespace {
constexpr const char *ROUTE_KEY_PREFIX = "fit.route.";
constexpr int32_t ROUTE_KEY_PREFIX_LEN = 10;
}

/**
 * 向route context中设置一组值
 * @param ctx 上下文对象句柄
 * @param key
 * @param value
 * @return 是否设置成功
 */
bool PutOneRouteContext(ContextObj ctx, const Fit_String *key, const Fit_String *value)
{
    if (key == nullptr || value == nullptr || key->data == nullptr || value->data == nullptr) {
        FIT_LOG_ERROR("Bad input! please check it!");
        return false;
    }

    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }

    Fit::string routeKey = ROUTE_KEY_PREFIX + Fit::string(key->data, key->size);
    return wrapper->context.PutGlobalContext(
        routeKey, Fit::string(value->data, value->size));
}

/**
 * 获取route Context中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return resultSet key/value 数组
 * @return size 数组大小
 */
void GetAllRouteContext(ContextObj ctx, ContextKV **resultSet, uint32_t *size)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }

    Fit::map<Fit::string, Fit::string> tmp;
    auto allContext = wrapper->context.GetAllGlobalContext();
    for (auto it = allContext.begin(); it != allContext.end(); ++it) {
        if (it->first.find(ROUTE_KEY_PREFIX) == 0) {
            tmp[it->first.substr(ROUTE_KEY_PREFIX_LEN)] = it->second;
        }
    }

    if (tmp.empty()) {
        return;
    }

    *size = static_cast<uint32_t>(tmp.size());
    (*resultSet) = static_cast<ContextKV *>(ContextMalloc(ctx, *size * sizeof(ContextKV)));
    if ((*resultSet) == nullptr) {
        FIT_LOG_ERROR("ResultSet is nullptr");
        return;
    }
    uint32_t idx {0};
    for (const auto &entry : tmp) {
        auto &item = (*resultSet)[idx];
        CppStringToKvEntry(ctx, entry.first, &item.key);
        CppStringToKvEntry(ctx, entry.second, &item.value);
        ++idx;
    }
}
