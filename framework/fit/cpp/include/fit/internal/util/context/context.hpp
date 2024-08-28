/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 16:33
 */
#ifndef CONTEXT_HPP
#define CONTEXT_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/vector.hpp>
#include <fit/external/util/context/context_api.hpp>
#include "obj_context.hpp"
#include "global_context.hpp"
#include "map_context.hpp"
#include "allocator.hpp"

namespace Fit {
namespace Context {
class Context : public ObjContext {
public:
    explicit Context(AllocatorPtr allocator);
    ~Context() override;

    void SetGerericableId(const Fit::string &genericableId) noexcept;
    const Fit::string &GetGenericableId() const noexcept;

    void SetAlias(const Fit::string &genericableId) noexcept;
    const Fit::string &GetAlias() const noexcept;

    void SetFitableId(const char* val);
    const string& GetFitableId() const noexcept;

    void SetTargetWorker(const Fit::string& targetWorkerId) noexcept;
    const Fit::string& GetTargetWorker() const noexcept;

    /**
     * 获取重试次数， 不设置默认0
     * @return 重试次数
     */
    uint32_t GetRetry() const noexcept;

    /**
     * 设置重试次数， 不设置默认不重试
     * @param ctx 上下文对象句柄
     * @param count 重试次数
     */
    void SetRetry(uint32_t count) noexcept;

    /**
     * 获取超时时间设置, 不设置默认5000ms
     * @return 超时时间(ms)
     */
    uint32_t GetTimeout() const noexcept;

    /**
     * 设置超时时间， 不设置默认5000ms
     * @param ctx 上下文对象句柄
     * @param ms 超时时间，单位ms
     */
    void SetTimeout(uint32_t ms) noexcept;

    /**
     * 获取调用策略， 默认为0
     * @return 调用策略
     */
    uint8_t GetPolicy() const noexcept;

    /**
     * 设置调用策略
     * @param policy 策略
     */
    void SetPolicy(uint8_t policy) noexcept;

    void SetAccessToken(const Fit::string& AccessToken) noexcept;
    const Fit::string& GetAccessToken() const noexcept;

    /**
    * 获取context中指定调用地址
    * @param ctx 上下文对象句柄
    * @return 指定调用地址
    */
    const std::unique_ptr<TargetAddress>& GetTargetAddress() const noexcept;
    /**
    * 向context中设置指定调用地址，nullptr取消已设置地址
    * @param ctx 上下文对象句柄
    * @param targetAddressPtr
    * @return 是否设置成功
    */
    FitCode SetTargetAddress(const TargetAddress* targetAddressPtr) noexcept;

    /**
     * 获取上下文路由键值对
     * @return 键值对
     */
    const Fit::map<Fit::string, Fit::string> &GetCtxKV() const noexcept;

    /**
     * 设置上下文路由键值对
     * @param kv kv键值对
     */
    void SetCtxKV(const Fit::map<Fit::string, Fit::string> &kv) noexcept;

    /**
    * 向global context中设置一组值
    * @param ctx 上下文对象句柄
    * @param key
    * @param value
    * @return 是否设置成功
    */
    bool PutGlobalContext(const Fit::string &key, const Fit::string &value);

    /**
     * 删除globalContext中key对应的值
     * @param ctx 上下文对象句柄
     * @param key
     * @return 是否成功
     */
    bool RemoveGlobalContext(const Fit::string &key);

    /**
    * 获取globalContext中key对应的值
    * @param ctx 上下文对象句柄
    * @param key
    * @return value，不存在时为空值
    */
    Fit::string GetGlobalContext(const Fit::string &key);

    /**
     * 获取globalContext中所有的缓存数据
     * @param ctx 上下文对象句柄
     * @return 缓存数据
     */
    Fit::map<Fit::string, Fit::string> GetAllGlobalContext();

    /**
     * 设置globalContext中所有的缓存数据
     * @param ctx 上下文对象句柄
     * @param data 数据
     * @return 是否成功
     */
    bool RestoreGlobalContext(const Fit::map<Fit::string, Fit::string> &data);

    /**
     * 获取一个值，该值指示是否包含全局上下文。
     *
     * @return 若包含全局上下文，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool HasGlobalContext();

    FitCode SerializeGlobalContext(Fit::string &result);

    FitCode DeserializeGlobalContext(const Fit::string &data);

    /**
     * 设置异常的上下文信息。
     *
     * @param key 表示上下文信息的键的字符串。
     * @param value 表示上下文信息的值的字符串。
     * @return 若设置成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool PutExceptionContext(const Fit::string &key, const Fit::string &value);

    /**
     * 移除指定键的异常上下文。
     *
     * @param key 表示上下文信息的键的字符串。
     * @return 若移除成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool RemoveExceptionContext(const Fit::string &key);

    /**
     * 获取指定键的异常上下文的值。
     *
     * @param key 表示异常上下文的键的字符串。
     * @return 表示异常上下文的值的字符串。
     */
    Fit::string GetExceptionContext(const Fit::string &key);

    /**
     * 获取所有的异常上下文信息。
     *
     * @return 表示异常上下文信息的键值映射。
     */
    Fit::map<Fit::string, Fit::string> GetAllExceptionContext();

    /**
     * 使用指定键值映射的内容重置异常上下文信息。
     *
     * @param data 表示异常上下文信息的键值映射。
     * @return 若重置成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool RestoreExceptionContext(const Fit::map<Fit::string, Fit::string> &data);

    /**
     * 获取一个值，该值指示是否包含异常上下文。
     *
     * @return 若包含异常上下文，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool HasExceptionContext();

    /**
     * 序列化异常上下文。
     *
     * @param result 表示异常上下文的序列化结果输出到的字符串。
     * @return 表示序列化结果，若为 <code>FIT_OK</code>，则序列化成功；否则序列化失败。
     */
    FitCode SerializeExceptionContext(Fit::string &result);

    /**
     * 反序列化异常上下文。
     *
     * @param data 表示包含异常上下文信息的字符串。
     * @return 表示反序列化结果，若为 <code>FIT_OK</code>，则反序列化成功；否则反序列化失败。
     */
    FitCode DeserializeExceptionContext(const Fit::string &data);

private:
    Fit::string genericableId_ {};
    Fit::string fitableId_ {};
    Fit::string alias_ {};
    Fit::string targetWorkerId_ {};
    // 重试次数
    uint32_t retry_ {0};
    // 超时时间，默认5000ms
    uint32_t timeout_ {5000};
    // 调用策略
    uint8_t policy_ {0};
    Fit::string accessToken_ {};
    // 调用地址
    std::unique_ptr<TargetAddress> targetAddressPtr_;
    // global context
    static thread_local GlobalContextPtr globalContext_;
    static thread_local MapContextPtr exceptionContext_;
};
}
}
#endif // CONTEXT_HPP
