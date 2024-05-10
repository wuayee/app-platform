/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : Provides utility methods for strings.
 * Author       : songyongtan 00558940
 * Date         : 2022/02/24
 */

#ifndef FIT_STRING_UTILS_HPP
#define FIT_STRING_UTILS_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

#include <functional>
#include <sstream>

namespace Fit {
/**
 * 为字符串提供工具类。
 */
class StringUtils {
public:
    StringUtils() = delete;

    /**
     * 检查指定的字符串是否不为空字符串。
     *
     * @param s 表示待检查的字符串。
     * @return 若字符串不是空字符串，则为 true；否则为 false。
     */
    static bool NotEmpty(const ::Fit::string& s);

    /**
     * 将字符串转为32位整数。
     *
     * @param value 表示待转为32位整数的字符串。
     * @return 表示从字符串中获取到的32位整数。
     */
    static int32_t ToInt32(const ::Fit::string& value);

    /**
     * 将指定的64位无符号整数转为字符串表现形式。
     *
     * @param value 表示待转为16进制字符串表现形式的64位无符号整数。
     * @return 表示该64位无符号整数的字符串表现形式。
     */
    static ::Fit::string ToHexString(uint64_t value);

    /**
     * 将bytes中的16进制值转换为字符串，每个字节->两个字符
     * @param value 待转换的二进制buffer
     * @return
     */
    static ::Fit::string ToHexString(const ::Fit::string& value);

    /**
     * 计算指定字符串的哈希值。
     *
     * @param value 表示待计算哈希值的字符串。
     * @return 表示字符串的哈希值。
     */
    static size_t ComputeHash(const ::Fit::string& value);

    /*
     * 分割字符串，返回子串集合。
     *
     * @param s 表示待分割的原始字符串。
     * @param separator 表示分割符。
     * @return 表示包含分割得到的子串的集合。
     */
    static ::Fit::vector<::Fit::string> Split(const ::Fit::string& s, char separator);

    /**
     * 分割字符串，并对得到的字符串进行过滤，返回符合条件的子串集合。
     *
     * @param s 表示待分割的原始字符串。
     * @param separator 表示分割符。
     * @param filter 表示用以过滤子串的方法。
     * @return 表示包含分割得到的子串的集合。
     */
    static ::Fit::vector<::Fit::string> Split(
        const ::Fit::string& s, char separator, const std::function<bool(const ::Fit::string&)>& filter);

    /**
     * 格式化字符串。
     *
     * @param fmt 表示字符串的格式化模板。
     * @param ... 表示格式化参数的列表。
     * @return 表示格式化后的字符串。
     */
    static ::Fit::string Format(const char* fmt, ...) __attribute__((format(printf, 1, 2)));

    /**
     * 格式化字符串。
     *
     * @param fmt 表示字符串的格式化模板。
     * @param args 表示格式化参数的列表。
     * @return 表示格式化后的字符串。
     */
    static ::Fit::string Format(const char* fmt, va_list args);

    /**
     * 使用指定的连接符拼接字符串。
     *
     * @param connector 表示待拼接字符串使用的连接符。
     * @param parts 表示待拼接的字符串的集合。
     * @return 表示拼接后的字符串。
     */
    static ::Fit::string Join(char connector, const ::Fit::vector<::Fit::string>& parts);

    /**
     * 将指定的字符串序列以指定的分隔符进行拼接。
     *
     * @param connector 表示拼接时使用的连接符的字符串。
     * @param parts 表示待拼接的字符串的序列。
     * @return 表示拼接后得到的字符串。
     */
    static ::Fit::string Join(const ::Fit::string& connector, const ::Fit::vector<::Fit::string>& parts);

    /**
     * join with custom stringify function
     *
     * @tparam Container
     * @tparam StringifyF sign:void(std::stringstream& ss, const T& v)
     * @param connector
     * @param c container
     * @param f stringify function
     * @return string join result
     */
    template<typename Container, typename StringifyF>
    static string Join(const string& connector, Container&& c, StringifyF&& f)
    {
        std::stringstream ss;
        auto iter = c.begin();
        if (iter != c.end()) {
            f(ss, *iter);
            while (++iter != c.end()) {
                ss << connector;
                f(ss, *iter);
            }
        }
        return Fit::to_fit_string(ss.str());
    }

    /**
     * wrapper string
     *
     * @param b begin string
     * @param e end string
     * @param s target string
     * @return string -> `b + s + e`
     */
    template<typename S>
    static string Wrapper(const string& b, const string& e, S&& s)
    {
        std::stringstream ss;
        ss << b << std::forward<S>(s) << e;
        return Fit::to_fit_string(ss.str());
    }

    /**
     * 替换字符串中子串
     *
     * @param s 源字符串
     * @param target 需要替换的目标字符串
     * @param replaceStr 匹配的字符串替换成该字符串
     * @return 替换后的字符串
     */
    static ::Fit::string Replace(const ::Fit::string& s, const ::Fit::string& target, const ::Fit::string& replaceStr);

    /**
     * 判断字符串是否以目标字符串开头
     *
     * @param s 源字符串
     * @param prefix 待匹配的字符串
     * @return true-成功， false-失败
     */
    static bool StartsWith(const char* s, const char* prefix);
    static bool StartsWith(const ::Fit::string& s, const char* prefix);

    /**
     * 是否是空白字符，如：空格，tab, 换行
     * @param ch 目标字符
     * @return true-是，false-否
     */
    static bool IsBlank(char ch);
};
}
#endif // FIT_STRING_UTILS_HPP
