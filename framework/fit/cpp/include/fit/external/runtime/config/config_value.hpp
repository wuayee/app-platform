/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#ifndef CONFIG_VALUE_HPP
#define CONFIG_VALUE_HPP

#include <cstdint>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace Fit {
namespace Config {
using ValueType = uint32_t;
static constexpr ValueType VALUE_TYPE_NULL = 0;
static constexpr ValueType VALUE_TYPE_BOOL = 1;
static constexpr ValueType VALUE_TYPE_INT = 2;
static constexpr ValueType VALUE_TYPE_DOUBLE = 4;
static constexpr ValueType VALUE_TYPE_STRING = 8;
static constexpr ValueType VALUE_TYPE_ARRAY = 16;
static constexpr ValueType VALUE_TYPE_OBJECT = 32;
class Value {
public:
    Value();
    virtual ~Value();

    Value(const Value &other) = delete;
    Value(Value &&other) = delete;
    Value &operator=(const Value &other) = delete;
    Value &operator=(Value &&other) = delete;

    /**
     * 只有Array类型才可使用
     * @param index
     * @return 返回对应位置的值, 不存在时返回value IsNull为true
     */
    virtual Value &operator[](int32_t index);

    /**
     * 只有Object类型才可使用
     * @param key
     * @return 返回对应的值, 不存在时返回value IsNull为true
     */
    virtual Value &operator[](const char *key);

    virtual ValueType GetType() const;

    /**
    * 返回当前位置下一层的key值
    * @return
    */
    virtual vector<string> GetKeys() const;

    /**
     * 返回Array的大小
     * @return
     */
    virtual int32_t Size() const;

    // 类型不匹配时抛出std::runtime_error异常
    virtual bool AsBool() const;
    virtual int32_t AsInt() const;
    virtual double AsDouble() const;
    virtual string AsString() const;

    // 失败时返回默认值
    virtual bool AsBool(bool defaultValue) const;
    virtual int32_t AsInt(int32_t defaultValue) const;
    virtual double AsDouble(double defaultValue) const;
    virtual string AsString(const char *defaultValue) const;
    virtual string AsString(const string& defaultValue) const;

    virtual bool IsNull() const;
    virtual bool IsBool() const;
    virtual bool IsInt() const;
    virtual bool IsDouble() const;
    virtual bool IsString() const;
    virtual bool IsObject() const;
    virtual bool IsArray() const;
};
}
}
#endif // CONFIG_VALUE_HPP
