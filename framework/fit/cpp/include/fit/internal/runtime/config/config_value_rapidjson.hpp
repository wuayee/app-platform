/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#ifndef CONFIG_VALUE_RAPIDJSON_HPP
#define CONFIG_VALUE_RAPIDJSON_HPP

#include <fit/external/runtime/config/config_value.hpp>
#include <rapidjson/document.h>
#include <map>
#include <string>
#include <memory>

namespace Fit {
namespace Config {
class ValueRapidJson : public Value {
public:
    ValueRapidJson() = default;
    ValueRapidJson(rapidjson::Value *jsonValue, rapidjson::Document *document);
    ~ValueRapidJson() override;

    /**
     * 只有Array类型才可使用
     * @param index
     * @return 返回对应位置的值, 不存在时返回value IsNull为true
     */
    Value &operator[](int32_t index) override;

    /**
     * 只有Object类型才可使用
     * @param key
     * @return 返回对应的值, 不存在时返回value IsNull为true
     */
    Value &operator[](const char *key) override;

    ValueType GetType() const override;
    Fit::vector<Fit::string> GetKeys() const override;

    /**
     * 返回Array的大小
     * @return
     */
    int32_t Size() const override;

    // 类型不匹配时抛出std::runtime_error异常
    bool AsBool() const override;
    int32_t AsInt() const override;
    double AsDouble() const override;
    string AsString() const override;

    // 失败时返回默认值
    bool AsBool(bool defaultValue) const override;
    int32_t AsInt(int32_t defaultValue) const override;
    double AsDouble(double defaultValue) const override;
    string AsString(const char *defaultValue) const override;
    string AsString(const string& defaultValue) const override;

    bool IsNull() const override;
    bool IsBool() const override;
    bool IsInt() const override;
    bool IsDouble() const override;
    bool IsString() const override;
    bool IsObject() const override;
    bool IsArray() const override;

    /**
     * 清空对象，后续IsNull为true
     * @return 自身
     */
    ValueRapidJson &SetNull();
    /**
     * 更改为bool类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    ValueRapidJson &SetValue(bool val);
    /**
     * 更改为int类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    ValueRapidJson &SetValue(int32_t val);
    /**
     * 更改为double类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    ValueRapidJson &SetValue(double val);
    /**
     * 更改为字符串类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    ValueRapidJson &SetValue(const char *val);
    /**
     * 更改为object类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    ValueRapidJson &SetObject();
    /**
     * 添加name节点
     * @param name
     * @return 返回新增节点
     */
    ValueRapidJson &AddMember(const char *name);
    /**
     * 查找name节点
     * @param name
     * @return 返回查找到的节点，未查到是返回节点IsNull方法为true
     */
    ValueRapidJson &FindMember(const char *name);
    /**
     * 更改为数组类型节点, 如果之前是其它类型则数据被清空
     * @param reserveSize
     * @return 自身
     */
    ValueRapidJson &SetArray();
    ValueRapidJson &Reserve(int32_t reserveSize);
    /**
     * 数组中添加新节点
     * @return 返回新增的节点
     */
    ValueRapidJson &PushBack();

    static ValueRapidJson nullValue_;
protected:
    void Clear();

private:
    rapidjson::Document *document_ {};
    rapidjson::Value *value_ {};
    std::map<std::string, std::unique_ptr<ValueRapidJson>> objects_;
    std::map<uint32_t, std::unique_ptr<ValueRapidJson>> arrays_;
};
}
}
#endif // CONFIG_VALUE_RAPIDJSON_HPP
