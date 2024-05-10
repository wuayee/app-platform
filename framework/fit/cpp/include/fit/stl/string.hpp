/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/13
 * Notes:       :
 */

#ifndef FIT_STRING_HPP
#define FIT_STRING_HPP

#include <string>
#include <utility>
#include <functional>
#include <fit/stl/stl_allocator.hpp>
#include "except.hpp"

namespace Fit {
template<
    class CharT,
    class Traits = std::char_traits<CharT>,
    class Allocator = Fit::stl_allocator<CharT>>
using basic_string = std::basic_string<CharT, Traits, Allocator>;

using string = Fit::basic_string<char>;

inline std::string to_std_string(const Fit::string &str)
{
    return std::string(str.data(), str.size());
}

inline Fit::string to_fit_string(const std::string &str)
{
    return Fit::string(str.data(), str.size());
}

template<class T>
Fit::string to_string(T v)
{
    return to_fit_string(std::to_string(v));
}

// 21.4 Numeric Conversions [string.conversions].
inline int stoi(const Fit::string &str)
{
    return std::stoi(std::string(str.data(), str.size()));
}

inline long stol(const string &str)
{
    return std::stol(std::string(str.data(), str.size()));
}

inline long long stoll(const string &str)
{
    return std::stoll(std::string(str.data(), str.size()));
}

inline unsigned long stoul(const string &str)
{
    return std::stoul(std::string(str.data(), str.size()));
}

inline unsigned long long stoull(const string &str)
{
    return std::stoull(std::string(str.data(), str.size()));
}

struct bytes {
public:
    bytes() = default;
    bytes(const Fit::string &data) : data_(data) {}
    bytes(Fit::string &&data) : data_(std::move(data)) {}
    bytes(Fit::bytes &&other) noexcept : data_(std::move(other.data_)) {}
    bytes(const Fit::bytes &other) = default;
    bytes(const char *buffer, uint32_t len) : data_(buffer, len) {}
    bytes &operator = (Fit::bytes &&other) noexcept
    {
        data_ = std::move(other.data_);
        return *this;
    }
    bytes &operator = (const Fit::bytes &other) = default;
    bytes &operator = (const Fit::string &data)
    {
        data_ = data;
        return *this;
    }
    bytes &operator = (Fit::string &&data) noexcept
    {
        data_ = std::move(data);
        return *this;
    }
    operator Fit::string()
    {
        return data_;
    }
    operator Fit::string() const
    {
        return data_;
    }
    bool empty() const
    {
        return data_.empty();
    }
    size_t size() const
    {
        return data_.size();
    }
    const char *data() const
    {
        return data_.data();
    }
    bool operator == (const bytes &other) const
    {
        return data_ == other.data_;
    }
    bool operator < (const bytes &other) const
    {
        return data_ < other.data_;
    }

private:
    Fit::string data_;
};
}
#ifdef USE_CUSTOM_MEMORY_STRUCTURE
namespace std {
template<>
struct hash<Fit::string> {
    std::size_t operator()(Fit::string const& s) const noexcept
    {
        return std::hash<std::string>{}(Fit::to_std_string(s));
    }
};
}
#endif
#endif // FITSTRING_HPP
