/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: adding make_unique for c++11
 */
#ifndef DATABUS_MEMORY_H
#define DATABUS_MEMORY_H

#include <memory>

namespace DataBus {
#if __cplusplus >= 201304L
    using make_unique = std::make_unique;
#else
    #include <type_traits>
    namespace Detail {
        template<typename>
        struct is_unbounded_array : std::false_type {};
        template<typename T>
        struct is_unbounded_array<T[]> : std::true_type {};

        template<typename T>
        struct is_bounded_array : std::false_type {};
        template<typename T, std::size_t N>
        struct is_bounded_array<T[N]> : std::true_type {};

        template<bool B, typename T = void>
        using enable_if_t = typename std::enable_if<B, T>::type;
    }  // namespace Detail

    template<class T, class... Args>
    auto make_unique(Args&&... args) -> Detail::enable_if_t<!std::is_array<T>::value, std::unique_ptr<T>>
    {
        return std::unique_ptr<T>(new T(std::forward<Args>(args)...));
    }

    template<class T>
    auto make_unique(std::size_t n) -> Detail::enable_if_t<Detail::is_unbounded_array<T>::value, std::unique_ptr<T>>
    {
        return std::unique_ptr<T>(new typename std::remove_extent<T>::type[n]());
    }

    template<class T, class... Args>
    typename std::enable_if<Detail::is_bounded_array<T>::value>::type make_unique(Args&&...) = delete;
#endif
}

#endif  // DATABUS_MEMORY_H
