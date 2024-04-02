/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
* Description  : Provides definitions for databus exceptions.
*/

#ifndef DATABUS_EXCEPTION_H
#define DATABUS_EXCEPTION_H

#include <exception>
#include <string>
#include <utility>

namespace DataBus {
namespace Common {

/**
* 为应用程序提供异常定义。
 */
class DataBusException : public std::exception {
public:
    ~DataBusException() override = default;

    explicit DataBusException(std::string message) noexcept : message_(std::move(message))
    {
    }

    const std::string& GetMessage() const noexcept
    {
        return message_;
    }

    const char* what() const noexcept override
    {
        return message_.c_str();
    }

private:
    std::string message_;
};

/**
* 当消息头不符合要求时抛出的异常。
 */
class IllegalMessageHeaderException : public DataBusException {
public:
    explicit IllegalMessageHeaderException(std::string message) noexcept : DataBusException(std::move(message))
    {
    }
    ~IllegalMessageHeaderException() override = default;
};

/**
* 当消息体不符合要求时抛出的异常。
 */
class IllegalMessageBodyException : public DataBusException {
public:
    explicit IllegalMessageBodyException(std::string message) noexcept : DataBusException(std::move(message))
    {
    }
    ~IllegalMessageBodyException() override = default;
};

/**
* 当内存IO出现问题时抛出的异常。
 */
class MemoryIOException : public DataBusException {
public:
    explicit MemoryIOException(std::string message) noexcept : DataBusException(std::move(message))
    {
    }
    ~MemoryIOException() override = default;
};
} // Common
} // DataBus
#endif // DATABUS_EXCEPTION_H
