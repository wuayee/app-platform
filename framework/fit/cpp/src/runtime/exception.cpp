/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : Provides implementation for exceptions.
 * Author       : liangjishi 00298979
 * Date         : 2022/02/24
 */

#include "include/fit/internal/exception.hpp"

using namespace ::Fit;

Exception::Exception(string message) : message_(std::move(message))
{
}

const string& Exception::GetMessage() const noexcept
{
    return message_;
}

const char* Exception::what() const noexcept
{
    return message_.c_str();
}

IllegalArgumentException::IllegalArgumentException(string message) : Exception(std::move(message))
{
}

IllegalStateException::IllegalStateException(string message) : Exception(std::move(message))
{
}