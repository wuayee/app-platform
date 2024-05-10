/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/17 21:39
 * Notes        :
 */

#ifndef FORMATTER_HELPER_HPP
#define FORMATTER_HELPER_HPP

namespace Fit {
namespace Framework {
namespace Formatter {
template<typename T>
inline constexpr T &ExtractArgToRef(T& arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return arg;
}

template<typename T>
inline constexpr T& ExtractArgToRef(T* arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return *arg;
}

template<typename T>
inline constexpr T& ExtractArgToRef(T** arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return **arg;
}

template<typename T>
inline constexpr bool IsNullArg(T& arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return false;
}

template<typename T>
inline constexpr bool IsNullArg(T* arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return arg == nullptr;
}

template<typename T>
inline constexpr bool IsNullArg(T** arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return arg == nullptr || *arg == nullptr;
}

template<typename T>
inline constexpr T* ExtractArgToPointer(T* arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return arg;
}

template<typename T>
inline constexpr T* ExtractArgToPointer(T** arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return *arg;
}

template<typename T>
inline T* CreateArg(ContextObj ctx, T*& arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    arg = Fit::Context::NewObj<T>(ctx);
    if (!arg) {
        return nullptr;
    }

    return arg;
}

template<typename T>
inline T** CreateArg(ContextObj ctx, T**& arg)
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    arg = Fit::Context::NewObj<T*>(ctx);
    if (!arg) {
        return nullptr;
    }
    *arg = Fit::Context::NewObj<T>(ctx);
    if (!(*arg)) {
        return nullptr;
    }

    return arg;
}
}
}
}

#endif // FORMATTERHELPER_H
