/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides context based on map.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/06
 */

#ifndef MAP_CONTEXT_HPP
#define MAP_CONTEXT_HPP

#include <fit/fit_code.h>
#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>

#include <memory>

namespace Fit {
namespace Context {
/**
 * 为数据传输提供map格式的上下文。
 */
class MapContext {
public:
    using ContentType = Fit::map<Fit::string, Fit::string>;

    /**
     * 当产生引用时调用。
     */
    void Ref();

    /**
     * 当丢弃引用时调用。
     */
    void Unref();

    /**
     * 设置一个上下文信息。
     *
     * @param key 表示上下文信息的键字符串。
     * @param value 表示上下文的值的字符串。
     * @return 若设置成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    void Put(const Fit::string& key, Fit::string value);

    /**
     * 移除指定键的上下文信息。
     *
     * @param key 表示上下文的信息的键的字符串。
     * @return 若移除成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    void Remove(const Fit::string& key);

    /**
     * 获取指定键的上下文信息的值。
     *
     * @param key 表示上下文信息的键的字符串。
     * @return 若存在该键对应的上下文信息，则为该上下文信息的值的字符串；否则是一个空字符串。
     */
    const Fit::string& Get(const Fit::string& key) const noexcept;

    /**
     * 获取上下文中包含的所有内容。
     *
     * @return 表示上下文内容的映射。
     */
    const ContentType& GetAll() const noexcept;

    /**
     * 使用上下文的所有信息重置当前上下文。
     *
     * @param content 表示将要替换成的上下文的内容。
     * @return 若重置成功，则为 <code>true</code>；否则为 <code>false</code>。
     */
    void Reset(ContentType content);

    /**
     * 获取一个值，该值指示上下文是否是空的。
     *
     * @return 若上下文是空的，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool IsEmpty() const noexcept;

    /**
     * 将当前上下文信息序列化到指定的字符串中。
     *
     * @param result 表示输出到的字符串。
     * @return 表示序列化的结果，若为 <code>FIT_OK</code>，则序列化成功；否则序列化失败。
     */
    FitCode Serialize(Fit::string& result);

    /**
     * 从字符串中反序列化上下文信息，并应用到当前上下文。
     *
     * @param data 表示包含上下文信息的字符串。
     * @return 表示反序列化的结果，若为 <code>FIT_OK</code>，则反序列化成功；否则反序列化失败。
     */
    FitCode Deserialize(const Fit::string& data);
private:
    ContentType values_;
    uint32_t referenceCount_ {0};
};

/**
 * 为基于映射的上下文提供共享指针。
 */
using MapContextPtr = std::shared_ptr<MapContext>;
}
}

#endif // MAP_CONTEXT_HPP
