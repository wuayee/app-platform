/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/7
 * Notes:       :
 */

#ifndef FIT_VALUE_HPP
#define FIT_VALUE_HPP

#include <cstdint>
#include <memory>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace Fit {
enum class ValueType {
    NULL_,
    BOOL = 1,
    NUMBER = 2,
    STRING = 3,
    OBJECT = 4,
    ARRAY = 5
};

class ObjectValue;

class ArrayValue;

/**
 * value can be one of (null, bool, number, string, object, array)
 * you can make a value, such as
 * --------------------------------
 * null: Value nullValue;
 *       -----------------
 *       Value nullValue {nullptr};
 *       -----------------
 *       Value nullValue;
 *       nullValue.SetNull();
 * --------------------------------
 * bool: Value boolValue {true}
 *       -----------------
 *       Value boolValue;
 *       boolValue.SetBool(true);
 * --------------------------------
 * number: Value numberValue {456}
 *         -----------------
 *         Value numberValue;
 *         numberValue.SetInt32(456);
 * --------------------------------
 * string: value stringValue {"hello"}
 *         -----------------
 *         Value stringValue;
 *         stringValue.SetString("hello");
 * --------------------------------
 * object: initializer_list with key(must be string)-value pairs, will be treated as object type value
 *         value objectValue {
 *                              {"null", nullptr},
 *                              {"b", true},
 *                              {"s", "hello"},
 *                              {"object", {
 *                                      {"b", true}
 *                                  }
 *                              },
 *                           }
 *         -----------------
 *         Value objectValue;
 *         auto &object = objectValue.SetObject();
 *         object["null"] = nullptr; // or object.Add("null")
 *         object["b"] = true; // or object.Add("b", true)
 *         object["s"] = "hello"; // or object.Add("s", "hello")
 *         object["object"] = {{"b", true}}; // or object.Add("object").SetObject().Add("b", true)
 *         for (auto &node : object) {
 *             // node type is Fit::Member
 *             std::cout << node.Name() << node.Value().Type() << std::endl;
 *         }
 * --------------------------------
 * array: initializer_list with non key(must be string)-value pairs, will be treated as array type value
 *        note: need more than one element
 *        value arrayValue {
 *                              nullptr,
 *                              true,
 *                              "string",
 *                              {"b", true}, // this will be treated as array type
 *                              {{"null", nullptr},} // this will be treated as object type
 *                           }
 *         -----------------
 *         Value arrayValue;
 *         auto &array = objectValue.SetArray();
 *         array.PushBack();
 *         array.PushBack(true); // or array.PushBack().SetBool(true);
 *         array.PushBack("hello"); // or array.PushBack().SetString("hello");
 *         array.PushBack() = {{"b", true}}; // or array.PushBack().SetObject().Add("b", true)
 *         for (auto &node : array) {
 *             // node type is Fit::Value
 *         }
 * --------------------------------
 */
class Value {
public:
    class Impl;
    Value();
    explicit Value(std::unique_ptr<Impl> impl);
    Value(const Value &);
    Value(Value &&) noexcept;
    Value &operator = (const Value &);
    Value &operator = (Value &&) noexcept;
    Value(std::nullptr_t);
    Value(bool value);
    Value(int32_t value);
    Value(double value);
    Value(const char *value);
    Value(std::initializer_list<Value> init);
    ~Value();
    ValueType Type() const;
    bool IsNull() const;
    bool IsBool() const;
    bool IsNumber() const;
    bool IsString() const;
    bool IsObject() const;
    bool IsArray() const;

    /* *
     * will clear all data and IsNull() is true
     * @return self
     */
    Value &SetNull();
    /* *
     * will clear all data and IsBool() is true
     * @return self
     */
    Value &SetBool(bool value);
    /* *
     * will clear all data and IsNumber() is true
     * @return self
     */
    Value &SetInt32(int32_t value);
    /* *
     * will clear all data and IsNumber() is true
     * @return self
     */
    Value &SetUInt32(uint32_t value);
    /* *
     * will clear all data and IsNumber() is true
     * @return self
     */
    Value &SetDouble(double value);
    /* *
     * will clear all data and IsString() is true
     * @return self
     */
    Value &SetString(const char *value);
    /* *
     * will clear all data and IsObject() is true
     * @return reference of object value
     */
    ObjectValue &SetObject();
    /* *
     * will clear all data and IsArray() is true
     * @return reference of array value
     */
    ArrayValue &SetArray();

    /* *
     * pre IsBool() == true, otherwise throw std::logic_error
     * @return bool value
     */
    bool AsBool() const;
    /* *
     * pre IsNumber() == true and value in range(MinInt32 ~ MaxInt32), otherwise throw std::logic_error
     * @return int32 value
     */
    int32_t AsInt32() const;
    /* *
     * pre IsNumber() == true and value in range(0 ~ MaxUInt32), otherwise throw std::logic_error
     * @return uint32 value
     */
    uint32_t AsUInt32() const;
    /* *
     * pre IsNumber() == true, otherwise throw std::logic_error
     * @return double value
     */
    double AsDouble() const;
    /* *
     * pre IsString() == true, otherwise throw std::logic_error
     * @return string value
     */
    const char *AsString() const;
    /* *
     * pre IsObject() == true, otherwise throw std::logic_error
     * @return object value
     */
    ObjectValue &AsObject();
    const ObjectValue &AsObject() const;
    /* *
     * pre IsArray() == true, otherwise throw std::logic_error
     * @return array value
     */
    ArrayValue &AsArray();
    const ArrayValue &AsArray() const;

    /* *
     * pre IsBool() == true, otherwise return defaultValue
     * @return bool value
     */
    bool AsBool(bool defaultValue) const;
    /* *
     * pre IsNumber() == true and value in range(MinInt32 ~ MaxInt32), otherwise return defaultValue
     * @return int32 value
     */
    int32_t AsInt32(int32_t defaultValue) const;
    /* *
     * pre IsNumber() == true and value in range(0 ~ MaxUInt32), otherwise return defaultValue
     * @return uint32 value
     */
    uint32_t AsUInt32(uint32_t defaultValue) const;
    /* *
     * pre IsNumber() == true, otherwise return defaultValue
     * @return double value
     */
    double AsDouble(double defaultValue) const;
    /* *
     * pre IsString() == true, otherwise return defaultValue
     * @return string value
     */
    const char *AsString(const char *defaultValue) const;
    /* *
     * pre IsObject() == true, otherwise return defaultValue
     * @return object value
     */
    const ObjectValue &AsObject(const ObjectValue &defaultValue) const;
    /* *
     * pre IsArray() == true, otherwise return defaultValue
     * @return array value
     */
    const ArrayValue &AsArray(const ArrayValue &defaultValue) const;

private:
    std::unique_ptr<Impl> impl_;
};

class ConstMember {
public:
    ConstMember() = default;
    ConstMember(const char *name, const Value *value) : name_(name), value_(value) {}
    ~ConstMember() = default;

    const char *Name() const
    {
        return name_;
    }

    const class Value &Value() const
    {
        return *value_;
    }

private:
    const char *name_ {};
    const class Value *value_ {};
};

class Member {
public:
    Member() = default;
    Member(const char *name, Value *value) : name_(name), value_(value) {}
    ~Member() = default;

    const char *Name() const
    {
        return name_;
    }

    const class Value &Value() const
    {
        return *value_;
    }

    class Value &Value()
    {
        return *value_;
    }

private:
    const char *name_ {};
    class Value *value_ {};
};

class ConstMemberIterator {
public:
    class Impl;

    ConstMemberIterator();
    explicit ConstMemberIterator(std::unique_ptr<Impl> impl);
    ~ConstMemberIterator();
    ConstMemberIterator(const Fit::ConstMemberIterator &);
    ConstMemberIterator &operator = (const Fit::ConstMemberIterator &);
    const ConstMember &operator*() const;
    const ConstMember *operator->() const;
    ConstMemberIterator &operator ++ ();
    ConstMemberIterator &operator -- ();
    ConstMemberIterator operator ++ (int);
    ConstMemberIterator operator -- (int);
    bool operator == (const ConstMemberIterator &) const noexcept;
    bool operator != (const ConstMemberIterator &) const noexcept;

    std::unique_ptr<Impl> impl_;
};

class MemberIterator {
public:
    class Impl;

    MemberIterator();
    explicit MemberIterator(std::unique_ptr<Impl> impl);
    ~MemberIterator();
    MemberIterator(const Fit::MemberIterator &);
    MemberIterator &operator = (const Fit::MemberIterator &);
    Member &operator*();
    Member *operator->();
    MemberIterator &operator ++ ();
    MemberIterator &operator -- ();
    MemberIterator operator ++ (int);
    MemberIterator operator -- (int);
    bool operator == (const MemberIterator &) const noexcept;
    bool operator != (const MemberIterator &) const noexcept;

    std::unique_ptr<Impl> impl_;
};

/**
 * object value, like map<name, Value>
 * --------------------------------
 * object: initializer_list with key(must be string)-value pairs, or else throw std::logic_error
 * ObjectValue object {
 *                      {"null", nullptr},
 *                      {"b", true},
 *                      {"s", "hello"},
 *                      {"object", {
 *                              {"b", true}
 *                          }
 *                      },
 *                   }
 * -----------------
 * ObjectValue object;
 * object["null"] = nullptr; // or object.Add("null")
 * object["b"] = true; // or object.Add("b", true)
 * object["s"] = "hello"; // or object.Add("s", "hello")
 * object["object"] = {{"b", true}}; // or object.Add("object").SetObject().Add("b", true)
 * for (auto &node : object) {
 *     // node type is Fit::Member
 *     std::cout << node.Name() << node.Value().Type() << std::endl;
 * }
 * --------------------------------
 */
class ObjectValue {
public:
    class Impl;
    using ConstIterator = ConstMemberIterator;
    using Iterator = MemberIterator;

    ObjectValue();
    ObjectValue(const ObjectValue &);
    ObjectValue(ObjectValue &&) noexcept;
    ObjectValue &operator = (const ObjectValue &);
    ObjectValue &operator = (ObjectValue &&) noexcept;
    ObjectValue(std::initializer_list<Value> init);
    ~ObjectValue();
    /* *
     * get member Value with name
     * if name is not found in object, then is is added to the object with null type value
     * @param name of member
     * @return value
     */
    Value &operator[](const char *name);
    /* *
     * get member Value with name
     * if name is not found in object, then is is added to the object with null type value
     * @param name of member
     * @return value
     */
    const Value &operator[](const char *name) const;
    /* *
     * @return member count
     */
    uint32_t Size() const;
    /* *
     * @return Size() == 0
     */
    bool Empty() const;
    /* *
     * release all member
     */
    void Clear();
    /* *
     * add a member with name, return null type value
     * if the name is exist, will return the exist value and set with null type
     * @param name name of member
     * @return the member value for name
     */
    Value &Add(const char *name);
    /* *
     * add a bool member value
     * if the name is exist, will return the exist value and set with bool value
     * @param name name of member
     * @param value bool value
     * @return self
     */
    ObjectValue &Add(const char *name, bool value);
    /* *
     * add a int32 member value
     * if the name is exist, will return the exist value and set with int32 value
     * @param name name of member
     * @param value int32 value
     * @return self
     */
    ObjectValue &Add(const char *name, int32_t value);
    /* *
     * add a uint32 member value
     * if the name is exist, will return the exist value and set with uint32 value
     * @param name name of member
     * @param value uint32 value
     * @return self
     */
    ObjectValue &Add(const char *name, uint32_t value);
    /* *
     * add a double member value
     * if the name is exist, will return the exist value and set with double value
     * @param name name of member
     * @param value double value
     * @return self
     */
    ObjectValue &Add(const char *name, double value);
    /* *
     * add a string member value
     * if the name is exist, will return the exist value and set with string value
     * @param name name of member
     * @param value string value
     * @return self
     */
    ObjectValue &Add(const char *name, const char *value);
    /* *
     * remove name of member
     * @param name name of member
     * @return
     */
    ObjectValue &Remove(const char *name);
    ObjectValue &Remove(Iterator iter);
    bool Exist(const char *name) const;
    /* *
     * get all member names, if empty, return an empty list
     * @return all member names
     */
    Fit::vector<Fit::string> GetNames() const;

    ConstIterator Find(const char *name) const;
    Iterator Find(const char *name);

    ConstIterator Begin() const;
    ConstIterator End() const;
    Iterator Begin();
    Iterator End();

    ConstIterator begin() const
    {
        return Begin();
    }

    ConstIterator end() const
    {
        return End();
    }

    Iterator begin()
    {
        return Begin();
    }

    Iterator end()
    {
        return End();
    }

    std::unique_ptr<Impl> impl_;
};

class ConstValueIterator {
public:
    class Impl;

    ConstValueIterator();
    explicit ConstValueIterator(std::unique_ptr<Impl> impl);
    ~ConstValueIterator();
    ConstValueIterator(const Fit::ConstValueIterator &);
    ConstValueIterator &operator = (const Fit::ConstValueIterator &);
    const Value &operator*() const;
    const Value *operator->() const;
    ConstValueIterator &operator ++ ();
    ConstValueIterator &operator -- ();
    ConstValueIterator operator ++ (int);
    ConstValueIterator operator -- (int);
    bool operator == (const ConstValueIterator &) const noexcept;
    bool operator != (const ConstValueIterator &) const noexcept;

    std::unique_ptr<Impl> impl_;
};

class ValueIterator {
public:
    class Impl;

    ValueIterator();
    explicit ValueIterator(std::unique_ptr<Impl> impl);
    ~ValueIterator();
    ValueIterator(const Fit::ValueIterator &);
    ValueIterator &operator = (const Fit::ValueIterator &);
    Value &operator*() const;
    Value *operator->() const;
    ValueIterator &operator ++ ();
    ValueIterator &operator -- ();
    ValueIterator operator ++ (int);
    ValueIterator operator -- (int);
    bool operator == (const ValueIterator &) const noexcept;
    bool operator != (const ValueIterator &) const noexcept;

    std::unique_ptr<Impl> impl_;
};

/**
 * array value like std::vector<Value>
 * ------------------------------------------
 * ArrayValue array {
 *                     nullptr,
 *                     true,
 *                     "string",
 *                     {"b", true}, // this will be treated as array type
 *                     {{"null", nullptr}} // this will be treated as object type
 *                  }
 * ------------------------------------------
 * ArrayValue array {
 *                      {"null", nullptr},
 *                      {"b", true},
 *                      {"s", "hello"}
 *                  }
 * ------------------------------------------
 * ArrayValue array;
 * array.PushBack();
 * array.PushBack(true); // or array.PushBack().SetBool(true);
 * array.PushBack("hello"); // or array.PushBack().SetString("hello");
 * array.PushBack() = {{"b", true}}; // or array.PushBack().SetObject().Add("b", true)
 * for (auto &node : array) {
 *     // node type is Fit::Value
 * }
 * ------------------------------------------
 */
class ArrayValue {
public:
    using ConstIterator = ConstValueIterator;
    using Iterator = ValueIterator;

    class Impl;

    ArrayValue();
    ArrayValue(const ArrayValue &);
    ArrayValue(ArrayValue &&) noexcept;
    ArrayValue &operator = (const ArrayValue &);
    ArrayValue &operator = (ArrayValue &&) noexcept;
    ArrayValue(std::initializer_list<Value> init);
    ~ArrayValue();
    /* *
     * get Value with index
     * if index is over range in object, throw std::out_of_range
     * @param index value index
     * @return value
     */
    Value &operator[](uint32_t index);
    const Value &operator[](uint32_t index) const;
    /* *
     * @return value count
     */
    uint32_t Size() const;
    /* *
     * @return Size() == 0
     */
    bool Empty() const;
    /* *
     * release all values
     */
    void Clear();
    /* *
     * prepare capacity to store values
     * @param size the capacity that the array at least need to have
     * @return self
     */
    ArrayValue &Reserve(uint32_t size);
    /* *
     * add a null type value to array
     * @return added value
     */
    Value &PushBack();
    /* *
     * add a bool type value to array
     * @param value bool value
     * @return self
     */
    ArrayValue &PushBack(bool value);
    /* *
     * add a int32 type value to array
     * @param value int32 value
     * @return self
     */
    ArrayValue &PushBack(int32_t value);
    /* *
     * add a uint32 type value to array
     * @param value uint32 value
     * @return self
     */
    ArrayValue &PushBack(uint32_t value);
    /* *
     * add a double type value to array
     * @param value double value
     * @return self
     */
    ArrayValue &PushBack(double value);
    /* *
     * add a string type value to array
     * @param value string value
     * @return self
     */
    ArrayValue &PushBack(const char *value);
    /* *
     * remove the back value
     * @return self
     */
    ArrayValue &PopBack();
    /* *
     * remove the value in the index, if index is not exist, do nothing
     * @param index value index
     * @return self
     */
    ArrayValue &Remove(uint32_t index);
    ArrayValue &Remove(Iterator iter);

    ConstIterator Begin() const;
    ConstIterator End() const;
    Iterator Begin();
    Iterator End();

    ConstIterator begin() const
    {
        return Begin();
    }

    ConstIterator end() const
    {
        return End();
    }

    Iterator begin()
    {
        return Begin();
    }

    Iterator end()
    {
        return End();
    }

private:
    std::unique_ptr<Impl> impl_;
};
}
#endif // FIT_VALUE_HPP
