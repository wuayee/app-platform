/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-01 11:02:39
 */

#ifndef FIT_STRING_UTIL_H
#define FIT_STRING_UTIL_H

#include <algorithm>
#include <iterator>
#include <sstream>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

inline bool is_whitespace(char ch)
{
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
}

template<typename Pattern>
inline bool begins_with(const Fit::string& src, const Pattern& pattern)
{
    return src.rfind(pattern, 0) != Fit::string::npos;
}

inline void ltrim(Fit::string& str)
{
    str.erase(str.begin(), std::find_if(str.begin(), str.end(), [](unsigned char c) { return !std::isspace(c); }));
}

inline void rtrim(Fit::string& str)
{
    str.erase(
        std::find_if(str.rbegin(), str.rend(), [](unsigned char c) { return !std::isspace(c); }).base(), str.end());
}

inline void trim(Fit::string& str)
{
    ltrim(str);
    rtrim(str);
}

inline Fit::vector<Fit::string> SplitString(const Fit::string& s, char deli = ' ')
{
    std::istringstream iss(Fit::to_std_string(s));
    auto res = Fit::vector<Fit::string> {};
    auto token = Fit::string {};

    while (getline(iss, token, deli)) {
        res.push_back(token);
    }

    return res;
}

template<typename Container>
inline Fit::string join_to_string(const Container& con, const char* delimiter)
{
    if (con.empty()) {
        return "";
    }

    std::ostringstream ss;
    std::copy(con.begin(), con.end() - 1, std::ostream_iterator<typename Container::value_type>(ss, delimiter));
    ss << *con.rbegin();

    return Fit::to_fit_string(ss.str());
}

inline Fit::string pointer_to_string(void* p)
{
    std::stringstream ss;
    ss << p;
    return Fit::to_fit_string(ss.str());
}

template<typename SubstrType>
inline bool contains_substr(const Fit::string& source_str, const SubstrType& substr)
{
    return source_str.find(substr) != Fit::string::npos;
}

inline Fit::string format_string_to_hex(const Fit::string &data)
{
    std::ostringstream result;
    std::for_each(data.begin(), data.end(), [&result](const char &item) { result << std::hex << item; });
    return Fit::to_fit_string(result.str());
}

inline bool is_number(const Fit::string& data)
{
    for (const auto &item : data) {
        if (!isdigit(item)) {
            return false;
        }
    }
    return true;
}

inline Fit::string& string_replace(Fit::string& s, char src, char dst)
{
    for (auto &c : s) {
        if (c == src) {
            c = dst;
        }
    }

    return s;
}
#endif
