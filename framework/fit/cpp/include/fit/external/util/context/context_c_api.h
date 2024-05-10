/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 14:51
 */
#ifndef CONTEXT_C_API_H
#define CONTEXT_C_API_H

#include <fit/fit_define_c.h>
#include "context_base.h"

#ifdef __cplusplus
extern "C" {
#endif
// 继承ContextBase方法

// 跟随ctx生命周期
// 内存分配，该内存会被管理
void *ContextMalloc(ContextObj ctx, size_t size);
#define FIT_CONTEXT_MAKE_OBJ(ctx, type) (type*)ContextMalloc(ctx, sizeof(type))

// 从ctx中移除并释放
void ContextFreeHasManaged(ContextObj ctx, void* data);

// string util
/**
 * Resize Fit_String, data指针会被覆盖，新申请的内存管理在ctx中，
 * 返回成功后，data内存已经分配，可以直接赋值
 * @param ctx 上下文对象句柄
 * @param size 字符串长度
 * @param resStr 被设置的Fit_String
 * @return 是否设置成功 成功 0 失败 其他
 */
int32_t ContextStringResize(ContextObj ctx, size_t size, Fit_String *resStr);

/**
 * Assign Fit_String, data指针会被覆盖，新申请的内存管理在ctx中，
 * 返回成功后，fromString内容深拷贝至Fit_String
 * @param ctx 上下文对象句柄
 * @param fromString 源c字符串
 * @param resStr 被设置的Fit_String
 * @return 是否设置成功 成功 0 失败 其他
 */
int32_t ContextStringAssign(ContextObj ctx, const char *fromString, Fit_String *resStr);

/**
 * Assign Fit_String, data指针会被覆盖，申请的内存管理在ctx中，
 * 返回成功后，fromString内容中size长度的内容深拷贝至Fit_String
 * @param ctx 上下文对象句柄
 * @param fromString 源c字符串
 * @param size 长度
 * @param resStr 被设置的Fit_String
 * @return 是否设置成功 成功 0 失败 其他
 */
int32_t ContextStringAssignWithSize(ContextObj ctx, const char *fromString, size_t size, Fit_String *resStr);

// bytes util
/**
 * Resize Fit_Bytes, data指针会被覆盖，新申请的内存管理在ctx中，
 * 返回成功后，data内存已经分配，可以直接赋值
 * @param ctx 上下文对象句柄
 * @param size 字符串长度
 * @param resBytes 最终被设置的bytes
 * @return 是否设置成功 成功 0 失败 其他
 */
int32_t ContextBytesResize(ContextObj ctx, size_t size, Fit_Bytes *resBytes);

/**
 * Assign Fit_Bytes, data指针会被覆盖，申请的内存管理在ctx中，
 * 返回成功后，fromBytes内容中size长度的内容深拷贝至Fit_Bytes
 * @param ctx 上下文对象句柄
 * @param fromBytes 源数据
 * @param resBytes 被设置的Fit_Bytes
 * @return 是否设置成功 成功 0 失败 其他
 */
int32_t ContextBytesAssign(ContextObj ctx, const char *fromBytes, size_t size, Fit_Bytes *resBytes);

// global context
typedef struct {
    Fit_String key;
    Fit_String value;
}ContextKV;

/**
 * 向global context中设置一组值
 * @param ctx 上下文对象句柄
 * @param key
 * @param value
 * @return 是否设置成功
 */
bool PutGlobalContext(ContextObj ctx, const Fit_String *key, const Fit_String *value);

/**
 * 删除globalContext中key对应的值
 * @param ctx 上下文对象句柄
 * @param key
 * @return 是否成功
 */
bool RemoveGlobalContext(ContextObj ctx, const Fit_String *key);

/**
* 获取globalContext中key对应的值
* @param ctx 上下文对象句柄
* @param key
* @return 是否成功获取
*/
bool GetGlobalContext(ContextObj ctx, const Fit_String *key, Fit_String **value);

/**
 * 获取globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return resultSet key/value 数组
 * @return size 数组大小
 */
void GetAllGlobalContext(ContextObj ctx, ContextKV **resultSet, uint32_t *size);

/**
 * 设置globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @param kvSet key/value 数组
 * @param size 数组大小
 * @return 是否成功
 */
bool RestoreGlobalContext(ContextObj ctx, const ContextKV *kvSet, uint32_t size);

// context route
/**
 * 向route context中设置一组值
 * @param ctx 上下文对象句柄
 * @param key
 * @param value
 * @return 是否设置成功
 */
bool PutOneRouteContext(ContextObj ctx, const Fit_String *key, const Fit_String *value);

/**
 * 获取route Context中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return resultSet key/value 数组
 * @return size 数组大小
 */
void GetAllRouteContext(ContextObj ctx, ContextKV **resultSet, uint32_t *size);

#ifdef __cplusplus
};
#endif

#endif // CONTEXTCAPI_H
