/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/13
 * Notes:       :
 */

#ifndef FIT_ANY_HPP
#define FIT_ANY_HPP

#include <memory>
#include <fit/memory/fit_base.hpp>
#include "utility.hpp"
#include "memory.hpp"

namespace Fit {
class any;

template<typename ValueType>
ValueType any_cast(const any& a);

template<typename ValueType>
ValueType any_cast(any& a);

template<typename ValueType>
ValueType any_cast(any &&operand);

template<typename T>
const T* any_cast(const any* a) noexcept;

template<typename T>
T* any_cast(any* a) noexcept;

class any {
public:
    template<typename T>
    struct IsInPlaceType;
    constexpr any() noexcept;

    any(const any& other) : obj_(other.has_value() ? other.obj_->clone() : std::unique_ptr<obj>()) {}

    any(any&& other) noexcept = default;

    template<typename T, typename ValueT = typename std::decay<T>::type,
        typename std::enable_if<!disjunction<
            std::is_same<any, ValueT>, IsInPlaceType<ValueT>,
            negation<std::is_copy_constructible<ValueT>>>::value>::type* = nullptr>
    any(T&& value) : obj_(new obj_instance<ValueT>(in_place, std::forward<T>(value))) {}

    template<typename T, typename... Args, typename ValueT = typename std::decay<T>::type,
        typename std::enable_if<conjunction<
            std::is_copy_constructible<ValueT>,
            std::is_constructible<ValueT, Args...>>::value>::type* = nullptr>
    explicit any(in_place_type_t<T>, Args&& ... args)
        : obj_(new obj_instance<ValueT>(in_place, std::forward<Args>(args)...)) {}

    template<
        typename T, typename U, typename... Args, typename ValueT = typename std::decay<T>::type,
        typename std::enable_if<conjunction<
            std::is_copy_constructible<ValueT>,
            std::is_constructible<ValueT, std::initializer_list<U>&, Args...>
        >::value>::type* = nullptr>
    explicit any(in_place_type_t<T>, std::initializer_list<U> ilist,
        Args&& ... args)
        : obj_(new obj_instance<ValueT>(in_place, ilist, std::forward<Args>(args)...)) {}

    any& operator=(const any& rhs)
    {
        any(rhs).swap(*this);
        return *this;
    }

    any& operator=(any&& rhs) noexcept
    {
        any(std::move(rhs)).swap(*this);
        return *this;
    }

    template<typename T, typename ValueT = typename std::decay<T>::type>
    any& operator=(T&& rhs)
    {
        any tmp(in_place_type_t<ValueT>(), std::forward<T>(rhs));
        tmp.swap(*this);
        return *this;
    }

    template<
        typename T, typename... Args, typename ValueT = typename std::decay<T>::type>
    ValueT& emplace(Args&& ... args)
    {
        reset();  // NOTE: reset() is required here even in the world of exceptions.
        auto * const object_ptr =
            new obj_instance<ValueT>(in_place, std::forward<Args>(args)...);
        obj_ = std::unique_ptr<obj>(object_ptr);
        return object_ptr->value;
    }

    template<
        typename T, typename U, typename... Args, typename ValueT = typename std::decay<T>::type>
    ValueT& emplace(std::initializer_list<U> ilist, Args&& ... args)
    {
        reset();  // NOTE: reset() is required here even in the world of exceptions.
        auto * const object_ptr =
            new obj_instance<ValueT>(in_place, ilist, std::forward<Args>(args)...);
        obj_ = std::unique_ptr<obj>(object_ptr);
        return object_ptr->value;
    }

    void reset() noexcept
    {
        obj_ = nullptr;
    }

    void swap(any& other) noexcept
    {
        obj_.swap(other.obj_);
    }

    bool has_value() const noexcept
    {
        return obj_ != nullptr;
    }

    const std::type_info& type() const noexcept
    {
        if (has_value()) {
            return obj_->type();
        }
        return typeid(void);
    }

private:
    class obj {
    public:
        virtual ~obj() = default;
        virtual std::unique_ptr<obj> clone() const = 0;
        virtual const std::type_info& type() const noexcept = 0;
    };

    template<class T>
    class obj_instance : public obj, public FitBase {
    public:
        template<typename... Args>
        explicit obj_instance(in_place_t, Args&& ... args)
            : value_(std::forward<Args>(args)...) {}

        std::unique_ptr<obj> clone() const override
        {
            return make_unique<obj_instance>(in_place, value_);
        }

        const std::type_info& type() const noexcept override
        {
            return typeid(T);
        }

        T value_;
    };

    std::unique_ptr<obj> obj_{};

    template<typename ValueType>
    friend ValueType any_cast(const any& a);
    template<typename ValueType>
    friend ValueType any_cast(any& a);
    template<typename T>
    friend const T* any_cast(const any* a) noexcept;
    template<typename T>
    friend T* any_cast(any* a) noexcept;
};

constexpr any::any() noexcept = default;

template<typename T>
struct any::IsInPlaceType : public std::false_type {
};

template<typename T>
struct any::IsInPlaceType<in_place_type_t<T>> : public std::true_type {
};

class bad_any_cast : public std::bad_cast {
public:
    ~bad_any_cast() override = default;

    const char* what() const noexcept override
    {
        return "bad any cast";
    }
};

void throw_bad_any_cast();

template<typename ValueT>
ValueT any_cast_internal(const any& a)
{
    using RawType = typename std::remove_cv<typename std::remove_reference<ValueT>::type>::type;
    static_assert(std::is_constructible<ValueT, const RawType&>::value, "invalid value type");
    auto * const result = (any_cast<RawType>)(&a);
    if (result == nullptr) {
        throw_bad_any_cast();
    }
    return static_cast<ValueT>(*result);
}

template<typename ValueT>
ValueT any_cast(const any& a)
{
    return any_cast_internal<ValueT>(a);
}

template<typename ValueT>
ValueT any_cast(any& a)
{
    return any_cast_internal<ValueT>(a);
}

template<typename ValueT>
ValueT any_cast(any&& operand)
{
    using RawType = typename std::remove_cv<
        typename std::remove_reference<ValueT>::type>::type;
    static_assert(std::is_constructible<ValueT, RawType>::value, "invalid value type");
    return static_cast<ValueT>(std::move((any_cast<RawType&>)(operand)));
}

template<typename T>
const T* any_cast(const any* a) noexcept
{
    using RawType = typename std::remove_cv<typename std::remove_reference<T>::type>::type;
    return a && a->type() == typeid(RawType) ? std::addressof(
        static_cast<const any::obj_instance<RawType>*>(a->obj_.get())->value_) : nullptr;
}

template<typename T>
T* any_cast(any* a) noexcept
{
    using RawType = typename std::remove_cv<typename std::remove_reference<T>::type>::type;
    return a && a->type() == typeid(RawType) ? std::addressof(
        static_cast<any::obj_instance<RawType>*>(a->obj_.get())->value_) : nullptr;
}
}

#endif // FIT_ANY_HPP
