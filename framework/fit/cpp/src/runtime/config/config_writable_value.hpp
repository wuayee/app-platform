/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : writable value
 * Author       : songyongtan
 * Create       : 2023-08-16
 * Notes:       :
 */

#ifndef FIT_CONFIG_WRITABLE_VALUE_HPP
#define FIT_CONFIG_WRITABLE_VALUE_HPP

#include <fit/external/runtime/config/config_value.hpp>
#include <fit/value.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace Fit {
namespace Config {
class WritableValue : public Value {
public:
    /**
     * 清空对象，后续IsNull为true
     * @return 自身
     */
    virtual WritableValue& SetNull();
    /**
     * 更改为bool类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    virtual WritableValue& SetValue(bool val);
    /**
     * 更改为int类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    virtual WritableValue& SetValue(int32_t val);
    /**
     * 更改为double类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    virtual WritableValue& SetValue(double val);
    /**
     * 更改为字符串类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    virtual WritableValue& SetValue(const char* val);
    virtual WritableValue& SetValue(const string& val);
    /**
     * 更改为object类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    virtual WritableValue& SetObject();
    /**
     * 添加name节点
     * @param name
     * @return 返回新增节点
     */
    virtual WritableValue& AddMember(const char* name);
    /**
     * 查找name节点
     * @param name
     * @return 返回查找到的节点，未查到是返回节点IsNull方法为true
     */
    virtual WritableValue& FindMember(const char* name);
    /**
     * 更改为数组类型节点, 如果之前是其它类型则数据被清空
     * @param reserveSize
     * @return 自身
     */
    virtual WritableValue& SetArray();
    virtual WritableValue& Reserve(int32_t reserveSize);
    /**
     * 数组中添加新节点
     * @return 返回新增的节点
     */
    virtual WritableValue& PushBack();

    static unique_ptr<WritableValue> New();

    static WritableValue Null;
};

class BooleanValue : public WritableValue {
public:
    explicit BooleanValue(bool value);
    ValueType GetType() const override;
    bool IsNull() const override;
    bool AsBool() const override;
    bool AsBool(bool) const override;
    bool IsBool() const override;
    WritableValue& SetValue(bool val) override;

private:
    bool data_;
};

class StringValue : public WritableValue {
public:
    explicit StringValue(string value);
    ValueType GetType() const override;
    bool IsNull() const override;
    bool IsString() const override;
    string AsString() const override;
    string AsString(const char*) const override;
    string AsString(const string&) const override;
    WritableValue& SetValue(const char* val) override;
    WritableValue& SetValue(const string& val) override;

private:
    string data_;
};

class DoubleValue : public WritableValue {
public:
    explicit DoubleValue(double value);
    ValueType GetType() const override;
    bool IsNull() const override;
    double AsDouble() const override;
    double AsDouble(double) const override;
    bool IsDouble() const override;
    WritableValue& SetValue(double val) override;

private:
    double data_;
};

class IntegerValue : public WritableValue {
public:
    explicit IntegerValue(int32_t value);
    ValueType GetType() const override;
    bool IsNull() const override;
    int32_t AsInt() const override;
    int32_t AsInt(int32_t) const override;
    bool IsInt() const override;
    WritableValue& SetValue(int32_t val) override;

private:
    int32_t data_;
};

class ObjectValue : public WritableValue {
public:
    Value& operator[](const char* key) override;
    ValueType GetType() const override;
    vector<string> GetKeys() const override;
    bool IsNull() const override;
    bool IsObject() const override;
    WritableValue& AddMember(const char* name) override;
    WritableValue& FindMember(const char* name) override;

private:
    map<string, unique_ptr<WritableValue>> data_;
};

class ArrayValue : public WritableValue {
public:
    Value& operator[](int32_t index) override;
    ValueType GetType() const override;
    int32_t Size() const override;
    bool IsNull() const override;
    bool IsArray() const override;
    WritableValue& Reserve(int32_t reserveSize) override;
    WritableValue& PushBack() override;

private:
    vector<unique_ptr<WritableValue>> data_;
};
}
}

#endif