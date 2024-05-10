/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 14:45
 */

#ifndef CONTEXT_BASE_H
#define CONTEXT_BASE_H

#include <cstddef>
#include <cstdint>
#include <fit/fit_code.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef void* ContextObj;
typedef void* (*AllocFunc)(size_t);
typedef void(*FreeFunc)(void *);

ContextObj NewContextDefault(void);
ContextObj NewContextWithMemFunc(AllocFunc allocFunc, FreeFunc freeFunc);

void ContextFreeAll(ContextObj ctx);
void ContextDestroy(ContextObj ctx);

// 提供一组直接分配/释放内存的方法，内部调用用户注册的函数
// 该方法需要配套使用，用户自行分配和释放
void *ContextMallocNoManage(ContextObj ctx, int32_t size);
#define FIT_CONTEXT_MAKE_OBJ_NO_MANAGE(ctx, type) (type*)ContextMallocNoManage(ctx, sizeof(type))

void ContextFreeNoManage(ContextObj ctx, void* data);

FitCode ContextSetFitableId(ContextObj ctx, const char *val);
const char *ContextGetFitableId(ContextObj ctx);

void ContextSetAlias(ContextObj ctx, const char *alias);
const char *ContextGetAlias(ContextObj ctx);

void ContextSetTargetWorker(ContextObj ctx, const char* targetWorkerId);
const char* ContextGetTargetWorker(ContextObj ctx);

/**
 * 获取重试次数， 不设置默认0
 * @return 重试次数
 */
uint32_t ContextGetRetry(ContextObj ctx);

/**
 * 设置重试次数， 不设置默认不重试
 * @param ctx 上下文对象句柄
 * @param count 重试次数
 */
void ContextSetRetry(ContextObj ctx, uint32_t count);

/**
 * 获取超时时间设置, 不设置默认5000ms
 * @return 超时时间(ms)
 */
uint32_t ContextGetTimeout(ContextObj ctx);

/**
 * 设置超时时间， 不设置默认5000ms
 * @param ctx 上下文对象句柄
 * @param ms 超时时间，单位ms
 */
void ContextSetTimeout(ContextObj ctx, uint32_t ms);

// 规则类型
typedef enum {
    POLICY_DEFAULT = 0, // 默认使用配置的default fid
    POLICY_ALIAS,       // 别名调用
    POLICY_RULE,        // 规则调用
    POLICY_FITABLE_ID   // 指定fitableId调用
}FitablePolicy;

/**
 * 设置fitable调用规则， 不设置默认POLICY_DEFAULT
 * @param ctx 上下文对象句柄
 * @param policy 规则编号
 */
void ContextSetPolicy(ContextObj ctx, FitablePolicy policy);

/**
 * 获取fitable调用规则, 不设置默认POLICY_DEFAULT
 * @return 规则
 */
FitablePolicy ContextGetPolicy(ContextObj ctx);

// 该组为老接口，适配老版本，不建议使用
// 可以使用 ContextMallocNoManage / ContextFreeNoManage
void *ContextMallocWithOutCache(ContextObj ctx, size_t size);
void ContextFree(ContextObj ctx, void *obj);

#ifdef __cplusplus
}
#endif

#endif // CONTEXTBASE_H
