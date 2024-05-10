/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/4/15
 * Notes:       :
 */

#ifndef RUNTIME_ELEMENT_HPP
#define RUNTIME_ELEMENT_HPP

#include <fit/stl/string.hpp>

#define DISABLE_MOVE_AND_COPY_CONSTRUCTOR(CLASS) \
    CLASS(CLASS&&) = delete;                      \
    CLASS(const CLASS&) = delete;                  \
    CLASS& operator=(CLASS&&) = delete;                      \
    CLASS& operator=(const CLASS&) = delete;

namespace Fit {
class Runtime;
class RuntimeElement {
public:
    RuntimeElement() = default;
    virtual ~RuntimeElement() = default;
    DISABLE_MOVE_AND_COPY_CONSTRUCTOR(RuntimeElement);
    /**
     * 元素的启动入口，在这里可以从runtime获取依赖的元素，缺少必须的元素时应返回false
     * @return 成功返回true, 其它返回false
     */
    virtual bool Start() = 0;
    /**
     * 这里清理资源，释放线程等
     *
     * @return 正常释放资源后返回true, 非预期行为返回false
     */
    virtual bool Stop() = 0;
    /**
     * 是否已经完成启动
     * @return true-已经启动，false-未启动
     */
    virtual bool IsStarted() = 0;
    /**
     * 设置是否启动成功
     * @param v true-已经启动，false-未启动
     */
    virtual void SetStarted(bool v) = 0;
    /**
     * 获取元素的名称，目前只是用于区分模块方便定位信息
     * @return 元素的名称
     */
    virtual const string& GetName() const = 0;
    /**
     * 获取关联的runtime
     * @return runtime
     */
    virtual Runtime& GetRuntime() = 0;
    /**
     * 设置关联的runtime，元素通过Start启动前必须要设置可用的runtime
     * @return runtime
     * @note 未设置runtime时通过 @ref GetRuntime读取runtime将引起程序运行异常
     */
    virtual void SetRuntime(Runtime& runtime) = 0;
};

class RuntimeElementBase : public RuntimeElement {
public:
    RuntimeElementBase(Runtime* runtime, const string& name) : runtime_(runtime), name_(name) {}
    explicit RuntimeElementBase(const string& name) : runtime_(nullptr), name_(name) {}

    ~RuntimeElementBase() override = default;
    DISABLE_MOVE_AND_COPY_CONSTRUCTOR(RuntimeElementBase);

    bool Start() override
    {
        isStarted_ = true;
        return true;
    }
    bool Stop() override { return true; }
    bool IsStarted() override { return isStarted_; }
    void SetStarted(bool v) override { isStarted_ = v; }
    const string& GetName() const final
    {
        return name_;
    }

    Runtime& GetRuntime() final;
    void SetRuntime(Runtime& runtime) final;

private:
    Runtime* runtime_ {};
    string name_;
    bool isStarted_ {false};
};
}
#endif // RUNTIME_ELEMENT_HPP
