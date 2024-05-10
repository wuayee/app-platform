/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : utility
 * Author       : songyongtan
 * Date         : 2021/4/13
 */

#ifndef FIT_UTILITY_HPP
#define FIT_UTILITY_HPP

#include <utility>

namespace Fit {
template<typename T>
struct identity {
    using type = T;
};

template<typename T>
using identity_t = typename identity<T>::type;

struct in_place_t {
};

template<class=void>
struct internal_inline_variable_holder_in_place {
    static constexpr identity_t<in_place_t> instance{};
};
template <class internal_dummy>
constexpr identity_t<in_place_t> internal_inline_variable_holder_in_place<internal_dummy>::instance;
static constexpr const identity_t<in_place_t> &in_place = internal_inline_variable_holder_in_place<>::instance;
static_assert(sizeof(void (*)(decltype(in_place))) != 0, "silence unused variable warnings.");

template<typename T>
struct in_place_type_tag {
    explicit in_place_type_tag() = delete;
    in_place_type_tag(const in_place_type_tag&) = delete;
    in_place_type_tag& operator=(const in_place_type_tag&) = delete;
};

template<size_t I>
struct in_place_index_tag {
    explicit in_place_index_tag() = delete;
    in_place_index_tag(const in_place_index_tag&) = delete;
    in_place_index_tag& operator=(const in_place_index_tag&) = delete;
};

template<typename T>
using in_place_type_t = void (*)(in_place_type_tag<T>);

template<typename T>
void in_place_type(in_place_type_tag<T>) {}

template<typename... Ts>
struct conjunction : public std::true_type {
};

template<typename T, typename... Ts>
struct conjunction<T, Ts...>
    : public std::conditional<T::value, conjunction<Ts...>, T>::type {
};

template<typename T>
struct conjunction<T> : public T {
};

template<typename... Ts>
struct disjunction : public std::false_type {
};

template<typename T, typename... Ts>
struct disjunction<T, Ts...> :
    public std::conditional<T::value, T, disjunction<Ts...>>::type {
};

template<typename T>
struct disjunction<T> : public T {
};

template<typename T>
struct negation : public std::integral_constant<bool, !T::value> {
};

template<typename T>
struct is_reference_wrapper : public std::false_type {
};

template<typename T>
struct is_reference_wrapper<std::reference_wrapper<T>> : public std::true_type {
};

template<typename T, T... Ints>
struct integer_sequence {
    using value_type = T;

    static constexpr size_t size() noexcept
    {
        return sizeof...(Ints);
    }
};

template<size_t... Ints>
struct index_sequence {
};

template<std::size_t N, std::size_t... Next>
struct index_sequence_helper : public index_sequence_helper<N - 1U, N - 1U, Next...> {
};

template<std::size_t... Next>
struct index_sequence_helper<0U, Next...> {
    using type = index_sequence<Next...>;
};

template<std::size_t N>
using make_index_sequence = typename index_sequence_helper<N>::type;

template<typename ForwardIterator, typename Eq>
bool exist(ForwardIterator first, ForwardIterator last, const Eq& eq)
{
    while (first != last) {
        if (eq(*first)) {
            return true;
        }
        ++first;
    }

    return false;
}
}
#endif // FITUTILITY_HPP
