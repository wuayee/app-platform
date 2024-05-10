/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : Provides definitions for exceptions.
 * Author       : liangjishi 00298979
 * Date         : 2022/02/24
 */

#ifndef FIT_EXCEPTION_HPP
#define FIT_EXCEPTION_HPP

#include <fit/stl/string.hpp>

#include <exception>

namespace Fit {
/**
 * 为应用程序提供异常定义。
 */
class Exception : public std::exception {
public:
    /**
     * 使用错误码初始化 ::Fit::Exception 类的新实例。
     *
     * @param message 表示错误信息的字符串。
     */
    explicit Exception(::Fit::string message);

    /**
     * 释放异常占用的所有资源。
     */
    ~Exception() override = default;

    /**
     * 获取异常信息。
     *
     * @return 表示异常信息的字符串。
     */
    const ::Fit::string& GetMessage() const noexcept;

    const char* what() const noexcept override;
private:
    ::Fit::string message_;
};

/**
 * 当输入参数不符合要求时抛出的异常。
 */
class IllegalArgumentException : public Exception {
public:
    /**
     * 使用错误码初始化 ::Fit::IllegalArgumentException 类的新实例。
     *
     * @param message 表示错误信息的字符串。
     */
    explicit IllegalArgumentException(::Fit::string message);
    ~IllegalArgumentException() override = default;
};

/**
 * 当状态不正确时引发的异常。
 */
class IllegalStateException : public Exception {
public:
    /**
     * 使用错误码初始化 ::Fit::IllegalStateException 类的新实例。
     *
     * @param message 表示错误信息的字符串。
     */
    explicit IllegalStateException(::Fit::string message);
    ~IllegalStateException() override = default;
};
}

#endif // FIT_EXCEPTION_HPP
