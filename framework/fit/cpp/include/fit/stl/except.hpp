/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : for exceptions
 * Author       : songyongtan
 * Date         : 2022/3/18
 * Notes:       :
 */

#ifndef FIT_EXCEPT_HPP
#define FIT_EXCEPT_HPP

#ifndef __cpp_exceptions
#ifdef __EXCEPTIONS
#define __cpp_exceptions 199711
#endif
#endif

namespace Fit {
void ThrowInvalidArgument(const char* msg);
void ThrowOutOfRange(const char* msg);
}

#define FIT_THROW_INVALID_ARGUMENT(msg) ::Fit::ThrowInvalidArgument(msg)
#define FIT_THROW_OUT_OF_RANGE(msg) ::Fit::ThrowOutOfRange(msg)

#ifdef __cpp_exceptions
    #define FIT_THROW_EXCEPTION0(ExceptType) throw (ExceptType)
    #define FIT_THROW_EXCEPTION1(ExceptType, a1) throw ExceptType(a1)
    #define FIT_THROW_EXCEPTION2(ExceptType, a1, a2) throw ExceptType(a1, a2)
#else
    #define FIT_EXCEPTION_ABORT std::abort()
    #define FIT_THROW_EXCEPTION0(ExceptType) FIT_EXCEPTION_ABORT
    #define FIT_THROW_EXCEPTION1(ExceptType, a1) FIT_EXCEPTION_ABORT
    #define FIT_THROW_EXCEPTION2(ExceptType, a1, a2) FIT_EXCEPTION_ABORT
#endif

#endif // EXCEPT_HPP
